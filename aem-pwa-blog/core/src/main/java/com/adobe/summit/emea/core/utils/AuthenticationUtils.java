package com.adobe.summit.emea.core.utils;

import com.adobe.summit.emea.core.models.NavigationModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kassa on 09/04/2019.
 */
public final class AuthenticationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtils.class);


    public static final String getCurrentUserId(SlingHttpServletRequest request) {
        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);
        String userId = session.getUserID();

        return userId;

    }

    public static Map<String,Object> getUserProfile(ResourceResolver resolver, String userId) {
        String userNameFormatted = userId;
        try {

            UserManager userManager = resolver.adaptTo(UserManager.class);

            User user = (User) userManager.getAuthorizable(userId);
            if (null != user) {
               String lastName = user
                        .getProperty("./profile/familyName") != null ? user
                        .getProperty("./profile/familyName")[0]
                        .getString() : "";
              String  firstName = user
                        .getProperty("./profile/givenName") != null ? user
                        .getProperty("./profile/givenName")[0]
                        .getString() : "";

             String  email = user
                        .getProperty("./profile/email") != null ? user
                        .getProperty("./profile/email")[0]
                        .getString() : "";

             List<String> hobbies = user
                        .getProperty("./profile/hobbies") != null ? Stream.of(user
                        .getProperty("./profile/hobbies")).map(v -> {
                    try{
                        return v.getString();
                    }catch (Exception e){
                        return null;
                    }
                }).filter(f -> f != null).collect(Collectors.toList()) : Collections.emptyList();


                Map<String,Object> userInfos = new HashMap<>();
                userInfos.put("lastname",lastName);
                userInfos.put("firstname",firstName);
                userInfos.put("email",email);
                userInfos.put("hobbies",hobbies);

                return userInfos;
            }

        } catch (Exception ex) {
            LOGGER.error("Error while getting user name", ex);
        }
        return null;
    }
}
