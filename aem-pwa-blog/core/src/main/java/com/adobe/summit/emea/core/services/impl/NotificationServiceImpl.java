package com.adobe.summit.emea.core.services.impl;

/**
 * Created by kassa on 05/04/2019.
 */

import com.adobe.summit.emea.core.services.NotificationService;
import com.adobe.summit.emea.core.servlets.ManifestServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Firebase Cloud Messaging (FCM) can be used to send messages to clients on iOS, Android and Web.
 *
 * This sample uses FCM to send two types of messages to clients that are subscribed to the `news`
 * topic. One type of message is a simple notification message (display message). The other is
 * a notification message (display notification) with platform specific customizations, for example,
 * a badge is added to messages that are sent to iOS devices.
 */
/**
 * <h1>NotificationServiceImpl</h1>
 *
 * <p>
 * This service will be used to send web push notifications to a PWA installed on a smartphone.
 * <p>
 *
 * <b>Example :</b>
 * <pre>
 * NotificationService notificationService = new NotificationServiceImpl() ;
 * </pre>
 *
 * @author Olympe Kassa &lt;kassa@adobe.com&gt;
 *

@Component(
        service = NotificationService.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + " This service is based on the firebase messaging sample file (https://github.com/firebase/quickstart-java/blob/master/messaging/src/main/java/com/google/firebase/quickstart/Messaging.java). It uses " +
                        "some features to suscribes and send web push notifications ",
                Constants.SERVICE_VENDOR + "=Adobe Summit EMEA 2019 | Technical Lab 30 : Building a PWA with AEM"
        }
)
 */
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private static final String PROJECT_ID = "aem-pwa-blog";
    private static final String BASE_URL = "https://fcm.googleapis.com";
    private static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";

    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };

    public static final String MESSAGE_KEY = "message";

    private static final String apiKey = "AIzaSyDSJsppoDjFCppABijYv5IXiEADtbdp_tM";

    private static Gson gson = new Gson();

    private static HashMap<String,Object> map = new HashMap<>();

    private static GoogleCredential googleCredential ;

    private static String deviceRegistrationToken;

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

    @Activate
    @Modified
    public void activate(){

        try {
            String initialString = gson.toJson(map);
            InputStream serviceAccount = new ByteArrayInputStream(initialString.getBytes());
            googleCredential = GoogleCredential
                    .fromStream(serviceAccount)
                    .createScoped(Arrays.asList(SCOPES));
        } catch (Exception e) {
            LOGGER.error("Error when activating the service",e);
        }
    }

    /**
     * Retrieve a valid access token that can be use to authorize requests to the FCM REST
     * API.
     *
     * @return Access token.
     * @throws IOException
     */
    // [START retrieve_access_token]
    private static String getAccessToken() throws IOException {

        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }
    // [END retrieve_access_token]

    /**
     * Create HttpURLConnection that can be used for both retrieving and publishing.
     *
     * @return Base HttpURLConnection.
     * @throws IOException
     */
    private static HttpURLConnection getConnection(String apiUrl) throws IOException {
        // [START use_access_token]
        URL url = new URL(apiUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setReadTimeout(10000);
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
        // [END use_access_token]
    }

    /**
     * Send request to FCM message using HTTP.
     *
     * @param fcmMessage Body of the HTTP request.
     * @throws IOException
     */
    private static void sendMessage(JsonObject fcmMessage,String url) throws IOException {
        HttpURLConnection connection = getConnection(url);
        connection.setDoOutput(true);
        if (fcmMessage != null){
            connection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(fcmMessage.toString());
            LOGGER.debug("Message to send : {}",fcmMessage);
            outputStream.flush();
            outputStream.close();
        }else{
            connection.setRequestProperty("Authorization", "key= " + apiKey);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            String response = inputstreamToString(connection.getInputStream());
            LOGGER.debug("Message sent to Firebase for delivery, response: {}",response);
        } else {

            String response = inputstreamToString(connection.getErrorStream());
            LOGGER.error("Unable to send message to Firebase: {}",response);
        }
    }


    /**
     * Build the android payload that will customize how a message is received on Android.
     *
     * @return android payload of an FCM request.
     */
    private static JsonObject buildAndroidOverridePayload() {
        JsonObject androidNotification = new JsonObject();
        androidNotification.addProperty("click_action", "android.intent.action.MAIN");

        JsonObject androidNotificationPayload = new JsonObject();
        androidNotificationPayload.add("notification", androidNotification);

        return androidNotificationPayload;
    }

    /**
     * Build the apns payload that will customize how a message is received on iOS.
     *
     * @return apns payload of an FCM request.
     */
    private static JsonObject buildApnsHeadersOverridePayload() {
        JsonObject apnsHeaders = new JsonObject();
        apnsHeaders.addProperty("apns-priority", "10");

        return apnsHeaders;
    }

    /**
     * Build aps payload that will add a badge field to the message being sent to
     * iOS devices.
     *
     * @return JSON object with aps payload defined.
     */
    private static JsonObject buildApsOverridePayload() {
        JsonObject badgePayload = new JsonObject();
        badgePayload.addProperty("badge", 1);

        JsonObject apsPayload = new JsonObject();
        apsPayload.add("aps", badgePayload);

        return apsPayload;
    }


    /**
     * Construct the body of a notification message request.
     *
     * @return JSON of notification message.
     */
    private static JsonObject buildNotificationMessage(String title,String body,String token) {

        JsonObject jNotification = new JsonObject();
        jNotification.addProperty("title", title);
        jNotification.addProperty("body", body);

        JsonObject jMessage = new JsonObject();
        jMessage.add("notification", jNotification);
        //jMessage.addProperty("topic", "news");
        // For the exercise we will use a single Token
        // That should be confire by the user using a
        // sling:OsgiConfig node
        jMessage.addProperty("token", token);
        jMessage.addProperty("name", "push-notification-1");

        JsonObject jFcm = new JsonObject();
        jFcm.add(MESSAGE_KEY, jMessage);

        return jFcm;
    }

    /**
     * Read contents of InputStream into String.
     *
     * @param inputStream InputStream to read.
     * @return String containing contents of InputStream.
     * @throws IOException
     */
    private static String inputstreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    /**
     * Pretty print a JsonObject.
     *
     * @param jsonObject JsonObject to pretty print.
     */
    private static void prettyPrint(JsonObject jsonObject) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LOGGER.debug(gson.toJson(jsonObject) + "\n");
    }

    /**
     * Send notification message to FCM for delivery to registered devices.
     *
     * @throws IOException
     */
    @Override
    public void sendCommonMessage(String title, String body,String token) throws IOException {

        JsonObject notificationMessage = buildNotificationMessage(title,body,token);
        LOGGER.debug("FCM request body for message using common notification object: {}",notificationMessage);
        prettyPrint(notificationMessage);
        sendMessage(notificationMessage,BASE_URL + FCM_SEND_ENDPOINT);
    }

    @Override
    public void sendSubscriptionMessage(String token, List<String> topics) throws IOException {
        topics.stream().forEach(t -> {
            try {
                sendMessage(null,String.format("https://iid.googleapis.com/iid/v1/%s/rel/topics/%s",token,t));
            } catch (IOException e) {
                LOGGER.error("An error occured when sending the subscription to firebase.");
            }
        });
    }

    @Override
    public void sendTopicMessage(String title, String body, String topic) throws IOException {
        // @TODO : Implement it if needed
    }
}
