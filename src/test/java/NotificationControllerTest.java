
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ed.notify.controller.NotificationController;
import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.entity.SubscriberDetails;
import uk.ac.ed.notify.entity.TopicSubscription;
import uk.ac.ed.notify.repository.*;


import java.util.Date;

/**
 * Created by rgood on 01/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
public class NotificationControllerTest {

    private Date date;

    private String notificationId;

    @Autowired
    NotificationRepository notificationRepository;

   /* @Autowired
    PublisherDetailsRepository publisherDetailsRepository;*/

    @Autowired
    TopicSubscriptionRepository topicSubscriptionRepository;

   /* @Autowired
    UserNotificationAuditRepository userNotificationAuditRepository;

    @Autowired
    NotificationErrorRepository notificationErrorRepository;*/

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

    }

    @After
    public void cleanup()
    {
        notificationRepository.deleteAll();
        topicSubscriptionRepository.deleteAll();
    }



    @Test
    public void testGetNotification() throws Exception {

        Notification notification = new Notification();
        notification.setBody("<p>Test</p>");
        notification.setTopic("TESTCATEGORY");
        notification.setPublisherId("TESTPUB");
        notification.setPublisherNotificationId("12");
        notification.setTitle("TESTTITLE");
        notification.setUrl("http://www.google.co.uk");
        notification.setUun("TESTUUN");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        notificationId = notification.getNotificationId();
        this.mockMvc.perform(get("/notification/" + notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("TESTTITLE")))
                .andExpect(jsonPath("$.body", is("<p>Test</p>")));
    }

    @Test
    public void testGetPublisherNotifications() throws Exception {

        Notification notification = new Notification();
        notification.setBody("<p>Test</p>");
        notification.setTopic("TESTCATEGORY");
        notification.setPublisherId("TESTPUB");
        notification.setPublisherNotificationId("12");
        notification.setTitle("TESTTITLE");
        notification.setUrl("http://www.google.co.uk");
        notification.setUun("TESTUUN");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        notificationId = notification.getNotificationId();
        notification = new Notification();
        notification.setBody("<p>Test Two</p>");
        notification.setTopic("TESTCATEGORYTWO");
        notification.setPublisherId("TESTPUB");
        notification.setPublisherNotificationId("12");
        notification.setTitle("TESTTITLETWO");
        notification.setUrl("http://www.google.co.uk");
        notification.setUun("TESTUUN");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        this.mockMvc.perform(get("/notification/publisher/TESTPUB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].body", is("<p>Test</p>")))
                .andExpect(jsonPath("$[1].body", is("<p>Test Two</p>")));

    }

    @Test
    public void testGetUserNotifications() throws Exception {

        SubscriberDetails subscriberDetails = new SubscriberDetails();
        subscriberDetails.setSubscriberId("TESTSUB");
        subscriberDetails.setStatus("A");
        subscriberDetails.setType("PULL");
        subscriberDetails.setLastUpdated(date);
        subscriberDetailsRepository.save(subscriberDetails);
        Notification notification = new Notification();
        notification.setBody("<p>Test</p>");
        notification.setTopic("TESTCATEGORY");
        notification.setPublisherId("TESTPUB");
        notification.setPublisherNotificationId("12");
        notification.setTitle("TESTTITLE");
        notification.setUrl("http://www.google.co.uk");
        notification.setUun("TESTUUN");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        notification = new Notification();
        notification.setBody("<p>Test Two</p>");
        notification.setTopic("TESTCATEGORYTWO");
        notification.setPublisherId("TESTPUB");
        notification.setPublisherNotificationId("12");
        notification.setTitle("TESTTITLETWO");
        notification.setUrl("http://www.google.co.uk");
        notification.setUun("TESTUUN");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        TopicSubscription topicSubscription = new TopicSubscription();
        topicSubscription.setStatus("A");
        topicSubscription.setLastUpdated(new Date());
        topicSubscription.setSubscriberId("TESTSUB");
        topicSubscription.setTopic("TESTCATEGORYTWO");
        topicSubscriptionRepository.save(topicSubscription);
        this.mockMvc.perform(get("/usernotifications/TESTSUB").with((MockHttpServletRequest request) -> {
            request.setParameter("user.login.id","TESTUUN");
            return request;
        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].title",is("TESTCATEGORYTWO")));
    }

    @Test
    public void testGetUserNotificationsWithInactiveSubscriber() throws Exception {
        this.mockMvc.perform(get("/usernotifications/INVALIDSUB").with((MockHttpServletRequest request) -> {
            request.setParameter("user.login.id","TESTUUN");
            return request;
        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].error",is("Invalid subscriber or subscriber inactive")));

    }

    @Test public void testGetUserNotificationsWithNoUser() throws Exception {
        this.mockMvc.perform(get("/usernotifications/TESTSUB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].error", is("No UUN provided")));
    }

}
