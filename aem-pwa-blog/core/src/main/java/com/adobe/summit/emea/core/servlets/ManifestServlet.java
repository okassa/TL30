/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.summit.emea.core.servlets;

import com.day.cq.commons.jcr.JcrConstants;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service=Servlet.class,
           property={
                   Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
                   "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                   "sling.servlet.resourceTypes="+ "aem-pwa-blog/components/structure/page",
                   "sling.servlet.selectors=" + "manifest",
                   "sling.servlet.extensions=" + "json"
           })
@Designate(ocd = ManifestServlet.Configuration.class)
public class ManifestServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private Gson gson = new Gson();

    private HashMap<String,Object> manifest = new HashMap<>();

    @Activate
    @Modified
    protected void Activate(Configuration config) {
        manifest.put("lang",config.lang());
        manifest.put("dir",config.dir());
        manifest.put("name",config.name());
        manifest.put("description",config.description());
        manifest.put("short_name",config.shortName());
        manifest.put("icons", Stream.of(config.icons()).map(s -> gson.fromJson(s, HashMap.class)).collect(Collectors.toList()));
        manifest.put("scope",config.scope());
        manifest.put("start_url",config.startUrl());
        manifest.put("display",config.display());
        manifest.put("orientation",config.orientation());
        manifest.put("theme_color",config.themeColor());
        manifest.put("gcm_sender_id",config.gcmSenderId());
        manifest.put("gcm_user_visible_only",config.gcmUserVisibleOnly());
        manifest.put("background_color",config.backgroundColor());
    }

    @ObjectClassDefinition(name="OSGi Annotation Demo Servlet")
    public @interface Configuration {

        @AttributeDefinition(
                name = "Language",
                description = "Language for the application"
        )
        String lang() default "en";

        //"dir": "ltr",
        @AttributeDefinition(
                name = "Direction",
                description = "Direction for app usage"
        )
        String dir() default "ltr";

        //"name": "PWA-TL30",
        @AttributeDefinition(
                name = "Name",
                description = "Name"
        )
        String name() default "PWA-TL30";


        //"gcm_sender_id": "847244712742",
        @AttributeDefinition(
                name = "gcmSenderId",
                description = "gcmSenderId"
        )
        String gcmSenderId() default "294077202000";

        //"gcm_user_visible_only": true
        @AttributeDefinition(
                name = "Name",
                description = "Name"
        )
        boolean gcmUserVisibleOnly() default true;


        //"description": "PWA-TL30 is a progressive web application powerded by Adobe Experience Manager",
        @AttributeDefinition(
                name = "Description",
                description = "Description"
        )
        String description() default "PWA-TL30 is a progressive web application powerded by Adobe Experience Manager";

         //"short_name": "PWA-TL30"
         @AttributeDefinition(
                 name = "short name",
                 description = "short name"
         )
         String shortName() default "PWA-TL30";

         // "icons"
         @AttributeDefinition(
                 name = "icons",
                 description = "icons"
         )
         String[] icons() default {
             "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon.png\",\"sizes\": \"64x64\"}",
             "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-48x48.png\",\"sizes\": \"48x48\"}",
             "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-96x96.png\",\"sizes\": \"96x96\"}",
             "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-144x144.png\",\"sizes\": \"144x144\"}",
             "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-192x192.png\",\"sizes\": \"192x192\"}",
             "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-256x256.png\",\"sizes\": \"256x256\"}"
         };

         //"scope": "/content/aem-pwa-blog/"
         @AttributeDefinition(
                 name = "scope",
                 description = "scope"
         )
         String scope() default "/content/aem-pwa-blog/";

         //"start_url": "/content/aem-pwa-blog/en.html",
         @AttributeDefinition(
                 name = "startUrl",
                 description = "startUrl"
         )
         String startUrl() default "/content/aem-pwa-blog/en.html";

         //"display": "fullscreen",
         @AttributeDefinition(
                 name = "display",
                 description = "display"
         )
         String display() default "fullscreen";

         // "orientation": "landscape",
         @AttributeDefinition(
                 name = "orientation",
                 description = "orientation"
         )
         String orientation() default "portrait";

        // "theme_color": "aliceblue",
        @AttributeDefinition(
                name = "theme_color",
                description = "theme_color"
        )
        String themeColor() default "#003c7f";

        //"background_color": "red",
        @AttributeDefinition(
                name = "background_color",
                description = "background_color"
        )
        String backgroundColor() default "#003c7f";

        /*     *   "serviceworker": {
         *     "src": "/etc.clientlibs/sw.js",
         *     "scope": "/content/aem-pwa-blog/",
         *     "update_via_cache": "none"
         *   }*/
        @AttributeDefinition(
                name = "serviceworker",
                description = "serviceworker"
        )
        String serviceworker() default "{\"src\": \"/etc.clientlibs/aem-pwa-blog/clientlibs/sw.js\",\"scope\": \"/content/aem-pwa-blog/\",\"update_via_cache\": \"none\"}";

    }

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
        final Resource resource = req.getResource();
        resp.setContentType("application/json");

        resp.getWriter().write(gson.toJson(manifest));
    }
}
