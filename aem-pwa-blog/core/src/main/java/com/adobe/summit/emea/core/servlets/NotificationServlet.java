package com.adobe.summit.emea.core.servlets;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.adobe.summit.emea.core.services.NotificationService;
import com.adobe.summit.emea.core.utils.AuthenticationUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.bouncycastle.util.encoders.Base64;
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
import javax.servlet.http.Cookie;
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
                "sling.servlet.resourceTypes="+ "cq:Page",
                "sling.servlet.selectors=" + "notification",
                "sling.servlet.extensions=" + "json"
        })
@Designate(ocd = NotificationServlet.Configuration.class)
public class NotificationServlet extends SlingAllMethodsServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServlet.class);

    @Reference
    private NotificationService notificationService;

    @Reference
    private CryptoSupport cryptoSupport;

    private Boolean directMessage;
    private Gson gson = new Gson();

    public NotificationServlet() {
    }

    @Activate
    @Modified
    public void activate(Configuration configuration) {
        this.directMessage = Boolean.valueOf(configuration.directMessage());
    }

    protected void doPost(@Nonnull SlingHttpServletRequest req, @Nonnull SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String token = String.valueOf(((HashMap)this.gson.fromJson((String)req.getReader().lines().collect(Collectors.joining()), HashMap.class)).get("token"));
        LOGGER.debug("Registration token received : {}", token);
        String user = AuthenticationUtils.getCurrentUserId(req);
        LOGGER.debug("Authenticated user : {}", user);
        if(StringUtils.isNotBlank(token)) {

            if(!StringUtils.isNotBlank(user) || !StringUtils.isNotBlank(token)) {
                throw new ServletException("Anonymous users can not suscribe to topics unless we have a valid token to track back this user");
            }

            HashMap<String,String> bodyMap = new HashMap<>();
            bodyMap.put("path","");
            bodyMap.put("message","You will receive web push notifications from AEM");
           boolean subscriptionStatus = this.notificationService.sendCommonMessage("Subscription", gson.toJson(bodyMap), token);

            // Create an encrypted string of the data.
            if(subscriptionStatus){

                ResourceResolver resourceResolver = req.getResourceResolver();
                Map userInfos = AuthenticationUtils.getUserProfile(resourceResolver, user);
                List hobbies = (List)userInfos.get("hobbies");
                boolean topicStatus = this.notificationService.sendSubscriptionMessage(token, hobbies);

                try {
                    String encryptedToken = cryptoSupport.protect(token);
                    Cookie firebaseSubscription = new Cookie ("firebaseSubscription", new String(Base64.encode(encryptedToken.getBytes())));
                    resp.addCookie(firebaseSubscription);
                } catch (CryptoException e) {
                    LOGGER.error("Error when encrypting the token");
                }
            }
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
