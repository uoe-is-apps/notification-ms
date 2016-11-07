package uk.ac.ed.notify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.springframework.security.core.context.SecurityContextImpl;
import java.util.List;
import java.text.SimpleDateFormat;
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
import uk.ac.ed.notify.NotificationStubResponse;
import uk.ac.ed.notify.entity.*;
import uk.ac.ed.notify.repository.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @ApiOperation(value="Get a specific notification",notes="Requires notification id to look up", response = Notification.class,
    authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})    
    @ApiResponses({@ApiResponse(code=404,message="Not found")})
    @RequestMapping(value="/notification/{notification-id}",method= RequestMethod.GET)
    public Notification getNotification(@PathVariable("notification-id") String notificationId, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws ServletException {

        OAuth2Authentication authentication = null;
        try {
            authentication = (OAuth2Authentication)((SecurityContextImpl)httpServletRequest.getSession().getAttribute(("SPRING_SECURITY_CONTEXT"))).getAuthentication();   
    	} catch (Exception e) {}
        
        long expires = (new Date()).getTime()+cacheExpiry;

        httpServletResponse.setHeader("cache-control", "public, max-age=" + cacheExpiry/1000 + ", cache");
        httpServletResponse.setDateHeader("Expires", expires);

        try{
        	if (authentication != null && authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
                {
                return NotificationStubResponse.getSingleNotification();
                }
        	return notificationRepository.findOne(notificationId);
        }
        catch (Exception e)
        {
            logger.error("Error retrieving notification details",e);
            throw new ServletException("Error retrieving notification details");
        }
    }

    @ApiOperation(value="Get notifications by publisher",notes="Retrieves all notifications for a specific publisher", response = Notification.class,responseContainer = "List",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @ApiResponses({@ApiResponse(code=404,message="Not found")})
    @RequestMapping(value="/notifications/publisher/{publisher-id}",method = RequestMethod.GET)
    public List<Notification> getPublisherNotifications(@PathVariable("publisher-id") String publisherId, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws ServletException {

        OAuth2Authentication authentication = null;
        try {
            authentication = (OAuth2Authentication)((SecurityContextImpl)httpServletRequest.getSession().getAttribute(("SPRING_SECURITY_CONTEXT"))).getAuthentication();   
    	} catch (Exception e) {}
        
        long expires = (new Date()).getTime()+cacheExpiry;

        httpServletResponse.setHeader("cache-control", "public, max-age=" + cacheExpiry/1000 + ", cache");
        httpServletResponse.setDateHeader("Expires", expires);
        
    	if (authentication!=null && authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
        {
    		return NotificationStubResponse.getNotificationsList();
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
    
    @ApiOperation(value="Get notifications by user",notes="Requires uun to look up", response = Notification.class,responseContainer = "List",
    	    authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @ApiResponses({@ApiResponse(code=404,message="Not found")})
    @RequestMapping(value="/notifications/user/{uun}", method= RequestMethod.GET) 
    public List<Notification> getUserNotifications(@PathVariable("uun") String uun, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) throws ServletException {
        
        OAuth2Authentication authentication = null;
        try {
            authentication = (OAuth2Authentication)((SecurityContextImpl)httpServletRequest.getSession().getAttribute(("SPRING_SECURITY_CONTEXT"))).getAuthentication();   
    	} catch (Exception e) {}
        
    	long expires = (new Date()).getTime()+cacheExpiry;

    	httpServletResponse.setHeader("cache-control", "public, max-age=" + cacheExpiry/1000 + ", cache");
    	httpServletResponse.setDateHeader("Expires", expires);

    	try{
    		if (authentication!=null && authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
                {
    			return NotificationStubResponse.getNotificationsList();
                }
                
                List<Notification> notifications = notificationRepository.findByUun(uun);
                
                //WEB010-6 Notification API get user notifications
                for(int i = 0; i < notifications.size(); i++){
                    Notification notification  = notifications.get(i);
                    List<NotificationUser> users = new ArrayList<NotificationUser>();
                    notification.setNotificationUsers(users);
                }
                
    		return notifications;
    	}
    	catch (Exception e)
        {
    		logger.error("Error retrieving notifications",e);
            throw new ServletException("Error retrieving notifications");
        }
    	
    }
    
    @ApiOperation(value="Create a new notification",notes="Requires a valid notification object. For creation DO NOT specify notificationId, one will be automatically generated.",response = Notification.class,
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.write",description = "Write access to notification API")})})
    @ApiResponses({@ApiResponse(code=404,message="Not found")})
    @RequestMapping(value="/notification/", method=RequestMethod.POST) 
    public Notification setNotification(@RequestBody Notification notification, HttpServletRequest httpServletRequest) throws ServletException {
        
        OAuth2Authentication authentication = null;
        try {
            authentication = (OAuth2Authentication)((SecurityContextImpl)httpServletRequest.getSession().getAttribute(("SPRING_SECURITY_CONTEXT"))).getAuthentication();   
    	} catch (Exception e) {}
        
    	if (authentication !=null && authentication.getOAuth2Request().getClientId().equals("notification-api-ui")) {
            notification.setNotificationId("12345-auto");
            return notification;
        }
    	if (!notification.getTopic().equals("Emergency") && notification.getNotificationUsers().isEmpty()) {
    		throw new ServletException("Must add users for non broadcast notifications");
    	}
        		
        try { 		
        	notification.setNotificationId(null);
        		
        	List<NotificationUser> users = notification.getNotificationUsers();
        	if (!users.isEmpty()) {
        		for (int i = 0; i < users.size(); i++) {
        			users.get(i).setNotification(notification);
        		}
        		notification.setNotificationUsers(users);
        	}
        		
        	notificationRepository.save(notification);
        	
            UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
            userNotificationAudit.setAction(AuditActions.CREATE_NOTIFICATION);
            userNotificationAudit.setAuditDate(new Date());
            userNotificationAudit.setPublisherId(notification.getPublisherId());
            userNotificationAudit.setNotificationId(notification.getNotificationId());
            userNotificationAudit.setAuditDescription(new ObjectMapper().writeValueAsString(notification));
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
    @ApiResponses({@ApiResponse(code=404,message="Not found")})
    @RequestMapping(value="/notification/{notification-id}",method=RequestMethod.PUT)
    public void updateNotification(@PathVariable("notification-id") String notificationId, @RequestBody Notification notification, HttpServletRequest httpServletRequest) throws ServletException {      
        
        OAuth2Authentication authentication = null;
        try {
            authentication = (OAuth2Authentication)((SecurityContextImpl)httpServletRequest.getSession().getAttribute(("SPRING_SECURITY_CONTEXT"))).getAuthentication();   
    	} catch (Exception e) {}
        
        if (!notificationId.equals(notification.getNotificationId()))
        {
            throw new ServletException("Notification ID and notification body do not match");
        }
        
        if (!notification.getTopic().equals("Emergency") && notification.getNotificationUsers().isEmpty())
        {
            throw new ServletException("Non broadcast notifications require at least one user");
        }
        
        if (authentication!=null && authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
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
            throw new ServletException("Cannot change publisher ID once set.");
        }
        try
        {
        	List<NotificationUser> users = notification.getNotificationUsers();
        	if (!users.isEmpty()) {
        		for (int i = 0; i < users.size(); i++) {
        			users.get(i).setNotification(notification);
        			users.get(i).getId().setNotificationId(notificationId);
        		}
        		notification.setNotificationUsers(users);
        	}

            notificationRepository.save(notification);
            
            UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
            userNotificationAudit.setAction(AuditActions.UPDATE_NOTIFICATION);
            userNotificationAudit.setAuditDate(new Date());
            userNotificationAudit.setPublisherId(notification.getPublisherId());
            userNotificationAudit.setNotificationId(notification.getNotificationId());
            userNotificationAudit.setAuditDescription(new ObjectMapper().writeValueAsString(notification));
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

    @ApiOperation(value="Delete a notification",notes="Requires a valid notification id",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.write",description = "Write access to notification API")})})
    @ApiResponses({@ApiResponse(code=404,message="Not found")})
    @RequestMapping(value="/notification/{notification-id}",method=RequestMethod.DELETE)
    public void deleteNotification(@PathVariable("notification-id") String notificationId, HttpServletRequest httpServletRequest) throws ServletException {     
        
        OAuth2Authentication authentication = null;
        try {
            authentication = (OAuth2Authentication)((SecurityContextImpl)httpServletRequest.getSession().getAttribute(("SPRING_SECURITY_CONTEXT"))).getAuthentication();   
    	} catch (Exception e) {}
        
    	if (authentication !=null && authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
        {
            return;
        }
    	
        try
        {   
        	Notification notification = notificationRepository.findOne(notificationId);
        	if (notification != null) {
        		
        	notificationRepository.delete(notificationId);
                
                UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
                userNotificationAudit.setAction(AuditActions.DELETE_NOTIFICATION);
                userNotificationAudit.setAuditDate(new Date());
                userNotificationAudit.setPublisherId(notification.getPublisherId());
                userNotificationAudit.setNotificationId(notification.getNotificationId());
                userNotificationAudit.setAuditDescription(new ObjectMapper().writeValueAsString(notification));
                userNotificationAuditRepository.save(userNotificationAudit);
        	}
        	else {
        		throw new Exception("Notification " + notificationId + " does not exist");
        	}
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

    @ApiOperation(value="Get a list of categories containing notifications for a user",notes="Requires subcriber id to look up, and uun of user",response = NotificationResponse.class,
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @ApiResponses({@ApiResponse(code=404,message="Not found")})
    @RequestMapping(value="/usernotifications/{subscriber-id}",method= RequestMethod.GET)
    public NotificationResponse getUserNotificationsBySubscription(@PathVariable("subscriber-id") String subscriberId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        
        OAuth2Authentication authentication = null;
        try {
            authentication = (OAuth2Authentication)((SecurityContextImpl)httpServletRequest.getSession().getAttribute(("SPRING_SECURITY_CONTEXT"))).getAuthentication();   
    	} catch (Exception e) {}
        
    	long expires = (new Date()).getTime()+cacheExpiry;

        httpServletResponse.setHeader("cache-control", "public, max-age=" + cacheExpiry/1000 + ", cache");
        httpServletResponse.setDateHeader("Expires", expires);
    	
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
            Date dateNow = cal.getTime();

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
                    
                    if(notification.getStartDate() == null)
                    {
                        entry.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("1970-01-01"));
                    }
                    else
                    {                        
                        entry.setStartDate(notification.getStartDate());
                    }
                    
                    if (notification.getEndDate()==null)
                    {   //if no due date, this means the notification is open ended, however no due date
                        //may cause problem to end system that consumes notifications, i.e. notification portlet
                        //set it to year 2099 to indicate this notification has no due date.
                        entry.setDueDate(new SimpleDateFormat("yyyy-MM-dd").parse("2099-12-31"));
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

        logger.info(notificationResponse.toString());

        return notificationResponse;
    }

    @ApiOperation(value="Get all emergency notifications",notes="Independent of users",response = NotificationResponse.class,
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @ApiResponses({@ApiResponse(code=404,message="Not found")})
    @RequestMapping(value="/emergencynotifications",method= RequestMethod.GET)//OAuth2Authentication authentication, 
    public NotificationResponse getEmergencyNotifications(HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        
        OAuth2Authentication authentication = null;
        try {
            authentication = (OAuth2Authentication)((SecurityContextImpl)httpServletRequest.getSession().getAttribute(("SPRING_SECURITY_CONTEXT"))).getAuthentication();   
    	} catch (Exception e) {}
        
        long expires = (new Date()).getTime()+cacheExpiry;

        httpServletResponse.setHeader("cache-control", "public, max-age=" + cacheExpiry/1000 + ", cache");
        httpServletResponse.setDateHeader("Expires", expires);
        
        NotificationResponse notificationResponse = new NotificationResponse();

        if (authentication !=null && authentication.getOAuth2Request().getClientId().equals("notification-api-ui"))
        {
            return NotificationStubResponse.getNotificationResponse();
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
}
