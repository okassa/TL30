package com.adobe.summit.emea.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kassa on 01/04/2019.
 */
@SuppressWarnings("serial")
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=PostMomentServlet Servlet - This servlet will help to create a captured moment",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourceTypes="+ "aem-pwa-blog/components/structure/post-page",
                "sling.servlet.selectors=" + "create",
                "sling.servlet.selectors=" + "update",
                "sling.servlet.extensions=" + "html"
        })
public class PostMomentServlet extends SlingAllMethodsServlet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PostMomentServlet.class);
	
	@Reference
    private ResourceResolverFactory resolverFactory;
	
    @Override
    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String[] selectors = req.getRequestPathInfo().getSelectors();
        String title = req.getParameter("title");
        String location = req.getParameter("location");
        String file = req.getParameter("file");

        if (selectors.length != 0 && "create".equals(selectors[0])) {
        	
        	
        	
        	
        	
        	
        	
        	LOGGER.info("---> Post moment successfully created.");
            resp.getWriter().write("{'msg': 'Post moment successfully created.' }");
            
        } else if (selectors.length != 0 && "update".equals(selectors[0])){
            //@TODO : Nice to have but not mandatory for the lab
        	
        	LOGGER.info("---> Post moment successfully updated.");
            resp.getWriter().write("{'msg': 'Post moment successfully updated.' }");
        }
    }
}
