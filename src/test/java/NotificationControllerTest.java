
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ed.notify.controller.NotificationController;
import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.repository.NotificationErrorRepository;
import uk.ac.ed.notify.repository.NotificationRepository;
import uk.ac.ed.notify.repository.PublisherDetailsRepository;
import uk.ac.ed.notify.repository.UserNotificationAuditRepository;


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
    NotificationController notificationController;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    PublisherDetailsRepository publisherDetailsRepository;

    @Autowired
    UserNotificationAuditRepository userNotificationAuditRepository;

    @Autowired
    NotificationErrorRepository notificationErrorRepository;

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
        Notification notification = new Notification();
        notification.setBody("<p>Test</p>");
        notification.setCategory("TESTCATEGORY");
        notification.setPublisherId("TESTPUB");
        notification.setPublisherNotificationId("12");
        notification.setTitle("TESTTITLE");
        notification.setUrl("http://www.google.co.uk");
        notification.setUun("TESTUUN");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        notificationId = notification.getNotificationId();

    }

    @Test
    public void testGetNotification() throws Exception {

        this.mockMvc.perform(get("/notification/" + notificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body", is("<p>Test</p>")));
    }

    @Test
    public void testGetPublisherNotifications() throws Exception {

        this.mockMvc.perform(get("/notification/publisher/TESTPUB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].body",is("<p>Test</p>")));

    }

    @Test
    public void testGetUserNotifications()
    {

    }

}
