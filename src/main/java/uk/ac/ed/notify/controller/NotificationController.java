package uk.ac.ed.notify.controller;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.notify.NotificationCategory;
import uk.ac.ed.notify.NotificationEntry;
import uk.ac.ed.notify.NotificationError;
import uk.ac.ed.notify.NotificationResponse;
import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.entity.SubscriberDetails;
import uk.ac.ed.notify.entity.TopicSubscription;
import uk.ac.ed.notify.repository.NotificationRepository;
import uk.ac.ed.notify.repository.PublisherDetailsRepository;
import uk.ac.ed.notify.repository.SubscriberDetailsRepository;
import uk.ac.ed.notify.repository.TopicSubscriptionRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rgood on 18/09/2015.
 */
@RestController
public class NotificationController {

    //TODO Logging
    //TODO Audit
    //TODO DB error logging

    @Value("${cache.expiry}")
    int cacheExpiry;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    PublisherDetailsRepository publisherDetailsRepository;

    @Autowired
    SubscriberDetailsRepository subscriberDetailsRepository;

    @Autowired
    TopicSubscriptionRepository topicSubscriptionRepository;

    @ApiOperation(value="Get a specific notification",notes="Requires notification id to look up",
    authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @RequestMapping(value="/notification/{notification-id}",method= RequestMethod.GET)
    public @ResponseBody
    Notification getNotification(@PathVariable("notification-id") String notificationId, HttpServletResponse httpServletResponse) throws ServletException {

        if (notificationId.equals(""))
        {
            throw new ServletException("You must provide a notification-id");
        }
        long expires = (new Date()).getTime()+cacheExpiry;

        httpServletResponse.setHeader("cache-control", "public, max-age=" + cacheExpiry/1000 + ", cache");
        httpServletResponse.setDateHeader("Expires", expires);

        return notificationRepository.findOne(notificationId);
    }

    @ApiOperation(value="Get a specific notification",notes="Gets all notifications for a specific publisher",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @RequestMapping(value="/notification/publisher/{publisher-id}",method = RequestMethod.GET)
    public @ResponseBody List<Notification> getPublisherNotifications(@PathVariable("publisher-id") String publisherId,Principal principal,HttpServletRequest request,OAuth2Authentication authentication)
    {
        //TODO restrict to client = publisherId
        return notificationRepository.findByPublisherId(publisherId);
    }

    @ApiOperation(value="Create a new notification",notes="Requires a valid notification object",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.write",description = "Write access to notification API")})})
    @RequestMapping(value="/notification/", method=RequestMethod.POST)
    public @ResponseBody Notification setNotification(@RequestBody Notification notification)
    {
        //TODO Check that publisher ID is valid
        //TODO Check that variables are valid
        //TODO Add audit row
        //TODO Log errors
        notificationRepository.save(notification);
        return notification;
    }

    @ApiOperation(value="Get a list of categories containing notifications for a user",notes="Requires subcriber id to look up, and uun of user",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @RequestMapping(value="/usernotifications/{subscriber-id}",method= RequestMethod.GET)
    public @ResponseBody
    NotificationResponse getNoticationByUser(@PathVariable("subscriber-id") String subscriberId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException
    {
        String uun = httpServletRequest.getParameter("user.login.id");

        NotificationResponse notificationResponse = new NotificationResponse();

        if (uun==null)
        {
            List<NotificationError> errors = new ArrayList<NotificationError>();
            errors.add(new NotificationError("No UUN provided", "Notification Backbone"));
            notificationResponse.setErrors(errors);
            return notificationResponse;
        }

        SubscriberDetails subscriberDetails = subscriberDetailsRepository.findOne(subscriberId);

        if (subscriberDetails==null||!subscriberDetails.getStatus().equals("A"))
        {
            List<NotificationError> errors = new ArrayList<NotificationError>();
            errors.add(new NotificationError("Invalid subscriber or subscriber inactive", "Notification Backbone"));
            notificationResponse.setErrors(errors);
            return notificationResponse;
        }

        try {
            List<TopicSubscription> topicSubscriptionList = topicSubscriptionRepository.findBySubscriberId(subscriberId);

            List<NotificationCategory> categories = new ArrayList<NotificationCategory>();
            NotificationCategory category;
            NotificationEntry entry;
            List<Notification> notificationList;
            List<NotificationEntry> entries;
            for (TopicSubscription topicSubscription : topicSubscriptionList) {
                category = new NotificationCategory();
                category.setTitle(topicSubscription.getTopic());
                entries = new ArrayList<NotificationEntry>();
                notificationList = notificationRepository.findByUunAndTopic(uun, category.getTitle());
                for (Notification notification : notificationList) {
                    entry = new NotificationEntry();
                    entry.setBody(notification.getBody());
                    entry.setTitle(notification.getTitle());
                    entry.setDueDate(notification.getEndDate());
                    entry.setUrl(notification.getUrl());
                    entries.add(entry);
                }

                category.setEntries(entries);
                categories.add(category);
            }
            notificationResponse.setCategories(categories);

        }
        catch (Exception e)
        {
            List<NotificationError> errors = new ArrayList<NotificationError>();
            errors.add(new NotificationError("Error while producing feed", "Notification Backbone"));
            notificationResponse.setErrors(errors);
        }

        return notificationResponse;
    }

}
