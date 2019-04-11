package com.adobe.summit.emea.core.events;

import com.adobe.summit.emea.core.services.NotificationService;
import com.day.cq.dam.api.DamConstants;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ExternalResourceChangeListener;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

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
        configurationPolicy = ConfigurationPolicy.REQUIRE,
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

    private static final List<String> pathList = Arrays.asList(new String[]{"/content/dam/aem-pwa-blog","/content/aem-pwa-blog"});

    @Reference
    private Scheduler scheduler;

    @Reference
    NotificationService notificationService;

    @Reference
    ResourceResolverFactory resolverFactory;

    public void handleEvent(final org.osgi.service.event.Event event) {
        LOGGER.debug("Resource event: {} at: {}", event.getTopic(), event.getProperty(SlingConstants.PROPERTY_PATH));
        LOGGER.debug("[onChange] >>>>>> Processing changes ....");

        try {
            ResourceResolver resolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "pwaWriteUserAccess"));

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
                    .filter(isResourceTypeAllowed)
                    .ifPresent(p -> {
                        LOGGER.trace("[onChange] ---  Processing changes for path '{}'", p);
                        String jobName = JOB_NAME_PREFIX + p;
                        options.name(jobName);
                        ImmediateJob job = new ImmediateJob(p);
                        LOGGER.debug("[onChange] ---  Adding job '{}' for path '{}'", jobName, p);
                        scheduler.schedule(job, options);
                    });
            LOGGER.trace("[onChange] <<<<<< Processing changes....DONE");

        } catch (LoginException e) {
            LOGGER.error("An error occured when opening the resource resolver {}",e);
        }


    }

    private class ImmediateJob implements Runnable {
        private final String path;

        /**
         * The constructor can be used to pass in serializable state that will be used during the Job processing.
         *
         * @param path example parameter passed in from the event
         */
        public ImmediateJob(String path) {
            // Maintain job state
            this.path = path;
        }

        /**
         * Run is the entry point for initiating the work to be done by this job.
         * The Sling job management mechanism will call run() to process the job.
         */
        public void run() {
            LOGGER.debug("[run] Start processing ---  The path is '{}'", path);
            // Get the tags from the asset or the page

            try {
               // notificationService.sendTopicMessage("Summit Lab EH", "A new picture "+path+" has been uploaded to the blog, we know you might be inetrested");
            } catch (Exception e) {
               LOGGER.error("An error occured when executing the job for path {}",path);
            }
            ;

            LOGGER.debug("[run] End processing ---  The path is '{}'", path);
        }
    }

}
