import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ed.notify.repository.NotificationErrorRepository;
import uk.ac.ed.notify.entity.NotificationError;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by rgood on 20/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class NotificationErrorRepositoryTest {

    @Autowired
    NotificationErrorRepository errorRepository;

    private Date date;

    @Before
    public void setup()
    {
        date = new Date();
        date.setTime(1445347648);
    }

    @Test
    public void testCreateError()
    {
        NotificationError notificationError = new NotificationError();
        notificationError.setErrorCode("TEST");
        notificationError.setErrorDescription("Error description");
        notificationError.setErrorDate(date);
        errorRepository.save(notificationError);
    }

    @Test
    public void testGetError()
    {
        System.out.println("Start");
        NotificationError notificationError = new NotificationError();
        notificationError.setErrorCode("TEST");
        notificationError.setErrorDescription("Error description");
        notificationError.setErrorDate(date);
        errorRepository.save(notificationError);
        notificationError = errorRepository.findOne(notificationError.getErrorId());
        assertEquals("TEST",notificationError.getErrorCode());
        assertEquals("Error description",notificationError.getErrorDescription());
        assertEquals(date.getTime(),notificationError.getErrorDate().getTime());
    }

    @Test
    public void testDeleteError()
    {
        NotificationError notificationError = new NotificationError();
        notificationError.setErrorCode("TEST");
        notificationError.setErrorDescription("Error description");
        notificationError.setErrorDate(date);
        errorRepository.save(notificationError);
        NotificationError notification = errorRepository.findOne(notificationError.getErrorId());
        errorRepository.delete(notification);
        notification = errorRepository.findOne(notification.getErrorId());
        assertNull(notification);

    }

}
