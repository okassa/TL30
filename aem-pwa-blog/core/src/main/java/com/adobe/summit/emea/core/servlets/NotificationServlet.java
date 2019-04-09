package com.adobe.summit.emea.core.servlets;

import com.adobe.summit.emea.core.services.NotificationService;
import com.google.gson.Gson;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by kassa on 08/04/2019.
 */
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths="+ "/bin/aem-pwa-blog/notifications",
                "sling.servlet.extensions=" + "json"
        })
@Designate(ocd = ManifestServlet.Configuration.class)
public class NotificationServlet extends SlingAllMethodsServlet {

    @Reference
    private NotificationService notificationService;

    private Gson gson = new Gson();

    @Override
    protected void doPost(@Nonnull SlingHttpServletRequest req, @Nonnull SlingHttpServletResponse resp) throws ServletException, IOException {

            final Resource resource = req.getResource();
            resp.setContentType("application/json");


            // Get request body
            String token = String.valueOf(gson.fromJson(req.getReader().lines().collect(Collectors.joining()), HashMap.class).get("token"));

            resp.getWriter().write("Nothing to suscribe to");

    }
}
