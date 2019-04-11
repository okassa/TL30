package com.adobe.summit.emea.core.servlets;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
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
                Constants.SERVICE_DESCRIPTION + "=ServiceWorker Servlet - This servlet will expose the service worker at /content/aem-pwa-blog/sw.js to be compliant with" +
                        "service worker requirements in the browser. ",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM",
                HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN+"=/content/aem-pwa-blog/sw.js" ,
                HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT+"="+ ("(" + HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME + "=org.osgi.service.http)")
        })
@Designate(ocd = ManifestServlet.Configuration.class)
public class ServiceWorkerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceWorkerServlet.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @ObjectClassDefinition(name="OSGi Annotation Demo Servlet")
    public @interface Configuration {

        @AttributeDefinition(
                name = "Service Worker JS file path",
                description = "Location of the sw.js file"
        )
        String serviceWorkerPath() default "/etc/clientlibs/aem-pwa-blog/sw.js";

    }

    private String serviceWorkerPath ;

    @Activate
    @Modified
    public void activate(Configuration configuration){
        this.serviceWorkerPath = configuration.serviceWorkerPath() ;
    }


    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp) throws ServletException, IOException {

        try(ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "msm-service"))){
            resp.setContentType("text/javascript");

            String serviceWorker = "";

            Resource serviceWorkerDataResource = resourceResolver.getResource(serviceWorkerPath+"/jcr:content");
            if (serviceWorkerDataResource == null){
                serviceWorker = "{error : 'The service worker file has been deleted or moved to a " +
                        "different location than expected. Please update the configuration to adjust the path.'}" ;
                LOGGER.error(serviceWorker);
            }else{
                InputStream serviceWorkerInputStream = serviceWorkerDataResource.adaptTo(InputStream.class);
                serviceWorker = IOUtils.toString(serviceWorkerInputStream, "UTF-8");
            }

            resp.getWriter().write(serviceWorker);

        }catch (Exception e){

        }

    }
}
