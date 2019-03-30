package com.adobe.summit.emea.core.services.impl;

import com.adobe.summit.emea.core.services.NotificationService;
import nl.martijndwars.webpush.Base64Encoder;
import nl.martijndwars.webpush.Utils;
import nl.martijndwars.webpush.cli.commands.GenerateKeyCommand;
import nl.martijndwars.webpush.cli.commands.SendNotificationCommand;
import nl.martijndwars.webpush.cli.handlers.GenerateKeyHandler;
import nl.martijndwars.webpush.cli.handlers.SendNotificationHandler;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import java.io.File;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.HashMap;
import java.util.Map;


@Component(service=NotificationService.class,
        name = "Notification Subscription Service"
        )
public class NotificationServiceImpl implements NotificationService {

    private GenerateKeyHandler generateKeyHandler = new GenerateKeyHandler(null);

    @Override
    public void suscribe(Object suscribtion) {

        Security.addProvider(new BouncyCastleProvider());

        try {
            Map<String,String> keys = generateKeys();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Map<String,String> generateKeys() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = generateKeyHandler.generateKeyPair();

        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();

        byte[] encodedPublicKey = Utils.encode((org.bouncycastle.jce.interfaces.ECPublicKey) publicKey);
        byte[] encodedPrivateKey = Utils.encode((org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey);


        System.out.println("PublicKey:");
        System.out.println(Base64Encoder.encodeUrl(encodedPublicKey));

        System.out.println("PrivateKey:");
        System.out.println(Base64Encoder.encodeUrl(encodedPrivateKey));

        Map<String,String> keys = new HashMap<>();

        keys.put("PublicKey",Base64Encoder.encodeUrl(encodedPublicKey));
        keys.put("PrivateKey",Base64Encoder.encodeUrl(encodedPrivateKey));

        return keys;
    }
}
