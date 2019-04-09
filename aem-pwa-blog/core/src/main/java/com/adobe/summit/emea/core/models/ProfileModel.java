package com.adobe.summit.emea.core.models;

import com.adobe.summit.emea.core.utils.AuthenticationUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Self
    private SlingHttpServletRequest request;

    protected boolean authenticated;

    String firstName = "" ;

    String lastName = "" ;

    String email = "" ;

    List<String> hobbies = Collections.emptyList();



    @PostConstruct
    protected void init() {
        String currentUser = getCurrentUserId(request);
        if (currentUser.equals("anonymous")){
            authenticated = false;
        }else{
            authenticated = true;
            getUserProfile(request.getResourceResolver(), currentUser);
        }

    }

    private void getUserProfile(ResourceResolver resolver, String userId) {

        Map<String,Object> userInfos = AuthenticationUtils.getUserProfile(resolver,userId);
        firstName = String.valueOf(userInfos.get("firstName"));
        lastName = String.valueOf(userInfos.get("lastName"));
        email = String.valueOf(userInfos.get("email"));
        hobbies = (List<String>) userInfos.get("hobbies");

    }

    protected String getCurrentUserId(SlingHttpServletRequest request) {
        return AuthenticationUtils.getCurrentUserId(request);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

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
