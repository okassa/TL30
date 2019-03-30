package com.adobe.summit.emea.core.services;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.Map;

public interface NotificationService {

    public void suscribe(Object suscribtion);

    public Map<String,String> generateKeys() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException;
}
