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
import uk.ac.ed.notify.NotificationResponse;
import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.repository.NotificationRepository;

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

    @Value("${cache.expiry}")
    int cacheExpiry;

    @Autowired
    NotificationRepository notificationRepository;

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
        System.out.println("Called setNotification");
        //TODO Check that publisher ID is valid
        //TODO Check that variables are valid
        //TODO Add JSOUP Cleaner
        //TODO Add audit row
        //TODO Log errors

        notificationRepository.save(notification);
        System.out.println("Saved notification");
        return notification;
    }

    @ApiOperation(value="Get a list of categories containing notifications for a user",notes="Requires notification id to look up",
            authorizations = {@Authorization(value="oauth2",scopes = {@AuthorizationScope(scope="notifications.read",description = "Read access to notification API")})})
    @RequestMapping(value="/usernotifications/user",method= RequestMethod.GET)
    public @ResponseBody
    NotificationResponse getNoticationByUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException
    {

        System.out.println(httpServletRequest.getParameter("user.login.id"));
        NotificationResponse notificationResponse = new NotificationResponse();
        /*List<NotificationError> errors = new ArrayList<NotificationError>();
        errors.add(new NotificationError("error message", "source"));
        notificationResponse.setErrors(errors);*/

        List<NotificationCategory> categories = new ArrayList<NotificationCategory>();
        NotificationCategory category = new NotificationCategory();
        category.setTitle("title");

        List<NotificationEntry> entries = new ArrayList<NotificationEntry>();
        NotificationEntry entry = new NotificationEntry();
        entry.setBody("body");
        entry.setTitle("test notification title");
        entry.setDueDate(new Date());

        entries.add(entry);
        category.setEntries(entries);

        categories.add(category);
        notificationResponse.setCategories(categories);

        return notificationResponse;
    }

}
