import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import uk.ac.ed.notify.entity.*;
import uk.ac.ed.notify.repository.NotificationErrorRepository;
import uk.ac.ed.notify.repository.NotificationRepository;
import uk.ac.ed.notify.repository.PublisherDetailsRepository;
import uk.ac.ed.notify.repository.SubscriberDetailsRepository;
import uk.ac.ed.notify.repository.TopicSubscriptionRepository;
import uk.ac.ed.notify.repository.UserNotificationAuditRepository;
import uk.ac.ed.notify.repository.test.NotificationUserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

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
    NotificationUserRepository notificationUserRepository;

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
    public void testGetRegularNotification() throws Exception {

    	Notification notification = new Notification();
        notification.setBody("<p>Regular Notification</p>");
        notification.setTopic("Notification");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("13");
        notification.setTitle("Notify Announcement");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notification.setLastUpdated(new Date());
        
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
    public void testGetRegularNotificationsByPublisher() throws Exception {
    	
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
        notification.setLastUpdated(new Date());
        
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

   @Test
   public void testGetRegularNotificationsByUun() throws Exception {
	   
	   Notification notification = new Notification();
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       NotificationUser user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       
       notification.setNotificationUsers(users);
       notificationRepository.save(notification);
       
       notification = new Notification();
       notification.setBody("<p>Regular Notification 2</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("11");
       notification.setTitle("Notify Announcement 2");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       users = new ArrayList<NotificationUser>();
       user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"gozer"));
       users.add(user);
       
       notification.setNotificationUsers(users);
       notificationRepository.save(notification);

       this.mockMvc.perform(get("/notifications/user/gozer"))
	       .andExpect(status().isOk())
	       .andExpect(jsonPath("$", hasSize(1)))
	       .andExpect(jsonPath("$[0].body", is("<p>Regular Notification 2</p>")))
               //we decided to not return list of uun as part of this api call
	       .andExpect(jsonPath("$[0].notificationUsers", hasSize(0))) 
	       //.andExpect(jsonPath("$[0].notificationUsers[0].user.uun", is("gozer"))
               ;
       
   }
   
   
   @Test
   public void testCreateEmergencyNotification() throws Exception {
	   
	   Notification notification = new Notification();
       notification.setBody("<p>Emergency Notification</p>");
       notification.setTopic("Emergency");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Announcement");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       ObjectMapper mapper = new ObjectMapper();
       String jsonString  = mapper.writeValueAsString(notification);
       
       String response = this.mockMvc.perform(post("/notification/")
    		   .contentType(MediaType.APPLICATION_JSON)
               .content(jsonString))
               .andExpect(status().isOk())
       
               .andExpect(jsonPath("$.notificationId", is(notNullValue())))
               .andExpect(jsonPath("$.notificationUsers", hasSize(0)))
               .andReturn().getResponse().getContentAsString();
       
       notification = mapper.readValue(response, Notification.class);
       
       Notification savedInstance = notificationRepository.findOne(notification.getNotificationId());
       assertThat(savedInstance, is(notNullValue()));
       assertThat(savedInstance.getNotificationId(), is(notification.getNotificationId()));
       assertThat(savedInstance.getNotificationUsers(), hasSize(0));
   }
   
   @Test
   public void testCreateRegularNotification() throws Exception {

	   Notification notification = new Notification();
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       NotificationUser user = new NotificationUser();
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       notification.setNotificationUsers(users);
       
       ObjectMapper mapper = new ObjectMapper();
       String jsonString  = mapper.writeValueAsString(notification);
       
	   String response = this.mockMvc.perform(post("/notification/")
    		   .contentType(MediaType.APPLICATION_JSON)
               .content(jsonString))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.notificationId", is(notNullValue())))
               .andExpect(jsonPath("$.notificationUsers", hasSize(1)))
               
               .andReturn().getResponse().getContentAsString();
       
       notification = mapper.readValue(response, Notification.class);
       
       Notification savedInstance = notificationRepository.findOne(notification.getNotificationId());
       assertThat(savedInstance, is(notNullValue()));
       assertThat(savedInstance.getNotificationUsers(), hasSize(1));
       assertThat(savedInstance.getNotificationUsers().get(0).getId().getUun(), is("donald"));
   }
   
   @Test(expected=ServletException.class)
   public void testCreateRegularNotificationNoUsers() throws Exception {
	   
	   Notification notification = new Notification();
       notification.setBody("<p>Regular Notification</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("learn");
       notification.setPublisherNotificationId("learn10");
       notification.setTitle("Announcement");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       ObjectMapper mapper = new ObjectMapper();
       String jsonString  = mapper.writeValueAsString(notification);
       
       this.mockMvc.perform(post("/notification/")
    		   .contentType(MediaType.APPLICATION_JSON)
               .content(jsonString));      
   }
   
   @Test
   public void testCreateRegularNotificationUserAudit() throws Exception{
	   
	   Notification notification = new Notification();
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       NotificationUser user = new NotificationUser();
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       notification.setNotificationUsers(users);
       
       ObjectMapper mapper = new ObjectMapper();
       String jsonString  = mapper.writeValueAsString(notification);
       
	   String response = this.mockMvc.perform(post("/notification/")
    		   .contentType(MediaType.APPLICATION_JSON)
               .content(jsonString))
               .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
	   
	   notification = mapper.readValue(response, Notification.class);
	   
	   List<UserNotificationAudit> audit = (List<UserNotificationAudit>) userNotificationAuditRepository.findAll();
	   assertThat(audit.get(0).getNotificationId(), is(notification.getNotificationId()));
	   assertThat(audit.get(0).getAction(), is(AuditActions.CREATE_NOTIFICATION));
   }
   
   @Test
   public void testUpdateEmergencyNotification() throws Exception {
	   
	   Notification notification = new Notification();
       notification.setBody("<p>Emergency Notification</p>");
       notification.setTopic("Emergency");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Announcement");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       notificationRepository.save(notification);
       
       String notificationId = notification.getNotificationId();
       assertThat(notificationId, is(notNullValue()));
       notification.setTitle("Updated announcement");
       
       ObjectMapper mapper = new ObjectMapper();
       String jsonString = mapper.writeValueAsString(notification);
       
       this.mockMvc.perform(put("/notification/"+notificationId)
    		   .content(jsonString)
    		   .contentType(MediaType.APPLICATION_JSON))
    		   .andExpect(status().isOk());
       
       Notification savedNotification = notificationRepository.findOne(notificationId);
       assertThat(savedNotification.getTitle(), is("Updated announcement"));
   }
   
   @Test
   public void testUpdateRegularNotificationAddUser() throws Exception {
	   
	   Notification notification = new Notification();
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       NotificationUser user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       
       notification.setNotificationUsers(users);
       notificationRepository.save(notification);
       
       String notificationId = notification.getNotificationId();
       assertThat(notificationId, is(notNullValue()));
       
       user = new NotificationUser();
       user.setId(new NotificationUserPK(null,"gozer"));
       users.add(user);
       notification.setNotificationUsers(users);
       
       ObjectMapper mapper = new ObjectMapper();
       String jsonString = mapper.writeValueAsString(notification);
       
       this.mockMvc.perform(put("/notification/"+notificationId)
    		   .content(jsonString)
    		   .contentType(MediaType.APPLICATION_JSON))
    		   .andExpect(status().isOk());
       
       Notification savedNotification = notificationRepository.findOne(notificationId);
       assertThat(savedNotification.getNotificationUsers(), hasSize(2));
       
       List<String> userNames = new ArrayList<String>(2);
       userNames.add(savedNotification.getNotificationUsers().get(0).getId().getUun());
       userNames.add(savedNotification.getNotificationUsers().get(1).getId().getUun());
       
       assertThat(userNames, hasItems("donald","gozer")); 
   }
   
   @Test
   public void testUpdateRegularNotificationRemoveUser() throws Exception {
	   
	   Notification notification = new Notification();
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       
       NotificationUser user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       
       user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"gozer"));
       users.add(user);
       
       notification.setNotificationUsers(users);
       notificationRepository.save(notification);
       
       String notificationId = notification.getNotificationId();
       assertThat(notificationId, is(notNullValue()));
       
       users.remove(user); //removing gozer
       
       ObjectMapper mapper = new ObjectMapper();
       String jsonString = mapper.writeValueAsString(notification);
       
       this.mockMvc.perform(put("/notification/"+notificationId)
    		   .content(jsonString)
    		   .contentType(MediaType.APPLICATION_JSON))
    		   .andExpect(status().isOk());
       
       Notification savedNotification = notificationRepository.findOne(notificationId);
       assertThat(savedNotification.getNotificationUsers(), hasSize(1));
       assertThat(savedNotification.getNotificationUsers().get(0).getId().getUun(), is("donald"));
   }
   
   @Test(expected = ServletException.class)
   public void testUpdateNotificationMismatchedNotificationId() throws Exception
   {
	   Notification notification = new Notification();
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       NotificationUser user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       
       notification.setNotificationUsers(users);
       notificationRepository.save(notification);
       
       String notificationId = notification.getNotificationId();
       assertThat(notificationId, is(notNullValue()));
       
       notification.setTitle("Notify announcement update");
       ObjectMapper objMapper = new ObjectMapper();
       String jsonString = objMapper.writeValueAsString(notification);
       
       this.mockMvc.perform(put("/notification/12")
               .content(jsonString)
               .contentType(MediaType.APPLICATION_JSON));
   }
   
   @Test(expected = ServletException.class)
   public void testUpdateNotificationUpdatePublisher() throws Exception
   {
	   Notification notification = new Notification();
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       NotificationUser user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       
       notification.setNotificationUsers(users);
       notificationRepository.save(notification);
       
       String notificationId = notification.getNotificationId();
       assertThat(notificationId, is(notNullValue()));
       
       notification.setPublisherId("other-publisher");
       
       ObjectMapper objMapper = new ObjectMapper();
       String jsonString = objMapper.writeValueAsString(notification);
       
       this.mockMvc.perform(put("/notification/"+notificationId)
               .content(jsonString)
               .contentType(MediaType.APPLICATION_JSON));
   }
   
   @Test(expected = ServletException.class)
   public void testUpdateNotificationNoNotification() throws Exception
   {
	   Notification notification = new Notification();
	   notification.setNotificationId("abcdid");
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       NotificationUser user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       notification.setNotificationUsers(users);
  
       ObjectMapper objMapper = new ObjectMapper();
       String jsonString = objMapper.writeValueAsString(notification);
       
       this.mockMvc.perform(put("/notification/"+notification.getNotificationId())
               .content(jsonString)
               .contentType(MediaType.APPLICATION_JSON));
   }
    
   @Test
   public void testDeleteRegularNotification() throws Exception {
	   
	   Notification notification = new Notification();
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       
       NotificationUser user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       
       user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"gozer"));
       users.add(user);
       
       notification.setNotificationUsers(users);
       notificationRepository.save(notification);
       
       String notificationId = notification.getNotificationId();
       assertThat(notificationId, is(notNullValue()));
       
       this.mockMvc.perform(delete("/notification/"+notificationId))
       			.andExpect(status().isOk());
       
       Notification savedNotification = notificationRepository.findOne(notificationId);
       Date now = new Date();
       assertThat(true, is(now.after(savedNotification.getEndDate())));
       
       List<NotificationUser> notificationUsers = notificationUserRepository.findByIdNotificationId(notificationId);
       assertThat(true, is(2 == notificationUsers.size()));
   }
   
   @Test
   public void testGetEmergencyNotifications() throws Exception {
	   
	   Notification notification = new Notification();
       notification.setBody("<p>Emergency Notification 1</p>");
       notification.setTopic("Emergency");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("12");
       notification.setTitle("Emergency 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(dateFuture);
       notification.setLastUpdated(new Date());
       notificationRepository.save(notification);
       
       notification = new Notification();
       notification.setBody("<p>Emergency Notification 2</p>");
       notification.setTopic("Emergency");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Emergency 2");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(dateFuture);
       notification.setLastUpdated(new Date());
       notificationRepository.save(notification);
       
       this.mockMvc.perform(get("/emergencynotifications"))
       		.andExpect(status().isOk())
       		.andExpect(jsonPath("$.categories[0].entries", hasSize(2)))
       		.andExpect(jsonPath("$.categories[0].entries[0].title",is("Emergency 1")))
            .andExpect(jsonPath("$.categories[0].entries[1].title",is("Emergency 2")));       
   }
   
   @Test
   public void testGetUserNotifications() throws Exception {
	   
	   SubscriberDetails subscriberDetails = new SubscriberDetails();
       subscriberDetails.setSubscriberId("TESTSUB");
       subscriberDetails.setStatus("A");
       subscriberDetails.setType("PULL");
       subscriberDetails.setLastUpdated(date);
       subscriberDetailsRepository.save(subscriberDetails);
       
       TopicSubscription topicSubscription = new TopicSubscription();
       topicSubscription.setStatus("A");
       topicSubscription.setLastUpdated(new Date());
       topicSubscription.setSubscriberId("TESTSUB");
       topicSubscription.setTopic("Notification");
       topicSubscriptionRepository.save(topicSubscription);
       
       Notification notification = new Notification();
       notification.setBody("<p>Regular Notification 1</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("10");
       notification.setTitle("Notify Announcement 1");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(date);
       notification.setLastUpdated(new Date());
       
       List<NotificationUser> users = new ArrayList<NotificationUser>();
       NotificationUser user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       
       notification.setNotificationUsers(users);
       notificationRepository.save(notification);
       
       notification = new Notification();
       notification.setBody("<p>Regular Notification 2</p>");
       notification.setTopic("Notification");
       notification.setPublisherId("notify-ui");
       notification.setPublisherNotificationId("11");
       notification.setTitle("Notify Announcement 2");
       notification.setUrl("http://www.google.co.uk");
       notification.setStartDate(date);
       notification.setEndDate(dateFuture);
       notification.setLastUpdated(new Date());
       
       users = new ArrayList<NotificationUser>();
       user = new NotificationUser();
       user.setNotification(notification);
       user.setId(new NotificationUserPK(null,"donald"));
       users.add(user);
       
       notification.setNotificationUsers(users);
       notificationRepository.save(notification);
       
       this.mockMvc.perform(get("/usernotifications/TESTSUB").with((MockHttpServletRequest request) -> {
           request.setParameter("user.login.id","donald");
           return request;
       }))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.categories[0].title",is("Notification")));
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
}
