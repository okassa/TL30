package com.adobe.summit.emea.core.events;

import com.adobe.summit.emea.core.services.NotificationService;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ExternalResourceChangeListener;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
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
import java.util.function.Function;
import java.util.function.Predicate;

import static org.apache.sling.api.resource.ResourceResolverFactory.SUBSERVICE;


/**
 * <h1>BlogContentChangeListener</h1>
 *
 * <p>
 * This listener will be executed only on publish and relay runmodes.
 * It will do the following tasks :
 * <ul>
 * <li>Reverse replicate the replication tracer node beneath /var/replication-tracer</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>exemple :</b><br>
 * No example, it will be trigered when an event occured.
 * </p>
 *
 * @author Gregory Stievenard  &lt;stievena@adobe.com&gt;
 */
@Component(
        immediate = true,
        service = EventListener.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Adobe Summit EMEA 2019 - TL30 Event listener used for newly created nodes",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA"
        }
)
public class BlogContentChangeListener implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogContentChangeListener.class);

    private static final String JOB_NAME_PREFIX = BlogContentChangeListener.class.getCanonicalName().replace(".", "/") + "/";

    private final int[] events = {Event.NODE_ADDED,Event.NODE_MOVED,Event.NODE_REMOVED};

    private static final List<String> pathList = Arrays.asList(new String[]{"/content/dam/aem-pwa-blog","/content/aem-pwa-blog"});

    @Reference
    private Scheduler scheduler;

   // @Reference
   // NotificationService notificationService;

    @Override
    public void onEvent(final EventIterator events) {

      List<Event> eventList =  IteratorUtils.toList(events);
        LOGGER.trace("[onChange] >>>>>> Processing changes ....");

        ScheduleOptions options = scheduler.NOW();
        options.canRunConcurrently(false);

        Predicate<String> isPathAllowed = path -> {
          long c =  pathList.stream()
                    .filter(p -> path.startsWith(p))
                    .count();
            return (c > 0) ? true : false;
        };

        eventList.stream()
                .map(c -> {
                    try {
                        return c.getPath();
                    } catch (RepositoryException e) {
                        return "";
                    }
                })
                .filter(s -> !s.isEmpty())
                .filter(isPathAllowed)
                .forEach(p -> {
                    LOGGER.trace("[onChange] ---  Processing changes for path '{}'", p);
                    String jobName = JOB_NAME_PREFIX + p;
                    options.name(jobName);
                    ImmediateJob job = new ImmediateJob(p);
                    LOGGER.debug("[onChange] ---  Adding job '{}' for path '{}'", jobName, p);
                    scheduler.schedule(job, options);
                });
        LOGGER.trace("[onChange] <<<<<< Processing changes....DONE");
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

            //notificationService.sendMessage("Summit Lab EH", "A new picture "+path+" has been uploaded to the blog, we know you might be inetrested","to-summit");;

            LOGGER.debug("[run] End processing ---  The path is '{}'", path);
        }
    }

}
