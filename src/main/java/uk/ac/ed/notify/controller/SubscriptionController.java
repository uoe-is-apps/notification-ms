package uk.ac.ed.notify.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.entity.SubscriptionMessage;
import uk.ac.ed.notify.service.SubscriptionService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by rgood on 25/11/2016.
 */
@RestController
public class SubscriptionController {

    private static final String CORS_PATTERN = "(.+\\.)*ed\\.ac\\.uk";


    @Autowired
    SubscriptionService subscriptionService;

    @RequestMapping(value="/push-subscription",method = RequestMethod.POST)
    public void addSubscription(@RequestBody SubscriptionMessage subscriptionMessage, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException {
        CorsMatcher.setAccessControlAllowOrigin(httpServletRequest, httpServletResponse, CORS_PATTERN);
        
        //TODO Ensure the principal matches the user in the subscription message

        subscriptionService.addSubscriber(subscriptionMessage);

    }



}
