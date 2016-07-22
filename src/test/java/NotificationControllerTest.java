
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import uk.ac.ed.notify.entity.*;
import uk.ac.ed.notify.repository.NotificationErrorRepository;
import uk.ac.ed.notify.repository.NotificationRepository;
import uk.ac.ed.notify.repository.PublisherDetailsRepository;
import uk.ac.ed.notify.repository.SubscriberDetailsRepository;
import uk.ac.ed.notify.repository.TopicSubscriptionRepository;
import uk.ac.ed.notify.repository.UserNotificationAuditRepository;
import uk.ac.ed.notify.repository.test.*;

import javax.servlet.ServletException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by rgood on 01/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class NotificationControllerTest {

    private Date date;
    private Date dateFuture;

    private String notificationId;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    PublisherDetailsRepository publisherDetailsRepository;

    @Autowired
    TopicSubscriptionRepository topicSubscriptionRepository;

    @Autowired
    UserNotificationAuditRepository userNotificationAuditRepository;

    @Autowired
    NotificationErrorRepository notificationErrorRepository;

    @Autowired
    SubscriberDetailsRepository subscriberDetailsRepository;

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;


    @Before
    public void setup(){

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // Setup Spring test in webapp-mode (same config as spring-boot)
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        date = new Date();
        date.setTime(1445347648);
        dateFuture = new Date();
        dateFuture.setTime(dateFuture.getTime()+100000);

    }

    @After
    public void cleanup()
    {
        notificationRepository.deleteAll();
        topicSubscriptionRepository.deleteAll();
        userNotificationAuditRepository.deleteAll();
        notificationErrorRepository.deleteAll();
    }



    @Test
    public void testGetNotification() throws Exception {

    	Notification notification = new Notification();
        notification.setBody("<p>Regular Notification</p>");
        notification.setTopic("Notification");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("13");
        notification.setTitle("Notify Announcement");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        
        List<NotificationUser> users = new ArrayList<NotificationUser>();
        NotificationUser user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"user"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        notificationId = notification.getNotificationId();
        assertThat(notificationId, is(notNullValue()));
        
        this.mockMvc.perform(get("/notification/" + notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Notify Announcement")))
                .andExpect(jsonPath("$.body", is("<p>Regular Notification</p>")))
                .andExpect(jsonPath("$.notificationUsers", hasSize(1)))
                .andExpect(jsonPath("$.notificationUsers[0].user.uun", is("user")));
    }
    
    @Test
    public void testGetPublisherNotifications() throws Exception {
    	
    	PublisherDetails publisherDetails = new PublisherDetails();
        publisherDetails.setPublisherId("TESTPUB");
        publisherDetails.setStatus("A");
        publisherDetails.setPublisherType("PULL");
        publisherDetailsRepository.save(publisherDetails);
        
    	Notification notification = new Notification();
        notification.setBody("<p>Regular Notification</p>");
        notification.setTopic("Notification");
        notification.setPublisherId("TESTPUB");
        notification.setPublisherNotificationId("13");
        notification.setTitle("Notify Announcement");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        
        List<NotificationUser> users = new ArrayList<NotificationUser>();
        NotificationUser user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"user"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        notificationId = notification.getNotificationId();
        assertThat(notificationId, is(notNullValue()));
        
        this.mockMvc.perform(get("/notifications/publisher/TESTPUB"))
	        .andExpect(status().isOk())
	        .andExpect(jsonPath("$", hasSize(1)))
	        .andExpect(jsonPath("$[0].notificationId", is(notificationId)))
	        .andExpect(jsonPath("$[0].body", is("<p>Regular Notification</p>")))
	        .andExpect(jsonPath("$[0].notificationUsers", hasSize(1)));
    	
    }

   
}
