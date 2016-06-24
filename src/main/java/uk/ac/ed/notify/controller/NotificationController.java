package uk.ac.ed.notify.controller;

import com.mangofactory.swagger.models.dto.OAuth;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.notify.NotificationCategory;
import uk.ac.ed.notify.NotificationEntry;
import uk.ac.ed.notify.NotificationError;
import uk.ac.ed.notify.NotificationResponse;
import uk.ac.ed.notify.entity.*;
import uk.ac.ed.notify.repository.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;

/**
 * Created by rgood on 18/09/2015.
 */
@RestController
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

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

    @Autowired
    UserNotificationAuditRepository userNotificationAuditRepository;

    @Autowired
    NotificationErrorRepository notificationErrorRepository;

    @ApiOperation(value="Get a specific notification",notes="Requires notification id to look up",
    authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @RequestMapping(value="/notification/{notification-id}",method= RequestMethod.GET)
    public @ResponseBody
    Notification getNotification(@PathVariable("notification-id") String notificationId, HttpServletResponse httpServletResponse, OAuth2Authentication authentication) throws ServletException {


        if (notificationId.equals(""))
        {
            logger.warn("getNotification called with no notification-id");
            throw new ServletException("You must provide a notification-id");
        }

        long expires = (new Date()).getTime()+cacheExpiry;

        httpServletResponse.setHeader("cache-control", "public, max-age=" + cacheExpiry/1000 + ", cache");
        httpServletResponse.setDateHeader("Expires", expires);

       if (authentication!=null&&authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
        {
            Notification notification = new Notification();
            notification.setBody("Test notification");
            notification.setTitle("Test title");
            notification.setStartDate(new Date());
            notification.setEndDate(new Date());
            notification.setTopic("Test topic");
            notification.setUrl("http://www.ed.ac.uk");
            notification.setUun("Test uun");
            notification.setLastUpdated(new Date());
            return notification;

        }

        return notificationRepository.findOne(notificationId);
    }

    @ApiOperation(value="Get all publisher notifiations",notes="Gets all notifications for a specific publisher",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @RequestMapping(value="/notification/publisher/{publisher-id}",method = RequestMethod.GET)
    public @ResponseBody List<Notification> getPublisherNotifications(@PathVariable("publisher-id") String publisherId,Principal principal,HttpServletRequest request,OAuth2Authentication authentication) throws ServletException {

        if (authentication!=null&&authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
        {
            List<Notification> notifications = new ArrayList<>();
            Notification notification = new Notification();
            notification.setBody("Test notification");
            notification.setTitle("Test title");
            notification.setStartDate(new Date());
            notification.setEndDate(new Date());
            notification.setTopic("Test topic");
            notification.setUrl("http://www.ed.ac.uk");
            notification.setUun("Test uun");
            notification.setLastUpdated(new Date());
            notifications.add(notification);
            notification.setTitle("Test title 2");
            notification.setBody("Test notification 2");
            notifications.add(notification);
            return notifications;

        }

        PublisherDetails publisherDetails = publisherDetailsRepository.findOne(publisherId);
        if (publisherDetails==null||!publisherDetails.getStatus().equals("A"))
        {
            logger.error("getPublisherNotifications called with invalid/inactive publisher");
            throw new ServletException("Invalid publisher or publisher is inactive");
        }
        try
        {
            return notificationRepository.findByPublisherId(publisherId);
        }
        catch (Exception e)
        {
            logger.error("Error getting publisher notifications",e);
            throw new ServletException("Error getting publisher notifications");
        }
    }

    @ApiOperation(value="Create a new notification",notes="Requires a valid notification object. For creation DO NOT specify notificationId, one will be automatically generated.",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.write",description = "Write access to notification API")})})
    @RequestMapping(value="/notification/", method=RequestMethod.POST)
    public @ResponseBody Notification setNotification(@RequestBody Notification notification, OAuth2Authentication authentication) throws ServletException {

        if (authentication!=null&&authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
        {
            notification.setNotificationId("12345");
            return notification;
        }
        if (!notification.getTopic().equals("Emergency")&&notification.getUun()==null)
        {
            throw new ServletException("Must set UUN for non broadcast notifications");
        }
        try
        {
            notification.setNotificationId(null);
            notificationRepository.save(notification);
            UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
            userNotificationAudit.setAction(AuditActions.CREATE_NOTIFICATION);
            userNotificationAudit.setAuditDate(new Date());
            userNotificationAudit.setPublisherId(notification.getPublisherId());
            userNotificationAudit.setUun(notification.getUun());
            userNotificationAuditRepository.save(userNotificationAudit);
            return notification;
        }
        catch (Exception e)
        {
            logger.error("Error saving notification",e);
            uk.ac.ed.notify.entity.NotificationError notificationError = new uk.ac.ed.notify.entity.NotificationError();
            notificationError.setErrorCode(ErrorCodes.SAVE_ERROR);
            notificationError.setErrorDescription(e.getMessage());
            notificationError.setErrorDate(new Date());
            notificationErrorRepository.save(notificationError);
            throw new ServletException("Error saving notification");
        }

    }

    @ApiOperation(value="Update notification",notes="Requires a valid notification object",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.write",description = "Write access to notification API")})})
    @RequestMapping(value="/notification/{notification-id}",method=RequestMethod.PUT)
    public void updateNotification(@PathVariable("notification-id") String notificationId, @RequestBody Notification notification, OAuth2Authentication authentication) throws ServletException {

        if (!notificationId.equals(notification.getNotificationId()))
        {
            throw new ServletException("Notification Id and notification body do not match");
        }
        if (!notification.getTopic().equals("Emergency")&&notification.getUun()==null)
        {
            throw new ServletException("Must set UUN for non broadcast notifications");
        }

        if (authentication!=null&&authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
        {
            return;
        }
        Notification one = notificationRepository.findOne(notificationId);

        if (one==null)
        {
            throw new ServletException("Notification not found");
        }

        if (!one.getPublisherId().equals(notification.getPublisherId()))
        {
            throw new ServletException("Cannot change publisher Id once set.");
        }

        try
        {
            notificationRepository.save(notification);
            UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
            userNotificationAudit.setAction(AuditActions.UPDATE_NOTIFICATION);
            userNotificationAudit.setAuditDate(new Date());
            userNotificationAudit.setPublisherId(notification.getPublisherId());
            userNotificationAudit.setUun(notification.getUun());
            userNotificationAuditRepository.save(userNotificationAudit);
        }
        catch (Exception e)
        {
            logger.error("Error saving notification",e);
            uk.ac.ed.notify.entity.NotificationError notificationError = new uk.ac.ed.notify.entity.NotificationError();
            notificationError.setErrorCode(ErrorCodes.SAVE_ERROR);
            notificationError.setErrorDescription(e.getMessage());
            notificationError.setErrorDate(new Date());
            notificationErrorRepository.save(notificationError);
            throw new ServletException("Error saving notification");
        }

    }

    @ApiOperation(value="Delete a notification",notes="Requires a valid notification-id",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.write",description = "Write access to notification API")})})
    @RequestMapping(value="/notification/{notification-id}",method=RequestMethod.DELETE)
    public void deleteNotification(@PathVariable("notification-id") String notificationId,Principal principal,OAuth2Authentication authentication) throws ServletException {

        if (authentication!=null&&authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
        {
            return;
        }

        try
        {
            Notification notification = notificationRepository.findOne(notificationId);
            notificationRepository.delete(notificationId);
            UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
            userNotificationAudit.setAction(AuditActions.DELETE_NOTIFICATION);
            userNotificationAudit.setAuditDate(new Date());
            userNotificationAudit.setPublisherId(notification.getPublisherId());
            userNotificationAudit.setUun(notification.getUun());
            userNotificationAuditRepository.save(userNotificationAudit);
        }
        catch (Exception e)
        {
            logger.error("Error deleting notification",e);
            uk.ac.ed.notify.entity.NotificationError notificationError = new uk.ac.ed.notify.entity.NotificationError();
            notificationError.setErrorCode(ErrorCodes.DELETE_ERROR);
            notificationError.setErrorDescription(e.getMessage());
            notificationError.setErrorDate(new Date());
            notificationErrorRepository.save(notificationError);
            throw new ServletException("Error deleting notification");
        }

    }



    @ApiOperation(value="Get a list of categories containing notifications for a user",notes="Requires subcriber id to look up, and uun of user",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @RequestMapping(value="/usernotifications/{subscriber-id}",method= RequestMethod.GET)
    public @ResponseBody
    NotificationResponse getUserNotificationsBySubscription(@PathVariable("subscriber-id") String subscriberId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
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

            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();
            cal.add(Calendar.YEAR, 1); // to get previous year add -1
            Date nextYear = cal.getTime();

            Date dateNow = new Date();
            List<NotificationCategory> categories = new ArrayList<NotificationCategory>();
            NotificationCategory category;
            NotificationEntry entry;
            List<Notification> notificationList;
            List<NotificationEntry> entries;
            for (TopicSubscription topicSubscription : topicSubscriptionList) {
                category = new NotificationCategory();
                category.setTitle(topicSubscription.getTopic());
                entries = new ArrayList<NotificationEntry>();
                notificationList = notificationRepository.findByUunTopicAndDate(uun, category.getTitle(), dateNow);
                for (Notification notification : notificationList) {
                    entry = new NotificationEntry();
                    entry.setBody(notification.getBody());
                    entry.setTitle(notification.getTitle());
                    if (notification.getEndDate()==null)
                    {
                        entry.setDueDate(nextYear);
                    }
                    else
                    {
                        entry.setDueDate(notification.getEndDate());
                    }
                    entry.setUrl(notification.getUrl());
                    entries.add(entry);
                }

                category.setEntries(entries);
                if (entries.size()>0)
                {
                    categories.add(category);
                }

            }

            notificationResponse.setCategories(categories);

        }
        catch (Exception e)
        {
            logger.error("Error building user notifications",e);
            uk.ac.ed.notify.entity.NotificationError notificationError = new uk.ac.ed.notify.entity.NotificationError();
            notificationError.setErrorCode(ErrorCodes.GET_ERROR);
            notificationError.setErrorDescription(e.getMessage());
            notificationError.setErrorDate(new Date());
            notificationErrorRepository.save(notificationError);
            List<NotificationError> errors = new ArrayList<NotificationError>();
            errors.add(new NotificationError("Error while producing feed", "Notification Backbone"));
            notificationResponse.setErrors(errors);
        }

        return notificationResponse;
    }

    @ApiOperation(value="Get all emergency notifications",notes="Independant of users",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @RequestMapping(value="/emergencynotifications",method= RequestMethod.GET)
    public @ResponseBody
    NotificationResponse getEmergencyNotifications(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,Principal principal,HttpServletRequest request,OAuth2Authentication authentication) {

        NotificationResponse notificationResponse = new NotificationResponse();

        if (authentication!=null&&authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
        {
            Date dateNow = new Date();
            List<NotificationCategory> categories = new ArrayList<NotificationCategory>();
            NotificationCategory category;
            NotificationEntry entry;
            List<Notification> notificationList;
            List<NotificationEntry> entries;

            category = new NotificationCategory();
            category.setTitle("Emergency");
            entries = new ArrayList<NotificationEntry>();

                entry = new NotificationEntry();
                entry.setBody("This is a test");
                entry.setTitle("Test title");
                entry.setDueDate(dateNow);
                entry.setUrl("http://www.ed.ac.uk");
                entries.add(entry);


            category.setEntries(entries);
            categories.add(category);

            notificationResponse.setCategories(categories);
            return notificationResponse;
        }

        try {

            Date dateNow = new Date();
            List<NotificationCategory> categories = new ArrayList<NotificationCategory>();
            NotificationCategory category;
            NotificationEntry entry;
            List<Notification> notificationList;
            List<NotificationEntry> entries;

            category = new NotificationCategory();
            category.setTitle("Emergency");
            entries = new ArrayList<NotificationEntry>();
            notificationList = notificationRepository.findByPublisherIdAndDate("notify-ui", dateNow);
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

            notificationResponse.setCategories(categories);

        } catch (Exception e) {
            logger.error("Error building user notifications", e);
            uk.ac.ed.notify.entity.NotificationError notificationError = new uk.ac.ed.notify.entity.NotificationError();
            notificationError.setErrorCode(ErrorCodes.GET_ERROR);
            notificationError.setErrorDescription(e.getMessage());
            notificationError.setErrorDate(new Date());
            notificationErrorRepository.save(notificationError);
            List<NotificationError> errors = new ArrayList<NotificationError>();
            errors.add(new NotificationError("Error while producing feed", "Notification Backbone"));
            notificationResponse.setErrors(errors);
        }

        return notificationResponse;
    }
    
    
    @ApiOperation(value="Get all notifications for a user",notes="Requires uun to look up",
    	    authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    	    @RequestMapping(value="/notification/user/{uun}", method= RequestMethod.GET)
    	    public List<Notification> getUserNotifications(@PathVariable("uun") String uun, HttpServletResponse httpServletResponse) throws ServletException {

    	        if (uun.equals(""))
    	        {
    	            logger.warn("getUserNotifications called with no uun");
    	            throw new ServletException("You must provide a uun");
    	        }

    	        long expires = (new Date()).getTime()+cacheExpiry;

    	        httpServletResponse.setHeader("cache-control", "public, max-age=" + cacheExpiry/1000 + ", cache");
    	        httpServletResponse.setDateHeader("Expires", expires);

    	        return notificationRepository.findByUun(uun);
    	    }
}