package com.adobe.summit.emea.core.servlets;

import com.adobe.summit.emea.core.services.NotificationService;
import com.adobe.summit.emea.core.utils.AuthenticationUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kassa on 08/04/2019.
 */
@Component(service=Servlet.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        property={
                Constants.SERVICE_DESCRIPTION + "=Notification Servlet - This servlet is the link between the browser and the cloud messaging provider",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
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

    public NotificationServlet() {
    }

    @Activate
    @Modified
    public void activate(NotificationServlet.Configuration configuration) {
        this.directMessage = Boolean.valueOf(configuration.directMessage());
    }

    protected void doPost(@Nonnull SlingHttpServletRequest req, @Nonnull SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String token = String.valueOf(((HashMap)this.gson.fromJson((String)req.getReader().lines().collect(Collectors.joining()), HashMap.class)).get("token"));
        LOGGER.debug("Registration token received : {}", token);
        String user = AuthenticationUtils.getCurrentUserId(req);
        LOGGER.debug("Authenticated user : {}", user);
        if(this.directMessage.booleanValue() && StringUtils.isNotBlank(token)) {
            this.notificationService.sendCommonMessage("Subscription", "You will receive web push notifications from AEM", token);
        } else {
            if(!StringUtils.isNotBlank(user) || !StringUtils.isNotBlank(token)) {
                throw new ServletException("Anonymous users can not suscribe to topics unless we have a valid token to track back this user");
            }

            ResourceResolver resourceResolver = req.getResourceResolver();
            Map userInfos = AuthenticationUtils.getUserProfile(resourceResolver, user);
            List hobbies = (List)userInfos.get("hobbies");
            this.notificationService.sendSubscriptionMessage(token, hobbies);
        }

        resp.getWriter().write("Nothing to suscribe to");
    }

    @ObjectClassDefinition(
            name = "NotificationService Configuration"
    )
    public @interface Configuration {
        @AttributeDefinition(
                name = "Registration token",
                description = "This represents the device registration token to be used for interacting via push notifications"
        )
        boolean directMessage() default true;
    }
}
