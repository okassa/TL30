package com.adobe.summit.emea.core.servlets;

import java.io.IOException;
import java.security.Principal;
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
                Constants.SERVICE_DESCRIPTION + "= UserProfileServlet Servlet",
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
	
    @Override
    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String[] selectors = req.getRequestPathInfo().getSelectors();
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String hobbies = req.getParameter("hobbies");

        if (selectors.length != 0 && "create".equals(selectors[0])) {
            // Create a user at /home/users/aem-pwa-blog/xxxx
            ResourceResolver resolver = null;
            Session session = null;
			try {
				Map<String, Object> serviceParams = new HashMap<String, Object>();
	            serviceParams.put(ResourceResolverFactory.SUBSERVICE, "pwaWriteUserAccess");

				resolver = resolverFactory.getServiceResourceResolver(serviceParams);
				session = resolver.adaptTo(Session.class);
				
	            UserManager userManager = resolver.adaptTo(UserManager.class);
				
				// Create UserManager Object
		        User user = null;
		        if (userManager.getAuthorizable(email) == null) {
		        	
		            user = userManager.createUser(email, password, new SimplePrincipal(email), "/home/users/aem-pwa-blog");

		            ValueFactory valueFactory = session.getValueFactory();
		            Value firstNameValue = valueFactory.createValue(firstName, PropertyType.STRING);
		            user.setProperty("./profile/givenName", firstNameValue);

		            Value lastNameValue = valueFactory.createValue(lastName, PropertyType.STRING);
		            user.setProperty("./profile/familyName", lastNameValue);

		            Value emailValue = valueFactory.createValue(email, PropertyType.STRING);
		            user.setProperty("./profile/email", emailValue);
		            
		            Value hobbiesValue = valueFactory.createValue(hobbies, PropertyType.STRING);
		            user.setProperty("./profile/hobbies", hobbiesValue);
		            
		            session.save();
		            LOGGER.info("---> {} User successfully created and added into group.", user.getID());
		            resp.getWriter().write("Account created, you can connect now with your email and password !!!");
		        } else {
		        	LOGGER.info("---> User already exist..");
		        	resp.getWriter().write("Email already exist, you can connect now with your email and password !!!");
		        }
				
			} catch (LoginException | RepositoryException e) {
				LOGGER.info("---> Error {} ", e);
				resp.getWriter().write("Error during registration process, please try later or contact administrator !!!");
			}            
        } else if (selectors.length != 0 && "update".equals(selectors[0])){

        }

        
    }
    
    
    
    
    private static class SimplePrincipal implements Principal {
        protected final String name;
    
        public SimplePrincipal(String name) {
            if (name.compareTo("")==0) {
                throw new IllegalArgumentException("Principal name cannot be blank.");
            }
            this.name = name;
        }
    
        public String getName() {
            return name;
        }
    
        @Override
        public int hashCode() {
            return name.hashCode();
        }
    
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Principal) {
                return name.equals(((Principal) obj).getName());
            }
            return false;
        }
    }
}
