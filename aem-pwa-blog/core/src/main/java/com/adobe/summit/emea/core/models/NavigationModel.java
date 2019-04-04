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
import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Required;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(
        adaptables = { Resource.class}
)
public class NavigationModel {

    public static final String RESOURCE_TYPE = "aem-pwa-blog/components/content/navigation";
    @Self
    private Resource resource ;

    @SlingObject
    @Required
    private ResourceResolver resourceResolver;


    private List<Resource> menuPages;

    @PostConstruct
    protected void init() {
        // Add sub nodes
        //Resource resource = request.getResource();
       String resourceParentPath = ResourceUtil.getParent(resource.getPath(),2);
        menuPages = IteratorUtils.toList(resourceResolver.resolve(resourceParentPath).listChildren());
        // Add virtual resource for HEnable notification
        Map<String,Object> hm = new HashMap<>();
        hm.put("jcr:title","Enable notifications");
        hm.put("icon","fa fa-bell");
        ValueMap vm = new ValueMapDecorator(hm);
        Resource enableNotificationResource = new ValueMapResource(resourceResolver,"/content/aem-pwa-blog/enable-notifications","aem-pwa-blog/components/page",vm);
        menuPages.add(enableNotificationResource);

    }

    public List<Resource> getMenuPages() {
        return menuPages;
    }
}
