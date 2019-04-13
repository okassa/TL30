package com.adobe.summit.emea.core.models;

import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.dam.api.Asset;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.resource.filter.ResourceFilterStream;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private static final String DAM_PATH = "/content/dam/aem-pwa-blog";

    private List<Resource> teasers = Collections.emptyList();

    @PostConstruct
    private void init() {

        // Build a set of resource based on the dam
        ///content/dam/aem-pwa-blog/exercises-notifications

        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource damRootRes = resourceResolver.resolve(DAM_PATH);
        if (damRootRes != null){
            ResourceFilterStream rfs = damRootRes.adaptTo(ResourceFilterStream.class);
            // Filter only assets

            this.teasers = rfs.setChildSelector("[jcr:primaryType] == 'dam:Asset'")
                    .stream()
                    .map(a -> {
                        String assetName = FilenameUtils.removeExtension(a.getName());
                        Asset asset = a.adaptTo(Asset.class);
                        Object teaserTitle = asset.getMetadata("teaserTitle");
                        Object teaserDescription = asset.getMetadata("teaserDescription");
                        String path = String.format("/content/aem-pwa-blog/home/jcr:content/tiles/teaser_%s",System.currentTimeMillis());
                        HashMap<String,Object> map = new HashMap();
                        map.put("title", !Objects.isNull(teaserTitle)?String.valueOf(teaserTitle):StringUtils.capitalize(assetName));
                        map.put("description",!Objects.isNull(teaserDescription)?String.valueOf(teaserDescription):"Lorem Ipsum Description for" +StringUtils.capitalize(assetName));
                        map.put("fileReference",a.getPath());
                        ValueMap vm = new ValueMapDecorator(map);
                        ValueMapResource vmr = new ValueMapResource(resourceResolver,path,"nt:unstructured",vm);
                        return vmr;
                    })
                    .collect(Collectors.toList());

            teasers.size();
        }


    }

    public List<Resource> getTeasers() {
        return teasers;
    }
}
