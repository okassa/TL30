package com.adobe.summit.emea.core.services;


import com.google.gson.JsonObject;

import java.io.IOException;

public interface NotificationService {

    void sendCommonMessage(String title,String body) throws IOException;

    void sendMessage(JsonObject message) throws IOException ;
}
