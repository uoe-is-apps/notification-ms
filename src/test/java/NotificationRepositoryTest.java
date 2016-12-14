import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.entity.NotificationUser;
import uk.ac.ed.notify.entity.NotificationUserPK;
import uk.ac.ed.notify.repository.NotificationRepository;
import uk.ac.ed.notify.repository.test.NotificationUserRepository;

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
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationUserRepository notificationUserRepository;

    private Date date;
    private Date dateFuture;

    @Before
    public void setup()
    {
        date = new Date();
        date.setTime(date.getTime()-10000);

        dateFuture = new Date();
        dateFuture.setTime(dateFuture.getTime()+100000);
    }

    @After
    public void cleanup()
    {
        notificationRepository.deleteAll();
    }

    @Test
    public void testCreateEmergencyNotification()
    {
        Notification notification = new Notification();
        notification.setBody("<p>Emergency Notification</p>");
        notification.setTopic("Emergency");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("12");
        notification.setTitle("Emergency");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
    }
    
    @Test
    public void testCreateRegularNotification() {
    	
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
    }

    @Test
    public void testGetEmergencyNotification() {
    	
    	Notification notification = new Notification();
        notification.setBody("<p>Emergency Notification</p>");
        notification.setTopic("Emergency");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("12");
        notification.setTitle("Emergency");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        
        notification = notificationRepository.findOne(notification.getNotificationId());
        assertThat("<p>Emergency Notification</p>", is(notification.getBody()));
        assertThat("Emergency", is(notification.getTopic()));
        assertThat("notify-ui", is(notification.getPublisherId()));
        assertThat("12", is(notification.getPublisherNotificationId()));
        assertThat("Emergency", is(notification.getTitle()));
        assertThat("http://www.google.co.uk", is(notification.getUrl()));
        assertThat(date, is(notification.getStartDate()));
        assertThat(date, is(notification.getEndDate()));
        
        assertThat(notification.getNotificationUsers(), hasSize(0));
    }
    
    
    @Test
    public void testGetRegularNotification()
    {
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
        
        notification = notificationRepository.findOne(notification.getNotificationId());
        assertThat("<p>Regular Notification</p>", is(notification.getBody()));
        assertThat("Notification", is(notification.getTopic()));
        assertThat("notify-ui", is(notification.getPublisherId()));
        assertThat("13", is(notification.getPublisherNotificationId()));
        assertThat("Notify Announcement", is(notification.getTitle()));
        assertThat("http://www.google.co.uk", is(notification.getUrl()));
        assertThat(date, is(notification.getStartDate()));
        assertThat(date, is(notification.getEndDate()));
        
        assertThat(notification.getNotificationUsers(), hasSize(1));
        assertThat(notification.getNotificationUsers().get(0).getId().getUun(), is("user"));
        assertThat(notification.getNotificationUsers().get(0).getId().getNotificationId(), is(notification.getNotificationId()));
    }
    
    @Test
    public void testDeleteEmergencyNotification()
    {
    	Notification notification = new Notification();
        notification.setBody("<p>Emergency Notification</p>");
        notification.setTopic("Emergency");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("12");
        notification.setTitle("Emergency");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        
        String notificationId = notification.getNotificationId();
        assertThat(notificationId, is(notNullValue()));
        
        notificationRepository.delete(notificationId);
        
        notification = notificationRepository.findOne(notificationId);
        
        assertThat(notification, is(nullValue()));
    }
    
    @Test
    public void testDeleteRegularNotification() {
    	
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
        
        String notificationId = notification.getNotificationId();
        assertThat(notificationId, is(notNullValue()));
        
        notificationRepository.delete(notificationId);
        
        notification = notificationRepository.findOne(notificationId);
        
        assertThat(notification, is(nullValue()));
        
        user = notificationUserRepository.findOne(new NotificationUserPK(notificationId, "user"));
        
        assertThat(user, is(nullValue()));
    }
    
    @Test
    public void testGetNotificationByPublisher() {
    	
    	Notification notification = new Notification();
        notification.setBody("<p>Emergency Notification by yes-publisher</p>");
        notification.setTopic("Emergency");
        notification.setPublisherId("yes-publisher");
        notification.setPublisherNotificationId("14");
        notification.setTitle("Emergency");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        
        notification = new Notification();
        notification.setBody("<p>Emergency Notification by no-publisher</p>");
        notification.setTopic("Emergency");
        notification.setPublisherId("no-publisher");
        notification.setPublisherNotificationId("15");
        notification.setTitle("Emergency");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        notificationRepository.save(notification);
        
        List<Notification> notificationList = notificationRepository.findByPublisherId("yes-publisher");
        assertThat(notificationList, is(notNullValue()));
        assertThat(notificationList, hasSize(1));
        assertThat(notificationList.get(0).getPublisherId(), is("yes-publisher"));
    }
    
    @Test
    public void testUpdateRegularNotificationAddUser() {
    	
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
        user.setId(new NotificationUserPK(null,"bolek"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        String notificationId = notification.getNotificationId();
        
        user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"antek"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        assertThat(notificationId, is(notification.getNotificationId()));
        
        users = notificationUserRepository.findByIdNotificationId(notificationId);
        assertThat(users, hasSize(2));
        assertThat(users.get(0).getId().getUun(), isOneOf("bolek","antek"));
        assertThat(users.get(1).getId().getUun(), isOneOf("bolek","antek"));
    }
    
    @Test
    public void testUpdateRegularNotificationDeleteUser() {
    	
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
        user.setId(new NotificationUserPK(null,"bolek"));
        users.add(user);
        
        user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"thumper"));
        users.add(user);
        
        user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"donald"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        String notificationId = notification.getNotificationId();
        
        for(int i = 0; i < users.size(); i++){
        	if(users.get(i).getId().getUun().equals("bolek")){
        		users.remove(i);
        	}
        }
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        assertThat(notificationId, is(notification.getNotificationId()));
        
        users = notificationUserRepository.findByIdNotificationId(notificationId);
        assertThat(users, hasSize(2));
        assertThat(users.get(0).getId().getUun(), is(not("bolek")));
        assertThat(users.get(1).getId().getUun(), is(not("bolek")));
    }
    
    @Test
    public void testGetNotificationByUun() {
    	
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
        user.setId(new NotificationUserPK(null,"yes-user"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        String yesNotificationId = notification.getNotificationId();
        assertThat(yesNotificationId, is(notNullValue()));
        
        notification = new Notification();
        notification.setBody("<p>Regular Notification 2</p>");
        notification.setTopic("Notification");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("14");
        notification.setTitle("Notify Announcement");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        
        users = new ArrayList<NotificationUser>();
        user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"no-user"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
    	
        List<Notification> notificationList = notificationRepository.findByUun("yes-user");
        assertThat(notificationList, is(notNullValue()));
        assertThat(notificationList, hasSize(1));
        assertThat(notificationList.get(0).getNotificationId(), is(yesNotificationId));
        assertThat(notificationList.get(0).getNotificationUsers(), hasSize(1));
        assertThat(notificationList.get(0).getNotificationUsers().get(0).getId().getUun(), is("yes-user"));
    }  
    
    @Test
    public void testGetNotificationByUunAndDate() {
    	
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
        user.setId(new NotificationUserPK(null,"donald"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        notification = new Notification();
        notification.setBody("<p>Regular Notification 2</p>");
        notification.setTopic("Notification");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("14");
        notification.setTitle("Notify Announcement Future");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(dateFuture);
        
        users = new ArrayList<NotificationUser>();
        user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"donald"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        String notificationId = notification.getNotificationId();
        assertThat(notificationId, is(notNullValue()));
        
        List<Notification> notificationList = notificationRepository.findByUunAndDate("donald", new Date());
        assertThat(notificationList, hasSize(1));
        assertThat(notificationList.get(0).getNotificationId(), is(notificationId));
        assertThat(notificationList.get(0).getTitle(), is("Notify Announcement Future"));
    }
    
    @Test
    public void testGetNotificationByUunAndTopic() {
    	
    	Notification notification = new Notification();
        notification.setBody("<p>Regular Notification 1</p>");
        notification.setTopic("YES TOPIC");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("13");
        notification.setTitle("Notify Announcement YES");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        
        List<NotificationUser> users = new ArrayList<NotificationUser>();
        NotificationUser user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"donald"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        String notificationId = notification.getNotificationId();
        assertThat(notificationId, is(notNullValue()));
        
        notification = new Notification();
        notification.setBody("<p>Regular Notification 2</p>");
        notification.setTopic("YES TOPIC");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("14");
        notification.setTitle("Notify Announcement Future");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(dateFuture);
        
        users = new ArrayList<NotificationUser>();
        user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"hilarry"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        notification = new Notification();
        notification.setBody("<p>Regular Notification 3</p>");
        notification.setTopic("NO TOPIC");
        notification.setPublisherId("notify-ui");
        notification.setPublisherNotificationId("14");
        notification.setTitle("Notify Announcement Future");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(dateFuture);
        
        users = new ArrayList<NotificationUser>();
        user = new NotificationUser();
        user.setNotification(notification);
        user.setId(new NotificationUserPK(null,"donald"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notificationRepository.save(notification);
        
        List<Notification> notificationList = notificationRepository.findByUunAndTopic("donald", "YES TOPIC");
        assertThat(notificationList, hasSize(1));
        assertThat(notificationList.get(0).getNotificationId(), is(notificationId));
        assertThat(notificationList.get(0).getTitle(), is("Notify Announcement YES"));
    }
    
    @Test
    public void testGetNotificationByPublisherAndDate() {
    	
    	Notification notification = new Notification();
        notification.setBody("<p>Regular Notification</p>");
        notification.setTopic("Notification");
        notification.setPublisherId("yes-publisher");
        notification.setPublisherNotificationId("13");
        notification.setTitle("Notify Announcement Future OK");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(dateFuture);
        
        notificationRepository.save(notification);
        
        String notificationId = notification.getNotificationId();
        assertThat(notificationId, is(notNullValue()));
        
        notification = new Notification();
        notification.setBody("<p>Regular Notification 2</p>");
        notification.setTopic("Notification");
        notification.setPublisherId("yes-publisher");
        notification.setPublisherNotificationId("14");
        notification.setTitle("Notify Announcement Past NOT OK");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);
        
        notificationRepository.save(notification);
        
        List<Notification> notificationList = notificationRepository.findByPublisherIdAndDate("yes-publisher", new Date());
        assertThat(notificationList, hasSize(1));
        assertThat(notificationList.get(0).getNotificationId(), is(notificationId));
        assertThat(notificationList.get(0).getTitle(), is("Notify Announcement Future OK"));
    }

    @Test
    public void testGetNotificationByPublisherTopicAndDate()
    {
        Notification notification = new Notification();
        notification.setBody("<p>Regular Notification</p>");
        notification.setTopic("Emergency");
        notification.setPublisherId("yes-publisher");
        notification.setPublisherNotificationId("13");
        notification.setTitle("Notify Announcement Future OK");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(dateFuture);

        notificationRepository.save(notification);

        String notificationId = notification.getNotificationId();
        assertThat(notificationId, is(notNullValue()));

        notification = new Notification();
        notification.setBody("<p>Regular Notification 2</p>");
        notification.setTopic("Emergency");
        notification.setPublisherId("yes-publisher");
        notification.setPublisherNotificationId("14");
        notification.setTitle("Notify Announcement Past NOT OK");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(date);

        notificationRepository.save(notification);

        notification = new Notification();
        notification.setBody("<p>Regular Notification</p>");
        notification.setTopic("Group");
        notification.setPublisherId("yes-publisher");
        notification.setPublisherNotificationId("13");
        notification.setTitle("Notify Announcement Future OK");
        notification.setUrl("http://www.google.co.uk");
        notification.setStartDate(date);
        notification.setEndDate(dateFuture);

        notificationRepository.save(notification);

        List<Notification> notificationList = notificationRepository.findByPublisherIdTopicAndDate("yes-publisher", "Emergency",new Date());
        assertThat(notificationList, hasSize(1));
        assertThat(notificationList.get(0).getNotificationId(), is(notificationId));
        assertThat(notificationList.get(0).getTitle(), is("Notify Announcement Future OK"));
    }
}
