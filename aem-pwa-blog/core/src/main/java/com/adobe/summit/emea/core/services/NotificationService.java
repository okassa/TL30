package com.adobe.summit.emea.core.services;


import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

/**
 * This service will send notifications to Firebase using the Firebase Admin Java SDK.
 */
public interface NotificationService {

    void sendCommonMessage(String title,String body,String token) throws IOException;

    void sendSubscriptionMessage(String token,List<String> hobbies) throws IOException;

    void sendUnsubscriptionMessage(String title,String body,String[] topics) throws IOException;
}
