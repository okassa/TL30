package com.adobe.summit.emea.core.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.bouncycastle.util.encoders.Base64;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kassa on 01/04/2019.
 */
@SuppressWarnings("serial")
@Component(service=Servlet.class,
        property={
                Constants.SERVICE_DESCRIPTION + "=PostMomentServlet Servlet - This servlet will help to create a captured moment and more...",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourceTypes="+ "cq:Page",
                "sling.servlet.selectors=" + "share-moment",
                "sling.servlet.extensions=" + "json"
        })
public class PostMomentServlet extends SlingAllMethodsServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostMomentServlet.class);
    @Reference
    private ResourceResolverFactory resolverFactory;

    private static final String EMPTY = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAZAAAACWCAYAAADwkd5lAAARGUlEQVR4Xu2bC8ifUxzHf7MZr9nM3jdjEa1cwpaQhLnlPikloeQyspmFyTWbCCmJklxyyXUooiR3a7m0pnkl17FcJsw2lzVGY/od/n/vXv+953me/3Oec/s8pXn/z3nO+Z3P7/ec7/n9zv8/rL+/f/2ff/4pPT09MmrUKBkxYoRwQQACEIAABAYTWLdunaxZs0Z+++03GT58uAz75ptv1o8fP15WrlwpK1asMO37+vqkt7fXNOCCAAQgAIF8CWiC0Ukfli9f/o+ATJgwoU1HlUWFRB8YPXq0EZKxY8fmS4+ZQwACEMiQwE8//WR0YPXq1UYHNLHQSlXr+vbbb/8vIAM52TrIkClThgAEIJAsgTIJhFVAWpQ2lsJQ4ko2jpgYBCCQCYGq63thARnIsYxCZcKfaUIAAhCIjkC3FaZKAkKJK7o4wWAIQAAChkCdCUDXAkKJi6iEAAQgEDaBqiUq26xqExBKXDbU3IcABCDQLIFuS1Q2a50ICCUuG3buQwACEHBDoM4Slc1C5wJCicvmAu5DAAIQ6I6AqxKVzarGBIQSl80V3IcABCBQjoDrEpXNGi8CQonL5hbuQwACEOhMoMkSlc0H3gWEEpfNRdyHAARyJ+CrRGXjHoyAUOKyuYr7EIBAbgR8l6hsvIMUEEpcNrdxHwIQSJVASCUqG+PgBYQSl82F3IcABGInEGqJysY1GgGhxGVzJfchAIHYCIReorLxjFJAKHHZ3Mp9CEAgVAIxlahsDKMXEEpcNhdzHwIQ8E0g1hKVjVsyAkKJy+Zq7kMAAk0TiL1EZeOVpIBQ4rK5nfsQgIArAimVqGyMkhcQSly2EOA+BCDQLYFUS1Q2LtkICCUuWyhwHwIQKEsg9RKVjUeWAkKJyxYW3IcABDZGIKcSlS0KshcQSly2EOE+BCCQa4nK5nkEpAMhdhi2sOE+BPIgkHuJyuZlBMRCiACyhRD3IZAWATaQxf2JgBRkRQpbEBTNIBAhAd7vak5DQCpwY4dSARqPQCBAAlQYunMKAtIdPyEAuwTI4xBomAAbwPqAIyA1sSQFrgkk3UDAAQHeTwdQRQQBccCVHY4DqHQJgQoEqBBUgFbiEQSkBKwqTQngKtR4BgLVCbCBq86u7JMISFliFduTQlcEx2MQKECA96sAJAdNEBAHUG1dskOyEeI+BIoRIMMvxslVKwTEFdmC/fICFARFMwj8S4ANWDihgIAE4gtS8EAcgRlBEuD9CNItfAsrRLewwwrRK9jkgwAZug/qxcckAynOyktLXiAv2BnUIwE2UB7hlxwaASkJzFdzUnhf5Bm3CQLEdxOU6x8DAamfqfMe2aE5R8wADREgw24ItKNhEBBHYJvqlhewKdKMUxcBNkB1kfTfDwLi3we1WEAJoBaMdOKIAPHpCKznbhEQzw5wMTw7PBdU6bMKATLkKtTieQYBicdXlSzlBa6EjYe6IMAGpgt4kT2KgETmsKrmUkKoSo7nihAgvopQSq8NApKeT60zYodoRUSDggTIcAuCSrQZApKoY4tOiwWgKCnatQiwASEWWgQQEGLBEKAEQSAMRYD4ID46EUBAiIv/EWCHSVC0CJChEgtDEUBAiI8hCbCA5BcgbCDy83nVGSMgVcll9hwljLQdjn/T9q+r2SEgrsgm3C871HScS4aZji99zAQB8UE9oTFZgOJzJhuA+HwWqsUISKieicwuSiBhOwz/hO2fWK1DQGL1XMB2s8MNxzlkiOH4IkVLEJAUvRrQnFjAmncGAt4881xHREBy9XzD86aE4hY4fN3ypffOBBAQIqNxAuyQ60NOhlcfS3oqTwABKc+MJ2okwAJYHiYCXJ4ZT7ghgIC44UqvJQlQghkaGHxKBhTNGyGAgDSCmUHKEGCH/R8tMrQykUPbpgkgIE0TZ7xSBHJcQBHQUiFCY48EEBCP8Bm6OIHUSzipz6+4p2kZEwEEJCZvYashkNIOPccMizBOhwACko4vs5xJjAtwSgKYZdAx6TYBBIRgSIJA6CWg0O1LIgiYROMEEJDGkTOgawIh7fBjzJBc+4f+0yGAgKTjS2bSgYCPBTwkASMoIOCSAALiki59B0PAdQnJdf/BgMQQCAwggIAQDtkRqDND8JHhZOcwJhwsAQQkWNdgWBMEqghAnQLUxBwZAwKuCCAgrsjSb1QEbCUo2/2oJouxEKiJAAJSE0i6SYfAwAxj8803NxNbu3at9Pb2Sl9fn/T09KQzWWYCgS4IICBdwOPRNAkgIGn6lVnVTwABqZ8pPUZIwFaist2PcMqYDIGuCSAgXSOkg5gJcIges/ew3TcBBMS3Bxi/cQJ1fouqigA1PmEGhIAjAgiII7B0GxYB1yUo1/2HRRNrIPAPAQSESEiagI8Moc4MJ2nnMLnoCSAg0buQCQwmENIC7kPAiAgINEUAAWmKNOM4JRB6CSl0+5w6h86TJYCAJOvaPCYW4w4/pAwpjyhhlq4IICCuyNKvMwIpLcAxCqAzx9JxdAQQkOhclqfBqZeAUp9fnlGb/qwRkPR9HPUMc9yhp5RhRR18GG8lgIBYEdGgaQIsoP8Rz1FAm443xqtOAAGpzo4nayRACWdomPCpMdjoqjYCCEhtKOmoCgF22OWpkaGVZ8YTbgggIG640usQBFgA6wsPBLg+lvRUngACUp4ZT1QgQAmmArQSj8C3BCya1kYAAakNJR11IsAOufm4IMNrnnmuIyIguXre4bxZwBzCLdk1Al4SGM1LEUBASuGi8cYIUEIJOzbwT9j+idU6BCRWzwViNzvcQBxRwgwyxBKwaDokAQSEAClNgAWoNLJgH2ADEKxrojAMAYnCTf6NpATi3wcuLcC/Lumm2zcCkq5va5kZO9RaMEbVCRlmVO7yaiwC4hV/mIOzgITpFx9WsYHwQT2eMRGQeHzl1FJKGE7xRt858RG9C51MAAFxgjWeTtlhxuOrUCwlQw3FE/7tQED8+6BxC1gAGkee7IBsQJJ1baGJISCFMMXfiBJE/D4MeQbEV8jecWcbAuKObRA9s0MMwg1ZGUGGm4+7EZAEfc0LnKBTI50SG5hIHVfQbASkIKjQm1FCCN1DedtHfKbpfwQkcr+yw4vcgRmaT4acjtMRkAh9yQsYodMwuSMBNkBxBwYCEon/KAFE4ijMrESA+K6EzftDCIh3FwxtADu0wB2EebUTIMOuHamzDhEQZ2ird8wLVJ0dT6ZFgA1U2P5EQALxDyl8II7AjCAJ8H4E6RZBQDz7hR2WZwcwfHQEyNDDcRkC4sEXvAAeoDNkkgTYgPl1KwLSEH9S8IZAM0yWBHi//LgdAXHMnR2SY8B0D4FBBMjwmwsJBMQBawLYAVS6hEAFAmzgKkAr8QgCUgLWUE1JoWsCSTcQcECA99MBVBG+hdUtVnY43RLkeQg0S4AKQX28yUAqsCQAK0DjEQgESIANYHdOQUAK8iMFLgiKZhCIkADvdzWnISAWbuxQqgUWT0EgVgJUGIp7DgHpwIoAKh5AtIRAygTYQA7tXQTkXz6ksCkvA8wNAt0RYH3ozC97AWGH0d2LxdMQyI0AFYr/PJ6lgBAAub3yzBcCbgjkvgHNRkBIQd28QPQKAQiI5Lq+JC8gue8QeLkhAIFmCeRU4UhSQHJyYLOvBqNBAAJlCKS+gU1GQHJNIcsEM20hAAE/BFJdn6IXkNQV3k+4MyoEIOCKQEoVkigFJCUHuApS+oUABMInEPsGOBoBSTUFDD/EsRACEHBNINb1LXgBiV2hXQce/UMAAmkRiKnCEqSAxAQwrdBlNhCAQEgEQt9AByMgsaZwIQUbtkAAAmkSCHV99C4goStsmuHIrCAAgVgJhFSh8SIgIQGINYiwGwIQSIPARx99JFtssYXsuOOOG0zos88+k3Hjxpn/Bl/ff/+9rF27VrbaaitZuXKlrF69Wnp7e6Wvr096eno2Cub333+Xzz//XHbfffd2G81udCM/8Np0001lzJgx7Y9+/fVX+eqrr2TnnXeW4cOHtz9vTEBCTcHSCEFmAQEIxEhgyZIlsssuu8i1114rc+fONVPQhfrII4+UTz/91Px95plnyn333SebbLKJrFu3Tk466SR59tlnzb3JkyfL66+/3haSFStWmM9VSFRQBi72+vm8efPkvPPOk19++aWN691335W99957A3zHHnusPP/88+azG264Qa6++mrz/6NHjzbj7bPPPuZv5wJCiSrGsMZmCEDANYE//vhDDjroIFm0aNEGAnLCCSfId999J08//bTJFg499FC56667zMJ/2223GaF59dVXjUgcc8wxstdee8kTTzzRNrdThefjjz+WBx98UB599FHTbqCAPPXUU3LVVVeZ+61r7Nixsttuuxnb9ttvP3nggQdERWX27NnyyiuvyLJly0SzFCcCQonKdejRPwQgEDuBOXPmmF2+ZgnHH3+8EYbly5fL+PHj5eWXX5YjjjjCTPHkk082grJgwQLZY489TAaiGYted955p5x//vmyatUqOeOMM0wZ7Pbbbzf3ZsyYIT///LNcd9118vjjj8vChQvlyy+/lC+++GIDAbn55pulv7+/LS4DuV588cVGRN544w3z8fvvv2+yntdee00OO+yw+gSEElXs4Yz9EIBAUwRUDA455BDRzODcc881YqEC8tZbb8mBBx5oziT0fEOva665Rm699VYjErrrV9HRbEAvzQa03KX96JmJCtGTTz4petZx+umnG9HQDKK1Pt9xxx1yyy23yNKlS9slLs1s5s+fb85atN1ZZ50lp5xyimy99dZy1FFHmfLWTTfdZMbTs5BRo0bJvffeK9OmTeteQChRNRVyjAMBCKRA4McffzTloRtvvNEswgcffHBbQPSM4rTTTpO//vpLhg0bZqar5aOzzz7bZA+aYbz55ptywAEHmHutMxTNEvbdd1+ZOXOmPPzww+aelqWuuOKKDZBpKeyyyy6TDz74wBy+65mGnrFo5nP99debTEf/1XHefvtt2WmnneSCCy6Qyy+/vN2PZkjar2YnlUpYlKhSCGPmAAEI+CCgpaWXXnpJNBvQg3FdiPUcY/r06aacpRmIHobrIbhe2u7+++83C/pmm21mDtD1nESv9957zzyr2YlmDK0SmAqD9jFy5MiOAtI6A9EEQM8zNLPYbrvtzLmKnqdoFqLfDrvoootM2Uyzltalwvbcc8/J1KlTiwsIJSofocaYEIBAagQuvPBCIwatS7OHbbbZxgjHI488YkpErdKTtlHB0fX3nnvuMYfuJ554olxyySXmcT3buPTSS+Xrr782f8+aNcschuvXenXR10PvgVcrA2kJiCYDerB++OGHy5ZbbmlER20755xzjDho+3feeccIhl4qNjvssIM5R9EsxZqBUKJKLXyZDwQgEBIBPWdQYWh9jVfPLLbddlsjBC+88IIpaT322GNy6qmnypVXXmmyERUYLTdpCUyfvfvuu01bPRvRxf7DDz80pSrNUPTQu3UNFhD9XMdXwdGDcRUGPZQfMWKE6OH64sWLzRgvvvii7LrrriZb0j71vEUzkY4CQokqpPDCFghAIGUCgwVEF3/9hpOWo/TSM4jWN6s0czjuuOPMOYhe+++/vymH6e9D9Fzl6KOPloceesj8PWXKFPNtK/2GlR6+66VCoxnLwK/xqkjoQb7+q9fEiRPlmWeekUmTJome16hotL7iq5mSfoV4zz33NG3bAqIHI3qoYvshSsqOZG4QgAAEQiCgJatPPvlEJkyYIPqbjMGXHqjr+YmWk+q69AeMem2//fam74HXDz/8YOzRcxbNPFo/VFSRG9bf379eDdaf02v9TdMXLghAAAIQgMBgAprdrFmzxhy866H/36KkYbkUU3YLAAAAAElFTkSuQmCC";

    private Gson gson = new Gson();

    public PostMomentServlet() {
    }

    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HashMap<String,String> bodyMap = this.gson.fromJson((String)req.getReader().lines().collect(Collectors.joining()), HashMap.class);
        String id = bodyMap.get("id");
        String tags = bodyMap.get("tags");
        String title = bodyMap.get("title");
        String file = bodyMap.get("file");
        String fileWithoutBase64 = "";
        Asset imageAsset = null;
        if (StringUtils.isNotEmpty(file) && !file.equals("undefined")){
            fileWithoutBase64 = file.replace("data:image/png;base64,", "");
        }else{
            fileWithoutBase64 = EMPTY.replace("data:image/png;base64,", "");;
        }
            byte[] initialArray = Base64.decode(fileWithoutBase64.getBytes());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(initialArray);
            String fullAssetPath = "/content/dam/aem-pwa-blog/uploads/" + title + "_" + System.currentTimeMillis() + ".jpg";
            ResourceResolver resolver = req.getResourceResolver();
            AssetManager assetManager = (AssetManager)resolver.adaptTo(AssetManager.class);
            imageAsset = assetManager.createAsset(fullAssetPath, inputStream, "image/jpeg", true);


        if (StringUtils.isNotEmpty(tags)){
             String[] tagArray = tags.split(",");
        }


        HashMap<String,String> res = new HashMap<>();
        res.put("id",id);
        if(imageAsset != null) {
            res.put("msg","Post moment successfully created.");

        } else {
            res.put("msg","Post moment successfully had an issue.");
        }

        resolver.refresh();
        resolver.commit();
        resp.getWriter().write(this.gson.toJson(res));

    }
}
