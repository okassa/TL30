package com.adobe.summit.emea.core.services.impl;

/**
 * Created by kassa on 05/04/2019.
 */

import com.adobe.summit.emea.core.services.NotificationService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <h1>FirebaseNotificationServiceImpl</h1>
 *
 * <p>
 * This service will be used to send web push notifications to a PWA installed on a smartphone.
 * <p>
 *
 * <b>Example :</b>
 * <pre>
 * NotificationService notificationService = new FirebaseNotificationServiceImpl() ;
 * </pre>
 *
 * @author Olympe Kassa &lt;kassa@adobe.com&gt;
 *
 */
@Component(
        service = NotificationService.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + " This service is based on the firebase admin-sdk notification service from (https://github.com/firebase/firebase-admin-java). It uses " +
                        "some features to suscribes and send web push notifications ",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM"
        }
)
public class FirebaseNotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseNotificationServiceImpl.class);

    private Gson gson = new Gson();


    @ObjectClassDefinition(name = "Airbus -  Hub channel Configuration", description = "Configure the groups allow to be used")
    public @interface FirebaseNotificationServiceConfiguration {

        @AttributeDefinition(
                name = "Type",
                description = "type",
                type = AttributeType.STRING
        )
        String type() default "__TO__BE__UPDATED__";

        @AttributeDefinition(
                name = "Project ID",
                description = "Project ID",
                type = AttributeType.STRING
        )
        String projectId() default "__TO__BE__UPDATED__";

        @AttributeDefinition(
                name = "Private Key ID",
                description = "Private Key ID",
                type = AttributeType.STRING
        )
        String privateKeyId() default "__TO__BE__UPDATED__";

        @AttributeDefinition(
                name = "Private Key",
                description = "Private Key",
                type = AttributeType.STRING
        )
        String privateKey() default "__TO__BE__UPDATED__";


        @AttributeDefinition(
                name = "Client Email",
                description = "Client Email",
                type = AttributeType.STRING
        )
        String clientEmail() default "__TO__BE__UPDATED__";

        @AttributeDefinition(
                name = "Client ID",
                description = "Client ID",
                type = AttributeType.STRING
        )
        String clientId() default "__TO__BE__UPDATED__";

        @AttributeDefinition(
                name = "Auth URI",
                description = "Auth URI",
                type = AttributeType.STRING
        )
        String authUri() default "__TO__BE__UPDATED__";

        @AttributeDefinition(
                name = "Token URI",
                description = "Token URI",
                type = AttributeType.STRING
        )
        String tokenUri() default "__TO__BE__UPDATED__";


        @AttributeDefinition(
                name = "Auth Provider",
                description = "Auth Provider",
                type = AttributeType.STRING
        )
        String authProvider() default "__TO__BE__UPDATED__";


        @AttributeDefinition(
                name = "Client Cert URL",
                description = "Client Cert URL",
                type = AttributeType.STRING
        )
        String clientCertUrl() default "__TO__BE__UPDATED__";

        @AttributeDefinition(
                name = "Database  URL",
                description = "Database  URL",
                type = AttributeType.STRING
        )
        String dataBaseUrl() default "__TO__BE__UPDATED__";
    }


    private FirebaseMessaging messaging ;

    @Activate
    public void activate(FirebaseNotificationServiceConfiguration configuration){
        try {

            Map<String,String> map = new HashMap<>();

            map.put("type", configuration.type());
            map.put("project_id", configuration.projectId());
            map.put("private_key_id", configuration.privateKeyId());
            map.put("private_key", configuration.privateKey());
            map.put("client_email", configuration.clientEmail());
            map.put("client_id", configuration.clientId());
            map.put("auth_uri", configuration.authUri());
            map.put("token_uri", configuration.tokenUri());
            map.put("auth_provider_x509_cert_url", configuration.authProvider());
            map.put("client_x509_cert_url", configuration.clientCertUrl());

            String initialString = gson.toJson(map);
            InputStream serviceAccount = new ByteArrayInputStream(initialString.getBytes());

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(configuration.dataBaseUrl())
                    .build();

            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
            this.messaging =  FirebaseMessaging.getInstance(firebaseApp);
        } catch (Exception e) {
            LOGGER.error("Error when activating the service",e);
        }
    }



    @Override
    public boolean sendCommonMessage(String title, String body, String token)  {
        try {

            Message message = Message.builder()
                    .setNotification(new Notification(title, body))
                    .setToken(token)
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(new WebpushNotification(title, body))
                            .build())
                    .build();
            String res = messaging.send(message);

            if (!res.isEmpty()){
                return true;
            }
            LOGGER.debug("Message sent : {}",res);

        } catch (Exception e) {
            LOGGER.error("Error when sending a message to this token :"+token,e);
        }
        return false;
    }

    @Override
    public boolean sendSubscriptionMessage(String token, List<String> hobbies)  {
       final Long count = hobbies.stream().map(topic -> {
                try {
                    TopicManagementResponse response = messaging.subscribeToTopic(Collections.singletonList(token), topic);
                    Long resCount = Long.valueOf(response.getSuccessCount());
                    LOGGER.error("Topic subscription sent : {}",resCount);
                    return resCount;
                } catch (Exception e) {
                    LOGGER.error("Error when suscribing to this topic :"+topic,e);
                    return Long.valueOf(0);
                }
            }).collect(Collectors.counting());

        if (count == hobbies.size()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean sendTopicMessage(String title, String body, String topic)  {
        try {

            Message message = Message.builder()
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(new WebpushNotification(
                                    title,
                                    body,
                                    "https://www.adobe.com/content/dam/www/icons/adobe-experience-manager.svg"))
                            .build())
                    .setTopic(topic)
                    .build();
            String res = messaging.send(message);
            LOGGER.error("Message sent : {}",res);
            if (!res.isEmpty()){
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Error when sending a message to this topic :"+topic,e);
        }
        return false;
    }
}
