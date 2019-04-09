package com.adobe.summit.emea.core.services;


import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

public interface NotificationService {

    void sendCommonMessage(String title,String body) throws IOException;

    void sendSubscriptionMessage(String token,List<String> hobbies) throws IOException;

    void sendUnsubscriptionMessage(String title,String body,String[] topics) throws IOException;
}
