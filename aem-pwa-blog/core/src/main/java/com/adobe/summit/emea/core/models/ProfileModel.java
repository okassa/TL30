//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.adobe.summit.emea.core.models;

import com.adobe.summit.emea.core.utils.AuthenticationUtils;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(
        adaptables = {SlingHttpServletRequest.class}
)
public class ProfileModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileModel.class);
    @Self
    private SlingHttpServletRequest request;
    protected boolean authenticated;
    String firstName = "";
    String lastName = "";
    String email = "";
    List<String> hobbies = Collections.emptyList();

    public ProfileModel() {
    }

    @PostConstruct
    protected void init() {
        String currentUser = this.getCurrentUserId(this.request);
        if(currentUser.equals("anonymous")) {
            this.authenticated = false;
        } else {
            this.authenticated = true;
            this.getUserProfile(this.request.getResourceResolver(), currentUser);
        }

    }

    private void getUserProfile(ResourceResolver resolver, String userId) {
        Map userInfos = AuthenticationUtils.getUserProfile(resolver, userId);
        this.firstName = String.valueOf(userInfos.get("firstName"));
        this.lastName = String.valueOf(userInfos.get("lastName"));
        this.email = String.valueOf(userInfos.get("email"));
        this.hobbies = (List)userInfos.get("hobbies");
    }

    protected String getCurrentUserId(SlingHttpServletRequest request) {
        return AuthenticationUtils.getCurrentUserId(request);
    }

    public boolean isAuthenticated() {
        return this.authenticated;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public List<String> getHobbies() {
        return this.hobbies;
    }
}
