//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.adobe.summit.emea.core.models;

import com.adobe.summit.emea.core.models.ProfileModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(
        adaptables = {SlingHttpServletRequest.class}
)
@Exporter(
        name = "jackson",
        extensions = {"json"},
        options = {@ExporterOption(
                name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY",
                value = "true"
        ), @ExporterOption(
                name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                value = "false"
        )}
)
public class NavigationModel extends ProfileModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationModel.class);
    @Self
    private SlingHttpServletRequest request;
    private List<Resource> menuPages = new ArrayList();

    public NavigationModel() {
    }

    @PostConstruct
    protected void init() {
        super.init();
        Resource resource = this.request.getResource();
        ResourceResolver resourceResolver = this.request.getResourceResolver();
        String resourceParentPath = ResourceUtil.getParent(resource.getPath(), 3);
        LOGGER.debug("Authentication status : {}", Boolean.valueOf(this.authenticated));
        IteratorUtils.toList(resourceResolver.resolve(resourceParentPath).listChildren()).stream().filter((n) -> {
            return n.getResourceType().equals("cq:Page");
        }).forEach((e) -> {
            if(e.getName().equals("login") && this.authenticated) {
                LOGGER.debug("Login link for an authenticated user should not be displayed ");
            } else if(e.getName().equals("profile") && !this.authenticated) {
                LOGGER.debug("Profile link for an non authenticated user should not be displayed ");
            } else {
                LOGGER.debug("Link for page {} has been added ", e.getName());
                this.menuPages.add(e);
            }

        });
    }

    public List<Resource> getMenuPages() {
        return this.menuPages;
    }
}
