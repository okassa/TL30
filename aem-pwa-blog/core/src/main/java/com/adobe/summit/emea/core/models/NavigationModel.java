/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.summit.emea.core.models;

import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.adobe.summit.emea.core.events.BlogContentChangeListener;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Required;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Model(
        adaptables = { SlingHttpServletRequest.class}
)
public class NavigationModel extends ProfileModel {


    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationModel.class);

    @Self
    private SlingHttpServletRequest request;


    private List<Resource> menuPages = new ArrayList<>();

    @PostConstruct
    protected void init() {

        super.init();
        // Add sub nodes
        Resource resource = request.getResource();
        ResourceResolver resourceResolver = request.getResourceResolver();
        String resourceParentPath = ResourceUtil.getParent(resource.getPath(),3);

        LOGGER.debug("Authentication status : {}",authenticated);

         IteratorUtils.toList(resourceResolver.resolve(resourceParentPath).listChildren())
                .stream()
                 .filter(n -> n.getResourceType().equals("cq:Page"))
                 .forEach( e -> {
                    if (e.getName().equals("login") && authenticated){
                        // We skip^
                        LOGGER.debug("Login link for an authenticated user should not be displayed ");
                    }else if (e.getName().equals("profile") && !authenticated) {
                        LOGGER.debug("Profile link for an non authenticated user should not be displayed ");
                    }else{
                        LOGGER.debug("Link for page {} has been added ", e.getName());
                        menuPages.add(e);
                    }
                });

                /**
                .filter(n -> n.getResourceType().equals("cq:Page"))
                //.filter(s -> (s.getName().equals("login") && authenticated) || (s.getName().equals("profile") && !authenticated))
                .collect(Collectors.toList());Collectors.toList();

                 **/

    }

    public List<Resource> getMenuPages() {
        return menuPages;
    }
}
