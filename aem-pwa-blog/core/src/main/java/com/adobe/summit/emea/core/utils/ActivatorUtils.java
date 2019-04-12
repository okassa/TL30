package com.adobe.summit.emea.core.utils;

import com.google.gson.Gson;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kassa on 09/04/2019.
 */
public final class ActivatorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivatorUtils.class);

    private static final String IMAGE_PATTERN = "\\{\"src\":[.*]+,\"sizes\":[.*]\\}";

    private Gson gson = new Gson();


    public static Object formatIcons(String[] icons) {

        return null;
    }
}
