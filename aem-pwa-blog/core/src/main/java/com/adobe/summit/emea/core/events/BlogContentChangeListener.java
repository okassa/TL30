package com.adobe.summit.emea.core.events;

import com.adobe.summit.emea.core.services.NotificationService;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ExternalResourceChangeListener;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.*;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.day.cq.dam.api.DamConstants.NT_DAM_ASSET;
import static com.day.cq.wcm.api.NameConstants.NT_PAGE;
import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;


/**
 * <h1>BlogContentChangeListener</h1>
 *
 *
 * @author Olympe Kassa
 */

@Component(service = EventHandler.class,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=BlogContent Change Listener - This service will notify a set of users who have suscribe to notifications when an page tagged with one of their hobby " +
                        "has been added to the website",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM",
                EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/*"
        })
public class BlogContentChangeListener implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogContentChangeListener.class);

    private static final String JOB_NAME_PREFIX = BlogContentChangeListener.class.getCanonicalName().replace(".", "/") + "/";

    private final int[] events = {Event.NODE_ADDED,Event.NODE_MOVED,Event.NODE_REMOVED};

    private final Gson gson = new Gson();

    private static final List<String> pathList = Arrays.asList(new String[]{"/content/dam/aem-pwa-blog","/content/aem-pwa-blog"});

    @Reference
    private Scheduler scheduler;

    @Reference
    private NotificationService notificationService;

    @Reference
    ResourceResolverFactory resolverFactory;

    public void handleEvent(final org.osgi.service.event.Event event) {
        LOGGER.debug("Resource event: {} at: {}", event.getTopic(), event.getProperty(SlingConstants.PROPERTY_PATH));
        LOGGER.debug("[onChange] >>>>>> Processing changes ....");

        try(ResourceResolver resolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "pwaWriteUserAccess"))) {


            ScheduleOptions options = scheduler.NOW();
            options.canRunConcurrently(false);

            Predicate<String> isPathAllowed = path -> {
                long c =  pathList.stream()
                        .filter(p -> path.startsWith(p))
                        .count();
                return (c > 0) ? true : false;
            };

            Predicate<String> isResourceTypeAllowed = path ->
                   Optional.of(path)
                        .map(p -> resolver.getResource(p))
                        .filter(r -> r.getResourceType().equals(NT_DAM_ASSET)).isPresent()
                    ||
                    Optional.of(path)
                        .map(p -> resolver.getResource(p))
                        .filter(r -> r.getResourceType().equals(NT_PAGE)).isPresent();

            Optional.of(event)
                    .map(e -> String.valueOf(e.getProperty(SlingConstants.PROPERTY_PATH)))
                    .filter(s -> s != null &&  !s.isEmpty())
                    .filter(isPathAllowed)
                    .ifPresent(p -> {
                        LOGGER.trace("[onChange] ---  Processing changes for path '{}'", p);
                        String jobName = JOB_NAME_PREFIX + p;
                        options.name(jobName);
                        Resource resource = resolver.resolve(p);
                        TagManager tagManager = resolver.adaptTo(TagManager.class);
                        Tag[] tags = tagManager.getTags(resource);
                        List<String> tagNames = Arrays.asList(tags).stream().map(t -> t.getName().toLowerCase()).collect(Collectors.toList());
                        ImmediateJob job = new ImmediateJob(tagNames,p);
                        LOGGER.debug("[onChange] ---  Adding job '{}' for path '{}'", jobName, p);
                        scheduler.schedule(job, options);


                    });
            LOGGER.trace("[onChange] <<<<<< Processing changes....DONE");

        } catch (LoginException e) {
            LOGGER.error("An error occured when opening the resource resolver {}",e);
        }


    }

    private class ImmediateJob implements Runnable {
        private final List<String> tags;
        private final String path;

        /**
         * The constructor can be used to pass in serializable state that will be used during the Job processing.
         *
         *
         */
        public ImmediateJob(List<String> tags,String path) {
            // Maintain job state
            this.tags = tags;
            this.path = path;
        }

        /**
         * Run is the entry point for initiating the work to be done by this job.
         * The Sling job management mechanism will call run() to process the job.
         */
        public void run() {
            LOGGER.debug("[run] Start processing ---  The path is '{}'", tags);
            // Get the tags from the asset or the page

            try(ResourceResolver resolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "pwaWriteUserAccess"))) {


                if(tags.size() > 0){
                    tags.stream().forEach(t -> {
                        HashMap<String,String> bodyMap = new HashMap<>();
                        bodyMap.put("path",StringUtils.substringBefore(path,"/jcr:content/metadata"));
                        bodyMap.put("message","Just because you like this topic : "+t+", we would like to share with you this new resource");

                        notificationService.sendTopicMessage("Adobe Summit EMEA 2019 - TL30", gson.toJson(bodyMap),t);
                    });
                }

            } catch (Exception e) {
               LOGGER.error("An error occured when executing the job for path {}",tags);
            }
            ;

            LOGGER.debug("[run] End processing ---  The path is '{}'", tags);
        }
    }

}
