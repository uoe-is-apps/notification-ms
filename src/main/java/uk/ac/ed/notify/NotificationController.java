package uk.ac.ed.notify;

import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.notify.entity.Notification;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by rgood on 18/09/2015.
 */
@RestController
public class NotificationController {

    @Value("${cache.expiry}")
    int cacheExpiry;

    @Autowired
    NotificationRepository notificationRepository;

    @ApiOperation(value="Get a specific location",notes="Requires notification id to look up")
    @RequestMapping(value="/notification/{notification-id}",method= RequestMethod.GET)
    public @ResponseBody
    Notification getNotification(@PathVariable("notification-id") String notificationId, HttpServletResponse httpServletResponse) throws ServletException {

        Notification notification = new Notification();
        notification.setNotificationId(notificationId);
        notification.setTitle("dogs");
        notification.setBody("some dogs eat chips.");
        notificationRepository.save(notification);
        if (notificationId.equals(""))
        {
            throw new ServletException("You must provide a notification-id");
        }
        long expires = (new Date()).getTime()+cacheExpiry;

        httpServletResponse.setHeader("cache-control", "public, max-age=" + cacheExpiry/1000 + ", cache");
        httpServletResponse.setDateHeader("Expires", expires);
        return notificationRepository.findOne(notificationId);
    }

}
