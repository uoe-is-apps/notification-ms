package uk.ac.ed.notify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.entity.PushNotification;
import uk.ac.ed.notify.entity.SubscriptionMessage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgood on 25/11/2016.
 */
@Service
public class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    /** The Time to live of GCM notifications */
    private static final int TTL = 255;

    private List<SubscriptionMessage> subscriptionMessageList;

    public SubscriptionService()
    {
        subscriptionMessageList = new ArrayList<>();

        // Add BouncyCastle as an algorithm provider
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public void addSubscriber(SubscriptionMessage subscriptionMessage)
    {

        if (subscriptionMessage.getGcmApiKey()!=null)
        {
            subscriptionMessage.setUseGCM(true);
        }
        System.out.println(subscriptionMessage.getEndPoint());
        System.out.println(subscriptionMessage.getUun());
        System.out.println("GCM"+subscriptionMessage.isUseGCM());
        subscriptionMessageList.add(subscriptionMessage);
        System.out.println("added subscriber");
    }

    public List<SubscriptionMessage> getSubscriptionMessageList() {
        return subscriptionMessageList;
    }

    public void setSubscriptionMessageList(List<SubscriptionMessage> subscriptionMessageList) {
        this.subscriptionMessageList = subscriptionMessageList;
    }

    public void notifySubscribers(Notification notification)
    {
        PushNotification pushNotification = new PushNotification(notification);
        ObjectMapper objectMapper = new ObjectMapper();

        String notificationString;
        try
        {
            notificationString = objectMapper.writeValueAsString(pushNotification);
        }
        catch (Exception e)
        {
            notificationString ="";
        }


        for (SubscriptionMessage subscriptionMessage : subscriptionMessageList)
        {
            try {
                System.out.println("Trying to send a message"+subscriptionMessage.getEndPoint());
                sendPushMessage(subscriptionMessage, notificationString.getBytes());
            }
            catch (Exception e)
            {
                logger.error("Error pushing message",e);
                System.out.println("error"+e);
            }


        }
    }

    public void sendPushMessage(SubscriptionMessage sub, byte[] payload) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, JoseException, BadPaddingException, InvalidKeyException, IOException {

        // Figure out if we should use GCM for this notification somehow
        boolean useGcm = sub.isUseGCM();
        nl.martijndwars.webpush.Notification notification;
        PushService pushService;
        //payload = payload.getBytes();

        System.out.println("initialised");
        if (!useGcm) {
            System.out.println("not using cloud messaging");
            // Create a notification with the endpoint, userPublicKey from the subscription and a custom payload
            notification = new nl.martijndwars.webpush.Notification(
                    sub.getEndPoint(),
                    sub.getUserPublicKey(),
                    sub.getAuthAsBytes(),
                    payload
            );

            // Instantiate the push service, no need to use an API key for Push API
            pushService = new PushService();
        } else {
            // Or create a GcmNotification, in case of Google Cloud Messaging
            notification = new nl.martijndwars.webpush.Notification(
                    sub.getEndPoint(),
                    sub.getUserPublicKey(),
                    sub.getAuthAsBytes(),
                    payload,
                    TTL
            );

            // Instantiate the push service with a GCM API key
            pushService = new PushService();
            pushService.setGcmApiKey(sub.getGcmApiKey());
        }

        System.out.println("Sending");
        // Send the notification
        pushService.send(notification);
        System.out.println("Sent");
    }


}
