import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.repository.NotificationRepository;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by rgood on 20/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class NotificationRepositoryTest {

    @Autowired
    NotificationRepository notificationRepository;

    private Date date;

    @Before
    public void setup()
    {
        date = new Date();
        date.setTime(1445347648);
    }

    @Test
    public void testCreateNotification()
    {
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

    }

    @Test
    public void testGetNotification()
    {
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
        notification = notificationRepository.findOne(notification.getNotificationId());
        assertEquals("<p>Test</p>",notification.getBody());
        assertEquals("TESTCATEGORY",notification.getCategory());
        assertEquals("TESTPUB",notification.getPublisherId());
        assertEquals("12",notification.getPublisherNotificationId());
        assertEquals("TESTTITLE",notification.getTitle());
        assertEquals("http://www.google.co.uk",notification.getUrl());
        assertEquals("TESTUUN",notification.getUun());
        assertEquals(date,notification.getStartDate());
        assertEquals(date,notification.getEndDate());
    }

    @Test
    public void testDeleteNotification()
    {
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
        notificationRepository.delete(notification);
        notification = notificationRepository.findOne(notification.getNotificationId());
    }

    @Test
    public void testGetNotificationByPublisher()
    {
        //TODO Add get by publisher test, one valid, one invalid entity, test only one returned

    }

}
