
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        PublisherDetails publisherDetails = new PublisherDetails();
        publisherDetails.setPublisherId("TESTPUB");
        publisherDetails.setStatus("A");
        publisherDetails.setPublisherType("PULL");
        publisherDetailsRepository.save(publisherDetails);
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
        notification.setEndDate(null);
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

    @Test
    public void testGetUserNotificationsWithNoUser() throws Exception {
        this.mockMvc.perform(get("/usernotifications/TESTSUB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors[0].error", is("No UUN provided")));
    }

    @Test
    public void testDeleteNotification() throws Exception {
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

        this.mockMvc.perform(delete("/notification/"+notificationId))
                .andExpect(status().isOk());

        notification = notificationRepository.findOne(notificationId);
        assertNull(notification);

        Iterable<UserNotificationAudit> userNotificationAudits = userNotificationAuditRepository.findAll();
        assertEquals(AuditActions.DELETE_NOTIFICATION,userNotificationAudits.iterator().next().getAction());
    }

    @Test
    public void testUpdateNotification() throws Exception
    {
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
        notification.setTitle("UPDATEDTITLE");
        ObjectMapper objMapper = new ObjectMapper();
        String jsonString = objMapper.writeValueAsString(notification);
        this.mockMvc.perform(put("/notification/"+notificationId).content(jsonString).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        notification = notificationRepository.findOne(notificationId);
        assertEquals("UPDATEDTITLE",notification.getTitle());

        Iterable<UserNotificationAudit> userNotificationAudits = userNotificationAuditRepository.findAll();
        assertEquals(AuditActions.UPDATE_NOTIFICATION,userNotificationAudits.iterator().next().getAction());

    }

    @Test
    public void testCreateNotification() throws Exception {
        Notification notification = new Notification();
        notification.setBody("<p>Testing?</p><p>&nbsp;</p><ul><li>One&nbsp;</li><li>two&nbsp;</li><li>three</li><li>&nbsp;</li></ul>");
        notification.setTopic("TESTCATEGORY");
        notification.setPublisherId("TESTPUB");
        notification.setPublisherNotificationId("12");
        notification.setTitle("TESTTITLE");
        notification.setUrl("http://www.google.co.uk");
        notification.setUun("TESTUUN");
        notification.setStartDate(date);
        notification.setEndDate(date);

        ObjectMapper objMapper = new ObjectMapper();
        String jsonString = objMapper.writeValueAsString(notification);
        String contentAsString = this.mockMvc.perform(post("/notification/")
                .content(jsonString).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        notification = objMapper.readValue(contentAsString,Notification.class);
        Notification one = notificationRepository.findOne(notification.getNotificationId());
        assertNotNull(one);

        Iterable<UserNotificationAudit> userNotificationAudits = userNotificationAuditRepository.findAll();
        assertEquals(AuditActions.CREATE_NOTIFICATION,userNotificationAudits.iterator().next().getAction());

    }

    @Test
    public void testGetEmergencyNotifications() throws Exception {

        Notification notification = new Notification();
        notification.setBody("<p>Test</p>");
        notification.setTopic("Emergency");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("12");
        notification.setTitle("TESTTITLE");
        notification.setUrl("http://www.google.co.uk");
        notification.setUun("TESTUUN");
        notification.setStartDate(date);
        notification.setEndDate(dateFuture);
        notificationRepository.save(notification);
        notification = new Notification();
        notification.setBody("<p>Test Two</p>");
        notification.setTopic("Emergency");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("12");
        notification.setTitle("TESTTITLETWO");
        notification.setUrl("http://www.google.co.uk");
        notification.setUun("TESTUUN");
        notification.setStartDate(date);
        notification.setEndDate(dateFuture);
        notificationRepository.save(notification);

        this.mockMvc.perform(get("/emergencynotifications").with((MockHttpServletRequest request) -> {
            request.setParameter("user.login.id", "TESTUUN");
            return request;
        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].title", is("Emergency")))
                .andExpect(jsonPath("$.categories[0].entries[0].title",is("TESTTITLE")))
                .andExpect(jsonPath("$.categories[0].entries[1].title",is("TESTTITLETWO")));
    }


}
