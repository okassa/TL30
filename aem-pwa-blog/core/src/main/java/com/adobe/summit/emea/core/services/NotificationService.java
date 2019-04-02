package com.adobe.summit.emea.core.services;


import com.google.gson.JsonObject;

import java.io.IOException;

public interface NotificationService {

    String sendMessage(String title, String body, String topic);
}
