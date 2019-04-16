package com.adobe.summit.emea.core.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.bouncycastle.util.encoders.Base64;
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
                Constants.SERVICE_DESCRIPTION + "=PostMomentServlet Servlet - This servlet will help to create a captured moment and more...",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths="+ "/bin/aem-pwa-blog/share-post",
                "sling.servlet.extensions=" + "json"
        })
public class PostMomentServlet extends SlingAllMethodsServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostMomentServlet.class);
    @Reference
    private ResourceResolverFactory resolverFactory;

    private Gson gson = new Gson();

    public PostMomentServlet() {
    }

    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String id = req.getParameter("id");
        String tags = req.getParameter("tags");
        String title = req.getParameter("title");
        String file = req.getParameter("file").replace("data:image/png;base64,", "");
        byte[] initialArray = Base64.decode(file.getBytes());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(initialArray);
        String fullAssetPath = "/content/dam/aem-pwa-blog/uploads/" + title + "_" + System.currentTimeMillis() + ".jpg";
        AssetManager assetManager = (AssetManager)req.getResourceResolver().adaptTo(AssetManager.class);
        Asset imageAsset = assetManager.createAsset(fullAssetPath, inputStream, "image/jpeg", true);

        HashMap<String,String> res = new HashMap<>();
        res.put("id",id);
        if(imageAsset != null) {
            res.put("msg","Post moment successfully created.");

        } else {
            res.put("msg","Post moment successfully had an issue.");
        }
        resp.getWriter().write(this.gson.toJson(res));

    }
}
