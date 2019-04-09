package com.adobe.summit.emea.core.servlets;

import com.adobe.granite.security.user.util.AuthorizableUtil;
import com.adobe.summit.emea.core.services.NotificationService;
import com.adobe.summit.emea.core.services.impl.NotificationServiceImpl;
import com.adobe.summit.emea.core.utils.AuthenticationUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.jackrabbit.user.AuthorizableQueryManager;
import org.apache.jackrabbit.oak.plugins.value.ValueFactoryImpl;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.ValueFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Designate(ocd = NotificationServlet.Configuration.class)
public class NotificationServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServlet.class);

    @Reference
    private NotificationService notificationService;

    private Boolean directMessage;

    private Gson gson = new Gson();

    @ObjectClassDefinition(name="NotificationService Configuration")
    public @interface Configuration {

        @AttributeDefinition(
                name = "Registration token",
                description = "This represents the device registration token to be used for interacting via push notifications"
        )
        boolean directMessage() default true;

    }

    @Activate
    @Modified
    public void activate(Configuration configuration){
        this.directMessage = configuration.directMessage();
    }
    @Override
    protected void doPost(@Nonnull SlingHttpServletRequest req, @Nonnull SlingHttpServletResponse resp) throws ServletException, IOException {

            resp.setContentType("application/json");

            // Get request body which contains the registration token to submit against firebase apis
            String token = String.valueOf(gson.fromJson(req.getReader().lines().collect(Collectors.joining()), HashMap.class).get("token"));
            LOGGER.debug("Registration token received : {}",token);

            // Get user informations to use the user's hobbies for subscriptions
            String user = AuthenticationUtils.getCurrentUserId(req);
             LOGGER.debug("Authenticated user : {}",user);
            if (directMessage && StringUtils.isNotBlank(token)){
                notificationService.sendCommonMessage("Subscription","You will receive web push notifications from AEM",token);
            }else{
                if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(token)){
                    ResourceResolver resourceResolver = req.getResourceResolver();
                    Map<String,Object> userInfos = AuthenticationUtils.getUserProfile(resourceResolver,user);

                    List<String> hobbies = (List<String>)userInfos.get("hobbies");
                    // Suscribe the device for each hobby so that when an event
                    // related to that hobby occurs the user gonna be automatically notified.
                    notificationService.sendSubscriptionMessage(token,hobbies);
                }else{
                    // If user anonymous send back an exception because we can not use profile hobbies
                    throw new ServletException("Anonymous users can not suscribe to topics unless we have a valid token to track back this user");
                }
            }





            resp.getWriter().write("Nothing to suscribe to");

    }
}
