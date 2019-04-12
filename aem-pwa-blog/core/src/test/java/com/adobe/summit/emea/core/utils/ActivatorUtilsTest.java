package com.adobe.summit.emea.core.utils;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by kassa on 05/10/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivatorUtilsTest {

    @Before
    public void setUp() throws Exception{

    }

    @Test
    public void testFormatIcons(){
        String[] icons = {"{\"src\": \"/etc/clientlibs/aem-pwa-blog/icons/summit-icon-48x48.png\",\"sizes\": \"48x48\"}"};
        Object iconsFormatted = ActivatorUtils.formatIcons(icons);
    }

}
