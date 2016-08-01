
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.entity.NotificationUser;
import uk.ac.ed.notify.entity.NotificationUserPK;
import uk.ac.ed.notify.entity.UserNotificationAudit;
import uk.ac.ed.notify.repository.UserNotificationAuditRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by rgood on 20/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class UserNotificationAuditRepositoryTest {

    @Autowired
    private UserNotificationAuditRepository userNotificationAuditRepository;

    private Date date;
    private Notification notification;

    @Before
    public void setup()
    {
        date = new Date();
        date.setTime(1445347648);
        
        notification = new Notification();
        notification.setNotificationId("sys-guid-id");
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
        user.setId(new NotificationUserPK(null,"user"));
        users.add(user);
        
        notification.setNotificationUsers(users);
    }

    @Test
    public void testCreateUserNotificationAudit() throws Exception
    {
        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
        userNotificationAudit.setAction("USER_VIEWED");
        userNotificationAudit.setAuditDate(date);
        userNotificationAudit.setPublisherId(notification.getPublisherId());
        userNotificationAudit.setNotificationId(notification.getNotificationId());
        userNotificationAudit.setAuditDescription(new ObjectMapper().writeValueAsString(notification));
        userNotificationAuditRepository.save(userNotificationAudit);
    }

    @Test
    public void testGetUserNotificationAudit() throws Exception
    {
    	String jsonString = new ObjectMapper().writeValueAsString(notification);
    	
        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
        userNotificationAudit.setAction("USER_VIEWED");
        userNotificationAudit.setAuditDate(date);
        userNotificationAudit.setPublisherId(notification.getPublisherId());
        userNotificationAudit.setNotificationId(notification.getNotificationId());
        userNotificationAudit.setAuditDescription(jsonString);
        userNotificationAuditRepository.save(userNotificationAudit);
        
        UserNotificationAudit savedUserNotificationAudit = userNotificationAuditRepository.findOne(userNotificationAudit.getAuditId());
        assertThat(savedUserNotificationAudit.getAuditId(), is(userNotificationAudit.getAuditId()));
        assertThat(savedUserNotificationAudit.getNotificationId(), is(userNotificationAudit.getNotificationId()));
        assertThat(savedUserNotificationAudit.getAction(), is("USER_VIEWED"));
        
        assertThat(jsonString, is(savedUserNotificationAudit.getAuditDescription()));
    }

    @Test
    public void testDeleteUserNotificationAudit() throws Exception
    {
        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
        userNotificationAudit.setAction("USER_VIEWED");
        userNotificationAudit.setAuditDate(date);
        userNotificationAudit.setPublisherId(notification.getPublisherId());
        userNotificationAudit.setNotificationId(notification.getNotificationId());
        userNotificationAudit.setAuditDescription(new ObjectMapper().writeValueAsString(notification));
        userNotificationAuditRepository.save(userNotificationAudit);
        
        userNotificationAuditRepository.delete(userNotificationAudit);
        userNotificationAudit = userNotificationAuditRepository.findOne(userNotificationAudit.getAuditId());
        
        assertThat(userNotificationAudit, is(nullValue()));
    }
}
