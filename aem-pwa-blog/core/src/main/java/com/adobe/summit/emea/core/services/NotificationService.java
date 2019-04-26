package com.adobe.summit.emea.core.services;


import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

/**
 * This service will send notifications to Firebase using the Firebase Admin Java SDK.
 */
public interface NotificationService {

    boolean sendCommonMessage(String title, String body, String token) ;

    boolean sendSubscriptionMessage(String token, List<String> hobbies) ;

    boolean sendTopicMessage(String title, String body, String topic) ;
}
