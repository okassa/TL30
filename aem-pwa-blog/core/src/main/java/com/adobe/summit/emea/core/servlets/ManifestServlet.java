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
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        property={
                Constants.SERVICE_DESCRIPTION + "=Manifest Servlet - This servlet will expose all informations used to make the browser understand that the website is a PWA",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths="+ "/content/manifest",
                "sling.servlet.extensions=" + "json"
        })
@Designate(ocd = ManifestServlet.Configuration.class)
public class ManifestServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();
    private HashMap<String, Object> manifest = new HashMap();

    public ManifestServlet() {
    }

    @Activate
    @Modified
    protected void Activate(Configuration config) {
        this.manifest.put("lang", config.lang());
        this.manifest.put("dir", config.dir());
        this.manifest.put("name", config.name());
        this.manifest.put("description", config.description());
        this.manifest.put("short_name", config.shortName());
        this.manifest.put("icons", Stream.of(config.icons()).map((s) -> {
            return (HashMap)this.gson.fromJson(s, HashMap.class);
        }).collect(Collectors.toList()));
        this.manifest.put("scope", config.scope());
        this.manifest.put("start_url", config.startUrl());
        this.manifest.put("display", config.display());
        this.manifest.put("orientation", config.orientation());
        this.manifest.put("theme_color", config.themeColor());
        this.manifest.put("gcm_sender_id", config.gcmSenderId());
        this.manifest.put("gcm_user_visible_only", Boolean.valueOf(config.gcmUserVisibleOnly()));
        this.manifest.put("background_color", config.backgroundColor());
        this.manifest.put("serviceworker", this.gson.fromJson(config.serviceworker(), HashMap.class));
    }

    protected void doGet(@Nonnull SlingHttpServletRequest req, @Nonnull SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.getWriter().write(this.gson.toJson(this.manifest));
    }

    @ObjectClassDefinition(
            name = "OSGi Annotation Demo Servlet"
    )
    public @interface Configuration {
        @AttributeDefinition(
                name = "Language",
                description = "Language for the application"
        )
        String lang() default "en";

        @AttributeDefinition(
                name = "Direction",
                description = "Direction for app usage"
        )
        String dir() default "ltr";

        @AttributeDefinition(
                name = "Name",
                description = "Name"
        )
        String name() default "PWA-TL30";

        @AttributeDefinition(
                name = "gcmSenderId",
                description = "gcmSenderId"
        )
        String gcmSenderId() default "103953800507";

        @AttributeDefinition(
                name = "Name",
                description = "Name"
        )
        boolean gcmUserVisibleOnly() default true;

        @AttributeDefinition(
                name = "Description",
                description = "Description"
        )
        String description() default "PWA-TL30 is a progressive web application powerded by Adobe Experience Manager";

        @AttributeDefinition(
                name = "short name",
                description = "short name"
        )
        String shortName() default "PWA-TL30";

        @AttributeDefinition(
                name = "icons",
                description = "icons"
        )
        String[] icons() default {"{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon.png\",\"sizes\": \"64x64\"}", "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-48x48.png\",\"sizes\": \"48x48\"}", "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-96x96.png\",\"sizes\": \"96x96\"}", "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-144x144.png\",\"sizes\": \"144x144\"}", "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-192x192.png\",\"sizes\": \"192x192\"}", "{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-256x256.png\",\"sizes\": \"256x256\"}"};

        @AttributeDefinition(
                name = "scope",
                description = "scope"
        )
        String scope() default "/content/aem-pwa-blog/";

        @AttributeDefinition(
                name = "startUrl",
                description = "startUrl"
        )
        String startUrl() default "/content/aem-pwa-blog/home.html";

        @AttributeDefinition(
                name = "display",
                description = "display"
        )
        String display() default "fullscreen";

        @AttributeDefinition(
                name = "orientation",
                description = "orientation"
        )
        String orientation() default "portrait";

        @AttributeDefinition(
                name = "theme_color",
                description = "theme_color"
        )
        String themeColor() default "#003c7f";

        @AttributeDefinition(
                name = "background_color",
                description = "background_color"
        )
        String backgroundColor() default "#003c7f";

        @AttributeDefinition(
                name = "serviceworker",
                description = "serviceworker"
        )
        String serviceworker() default "{\"src\": \"/content/sw.js\",\"scope\": \"/content/aem-pwa-blog/home.html\",\"update_via_cache\": \"none\"}";
    }
}
