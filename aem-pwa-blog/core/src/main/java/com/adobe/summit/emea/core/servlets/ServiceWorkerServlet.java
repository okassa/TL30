package com.adobe.summit.emea.core.servlets;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.metatype.annotations.Designate;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

/**
 * Created by kassa on 01/04/2019.
 */
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=Service Worker Root Servlet",
                HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/content/aem-pwa-blog/sw.js" ,
                HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT+"="+ ("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=org.osgi.service.http)")
        })
public class ServiceWorkerServlet extends HttpServlet {

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp) throws ServletException, IOException {

        try(ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "msm-service"))){
            resp.setContentType("text/javascript");

            Resource serviceWorkerDataResource = resourceResolver.getResource("/etc/clientlibs/aem-pwa-blog/sw.js/jcr:content");
            InputStream serviceWorkerInputStream = serviceWorkerDataResource.adaptTo(InputStream.class);
            String serviceWorker = IOUtils.toString(serviceWorkerInputStream, "UTF-8");

            // Use request dispatcher instead

            resp.getWriter().write(""+serviceWorker);

        }catch (Exception e){

        }

    }
}
