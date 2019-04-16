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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


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

    private static HashMap<String,Object> map = new HashMap<>();


    static {
        map.put("type", "service_account");
        map.put("project_id", "aem-pwa-blog");
        map.put("private_key_id", "766236e578315609e4f51939c3dc6aa499db0f4c");
        map.put("private_key", "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCswJ0VFXQuWf0m\nlHAebL1iQG79O0LAobNOKmrH+MBdeMzFv1iCOI/T7jRvN14hzKo9MM3FAjqdD3rO\nHrLbwHWqXPr9XbBItJAHCr9HYNyTTBp0SifjyZI6sSLeURB37m2/NMOe8imyx9Ks\nt7bNYURwOJ9tv3TEajrj8Ac+KInxPSk2GWMqlkyXnFXSblhMtz/as79uyKTfAd80\ndv1/Dvr6qfgaCCFf4Tk5n7Hzr0b1q38HR9jIxRLul0dBsMIoaZx44I4xOm9ioxvF\ngG9Rf0+1Tos1BGmukq799tp8d7hj0Y8dcFxCFUiYH5Hw7JN4gENImZB+6y112grK\nOTX4LhwRAgMBAAECggEAEP0cNY+XjlthLuMYJ5XQBhKKF7M1PFZqmLxJJgNLf6W6\nlZPcs2m2k0PwuiM6yZ4j+8KJ6d7vPrTgAdc5Ba1mpOth73rANFY1d8vRxcY7yuyI\nCXmldJxFGiqDYFOeh/zVpCEfP8lW2nMWP9ANVSNEpLiczSEv7YT456OaQr2f2y5b\n8c8pTUIhGAIcFPWIPcqe8rvgI5v+QeCLDbeX6WWnUbHH1Kngx0QnrUcbGgc+5ryY\nGHXDnUuqaDM1cLvGUof2F23WkxHYQYccC2sIMkoez0iXK0dkmVspGZjpAzCZDX51\nM+U/J9D4S/iAfzxyw1gyRG4xcv2DwOHhLNihwh9jMQKBgQDsmLuxlJhUVNAM4Urg\n6IZ3dnhiFyGkFEav7r6/xeHoBMgGtkNMufUiwVaUng8rvfgsJOk/wPMqPn3izTAm\nMHsoRvNhIse8rBCPlSjWDl3I+jVxMmzfYDU1phdyP65KnvJqwD3fUAwPVKAyIWMS\nCRdR51dmfko8hqGUMKcY8PHg9QKBgQC6634PyWnMKlyWMi06ze680ZcLicZO7X6n\nLcg5PDtigdq7jO5caGXBLBwiuy1vd0YPCgQwwuS1ae2OFa4012OQx0d2BuyWiVDo\nAI4cXha345jnpkI5moTwMn6+EedohbIIB3ZKD4slSVgw6TLqG4V6T+HYm4BNB/EA\n4Oq0z5StLQKBgH/F6C96AV4hw44BKZAW42+mwlKvDVLwRFCFDezBcEP+OQwu6F+K\nWdGgOiLezXChEmK8uF7e1DOvNgsDJwqoygoxbwS5ZMcG4Za5Ril77rg4MB5mzhdA\nfNvxS94+d+ECwAZYtdFhCHJLEIyiLT+zOy3XUwMeFvdi+eXu9H7quKX1AoGAfSDo\nYukSSfqRwHoWjScOiphD5RV2C6AxCPk88BPCLU+Afcz6RCIe/BHrQ9TJtbTC0Y6C\n/6F4OXmP7W3WEMmffWvqCrjX6G5EGwtEFvlle/SAh5JlAurN0336GoMhxna5l2Zn\naWy+WVegEh4KV23VDOws5DQ0z1xhmZT3idLsVIECgYAIE5WlZndveiy+nz9BbgAS\nc/bw5Cz7tD8p73jxNXoTw9JGTl+DuupxqPyckowb1PyHsYFLcBSlNWZK+jUedJqR\nH0lwEWWzOHu3soLFstyVcpJx0i0wSJ5ZfSHKvL3jpbTGKEXq8Wvur3xFxF77ZkYh\ncvWL95Xpcd9CdeGw4fVdqg==\n-----END PRIVATE KEY-----\n");
        map.put("client_email", "firebase-adminsdk-3a6rf@aem-pwa-blog.iam.gserviceaccount.com");
        map.put("client_id", "100928771033965663025");
        map.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
        map.put("token_uri", "https://oauth2.googleapis.com/token");
        map.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
        map.put("client_x509_cert_url", "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-3a6rf%40aem-pwa-blog.iam.gserviceaccount.com");
    }



    private FirebaseMessaging messaging ;

    @Activate
    public void activate(){
        try {

            String initialString = gson.toJson(map);
            InputStream serviceAccount = new ByteArrayInputStream(initialString.getBytes());

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://aem-pwa-blog.firebaseio.com")
                    .build();

            FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
            this.messaging =  FirebaseMessaging.getInstance(firebaseApp);
        } catch (Exception e) {
            LOGGER.error("Error when activating the service",e);
        }
    }



    @Override
    public void sendCommonMessage(String title, String body, String token) throws IOException {
        try {

            Message message = Message.builder()
                    .setNotification(new Notification(title, body))
                    .setToken(token)
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(new WebpushNotification(title, body))
                            .build())
                    .build();
            String res = messaging.send(message);;

            LOGGER.error("Message sent : {}",res);

        } catch (Exception e) {
            LOGGER.error("Error when sending a message to this token :"+token,e);
        }
    }

    @Override
    public void sendSubscriptionMessage(String token, List<String> hobbies)  {
            hobbies.stream().forEach(topic -> {
                try {
                    TopicManagementResponse response = messaging.subscribeToTopic(Collections.singletonList(token), topic);
                    int count  = response.getSuccessCount();
                    LOGGER.error("Topic subscription sent : {}",count);
                } catch (Exception e) {
                    LOGGER.error("Error when suscribing to this topic :"+topic,e);
                }
            });
    }

    @Override
    public void sendTopicMessage(String title, String body, String topic) throws IOException {
        try {

            Message message = Message.builder()
                    .setNotification(new Notification(title, body))
                    .setTopic(topic)
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(new WebpushNotification(title, body))
                            .build())
                    .build();
            String res = messaging.send(message);;

            LOGGER.error("Message sent : {}",res);

        } catch (Exception e) {
            LOGGER.error("Error when sending a message to this topic :"+topic,e);
        }
    }
}
