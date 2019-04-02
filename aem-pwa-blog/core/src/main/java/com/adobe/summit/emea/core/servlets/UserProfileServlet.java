package com.adobe.summit.emea.core.servlets;

import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Created by kassa on 01/04/2019.
 */
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "= UserProfileServlet Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourceTypes="+ "aem-pwa-blog/components/structure/page",
                "sling.servlet.selectors=" + "{create,update}",
                "sling.servlet.extensions=" + "html"
        })
@Designate(ocd = ManifestServlet.Configuration.class)
public class UserProfileServlet extends SlingAllMethodsServlet {

    @Override
    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws ServletException, IOException {
        final Resource resource = req.getResource();
        resp.setContentType("application/json");

        String[] selectors = req.getRequestPathInfo().getSelectors();
        // Get request body
        String body = req.getReader().lines().collect(Collectors.joining());

        if (selectors.length != 0 && selectors[0] == "create"){
            // Create a user at /home/users/aem-pwa-blog/xxxx
            ResourceResolver resourceResolver = req.adaptTo(ResourceResolver.class);
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);



        }else if (selectors.length != 0 && selectors[0] == "update"){

        }

        resp.getWriter().write("Nothing to suscribe to");
    }
}
