package com.adobe.summit.emea.core.servlets;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kassa on 01/04/2019.
 */
@SuppressWarnings("serial")
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=UserProfile Servlet - This servlet will help to register users and to update their profile when logged in",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourceTypes="+ "aem-pwa-blog/components/structure/profile-page",
                "sling.servlet.selectors=" + "create",
                "sling.servlet.selectors=" + "update",
                "sling.servlet.extensions=" + "html"
        })
@Designate(ocd = ManifestServlet.Configuration.class)
public class UserProfileServlet extends SlingAllMethodsServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileServlet.class);
    @Reference
    private ResourceResolverFactory resolverFactory;

    public UserProfileServlet() {
    }

    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String[] selectors = req.getRequestPathInfo().getSelectors();
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String hobbies = req.getParameter("hobbies");
        if(selectors.length != 0 && "create".equals(selectors[0])) {
            ResourceResolver resolver = null;
            Session session = null;

            try {
                resolver = this.resolverFactory.getServiceResourceResolver(Collections.singletonMap("sling.service.subservice", "pwaWriteUserAccess"));
                session = (Session)resolver.adaptTo(Session.class);
                UserManager e = (UserManager)resolver.adaptTo(UserManager.class);
                User user = null;
                if(e.getAuthorizable(email) == null) {
                    user = e.createUser(email, password, new SimplePrincipal(email), "/home/users/aem-pwa-blog");
                    ValueFactory valueFactory = session.getValueFactory();
                    Value firstNameValue = valueFactory.createValue(firstName, 1);
                    user.setProperty("./profile/givenName", firstNameValue);
                    Value lastNameValue = valueFactory.createValue(lastName, 1);
                    user.setProperty("./profile/familyName", lastNameValue);
                    Value emailValue = valueFactory.createValue(email, 1);
                    user.setProperty("./profile/email", emailValue);
                    Value hobbiesValue = valueFactory.createValue(hobbies, 1);
                    user.setProperty("./profile/hobbies", hobbiesValue);
                    session.refresh(true);
                    session.save();
                    LOGGER.info("---> {} User successfully created and added into group.", user.getID());
                    resp.getWriter().write("Account created, you can log in now with your email and password !!!");
                } else {
                    LOGGER.info("---> User already exist..");
                    resp.getWriter().write("Email already exists, you can log in now with your email and password !!!");
                }
            } catch (RepositoryException | LoginException var21) {
                LOGGER.info("---> Error {} ", var21);
                resp.getWriter().write("Error during registration process, please try later or contact administrator !!!");
            } finally {
                if(resolver != null) {
                    resolver.close();
                }

                if(session != null) {
                    session.logout();
                }

            }
        } else if(selectors.length != 0 && "update".equals(selectors[0])) {
            ;
        }

    }

    private static class SimplePrincipal implements Principal {
        protected final String name;

        public SimplePrincipal(String name) {
            if(name.compareTo("") == 0) {
                throw new IllegalArgumentException("Principal name cannot be blank.");
            } else {
                this.name = name;
            }
        }

        public String getName() {
            return this.name;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(Object obj) {
            return obj instanceof Principal?this.name.equals(((Principal)obj).getName()):false;
        }
    }
}
