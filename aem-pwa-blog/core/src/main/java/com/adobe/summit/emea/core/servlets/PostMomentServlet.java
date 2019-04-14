package com.adobe.summit.emea.core.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
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
        String tags = req.getParameter("tags");
        Object file = req.getParameter("file");
        byte[] bytes = ((String) file).getBytes();
        InputStream targetStream = new ByteArrayInputStream(bytes);
     

        if (selectors.length != 0 && "create".equals(selectors[0])) {
        	
        	writeToDam(targetStream, title);
        	
        	
        	LOGGER.info("---> Post moment successfully created.");
            resp.getWriter().write("{'msg': 'Post moment successfully created.' }");
            
        } else if (selectors.length != 0 && "update".equals(selectors[0])){
            //@TODO : Nice to have but not mandatory for the lab
        	
        	LOGGER.info("---> Post moment successfully updated.");
            resp.getWriter().write("{'msg': 'Post moment successfully updated.' }");
        }
    }
    
    //Save the uploaded file into the AEM DAM using AssetManager APIs
    private String writeToDam(InputStream is, String fileName) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "pwaContentWriteUserAccess");
        ResourceResolver resolver = null;
     
            
        try {
                       
            //Invoke the adaptTo method to create a Session used to create a QueryManager
            resolver = resolverFactory.getServiceResourceResolver(param);
            
            //Use AssetManager to place the file into the AEM DAM
            com.day.cq.dam.api.AssetManager assetMgr = resolver.adaptTo(com.day.cq.dam.api.AssetManager.class);
            String newFile = "/content/dam/aem-pwa-blog/travel/"+fileName ; 
            assetMgr.createAsset(newFile, is,"image/png", true);
          
     
            // Return the path to the file was stored
            return newFile;
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        return null;
    }
}
