package com.adobe.summit.emea.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.Session;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kassa on 05/04/2019.
 */
@Model(
        adaptables = { SlingHttpServletRequest.class}
)
public class ProfileModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileModel.class);

    private class UserProfile {

        public UserProfile(String firstName, String lastName, String email, List<String> hobbies) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.hobbies = hobbies;
        }

        String firstName ;

        String lastName ;

        String email ;

        List<String> hobbies;

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public List<String> getHobbies() {
            return hobbies;
        }
    }


    @Self
    private SlingHttpServletRequest request;

    private boolean authenticated;

    private  UserProfile userProfile;

    @PostConstruct
    protected void init() {
        String currentUser = getCurrentUserId(request);
        if (currentUser.equals("anonymous")){
            authenticated = false;
        }else{
            authenticated = true;
            userProfile =  getUserProfile(request.getResourceResolver(), currentUser);
        }

    }



    private UserProfile getUserProfile(ResourceResolver resolver, String userId) {
        String userNameFormatted = userId;
        try {
            UserManager userManager = resolver.adaptTo(UserManager.class);

            User user = (User) userManager.getAuthorizable(userId);
            if (null != user) {
                String lastName = user
                        .getProperty("./profile/familyName") != null ? user
                        .getProperty("./profile/familyName")[0]
                        .getString() : null;
                String firstName = user
                        .getProperty("./profile/givenName") != null ? user
                        .getProperty("./profile/givenName")[0]
                        .getString() : null;

                String email = user
                        .getProperty("./profile/email") != null ? user
                        .getProperty("./profile/email")[0]
                        .getString() : null;

                List<String> hobbies = user
                        .getProperty("./profile/hobbies") != null ? Stream.of(user
                        .getProperty("./profile/hobbies")).map(v -> {
                    try{
                       return v.getString();
                    }catch (Exception e){
                        return null;
                    }
                }).filter(f -> f != null).collect(Collectors.toList()) : null;

                String userName = user.getPrincipal().getName();
                if (StringUtils.isNotBlank(lastName)) {
                    userNameFormatted = lastName;
                    if (StringUtils.isNotBlank(firstName)) {
                        userNameFormatted += ", ";
                    }
                }
                if (StringUtils.isNotBlank(firstName)) {
                    userNameFormatted += firstName;
                }
                if (StringUtils.isBlank(userNameFormatted)
                        && StringUtils.isNotBlank(userName)) {
                    userNameFormatted = userName;
                }
                return new UserProfile(firstName,lastName,email,hobbies);
            }

        } catch (Exception ex) {
            LOGGER.error("Error while getting user name", ex);
        }
        return null;
    }

    private String getCurrentUserId(SlingHttpServletRequest request) {
        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);
        String userId = session.getUserID();

        return userId;

    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }
}
