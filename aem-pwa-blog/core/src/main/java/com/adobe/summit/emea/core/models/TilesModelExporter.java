package com.adobe.summit.emea.core.models;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * Created by kassa on 12/04/2019.
 */
@Model(
        adaptables = { SlingHttpServletRequest.class },
        resourceType = "aem-pwa-blog/components/content/tiles",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = "jackson", extensions = "json", options = {

        @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true"),
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value="false")
})
public class TilesModelExporter {
    @Self
    private SlingHttpServletRequest request;

    @Self
    private Resource resource;

    private List<Resource> teasers = Collections.emptyList();

    @PostConstruct
    private void init() {
        Resource resource = request.getResource();
        if (resource != null){
            this.teasers = IteratorUtils.toList(resource.listChildren());
        }
    }

    public List<Resource> getTeasers() {
        return teasers;
    }
}
