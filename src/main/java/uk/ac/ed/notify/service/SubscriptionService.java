package uk.ac.ed.notify.service;

import org.springframework.stereotype.Service;
import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.entity.SubscriptionMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgood on 25/11/2016.
 */
@Service
public class SubscriptionService {

    private List<SubscriptionMessage> subscriptionMessageList;

    public SubscriptionService()
    {
        subscriptionMessageList = new ArrayList<>();
    }

    public void addSubscriber(SubscriptionMessage subscriptionMessage)
    {
        subscriptionMessageList.add(subscriptionMessage);
    }

    public List<SubscriptionMessage> getSubscriptionMessageList() {
        return subscriptionMessageList;
    }

    public void setSubscriptionMessageList(List<SubscriptionMessage> subscriptionMessageList) {
        this.subscriptionMessageList = subscriptionMessageList;
    }

    public void notifySubscribers(Notification notification)
    {
        for (SubscriptionMessage subscriptionMessage : subscriptionMessageList)
        {

        }
    }
}
