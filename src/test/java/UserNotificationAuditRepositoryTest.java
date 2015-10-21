
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ed.notify.entity.UserNotificationAudit;
import uk.ac.ed.notify.repository.UserNotificationAuditRepository;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by rgood on 20/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class UserNotificationAuditRepositoryTest {

    @Autowired
    private UserNotificationAuditRepository userNotificationAuditRepository;

    private Date date;

    @Before
    public void setup()
    {
        date = new Date();
        date.setTime(1445347648);
    }

    @Test
    public void testCreateUserNotificationAudit()
    {
        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
        userNotificationAudit.setAction("USER_VIEWED");
        userNotificationAudit.setAuditDate(date);
        userNotificationAudit.setPublisherId("TEST");
        userNotificationAudit.setUun("TESTUSER");
        userNotificationAuditRepository.save(userNotificationAudit);
    }

    @Test
    public void testGetUserNotificationAudit()
    {
        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
        userNotificationAudit.setAction("USER_VIEWED");
        userNotificationAudit.setAuditDate(date);
        userNotificationAudit.setPublisherId("TEST");
        userNotificationAudit.setUun("TESTUSER");
        userNotificationAuditRepository.save(userNotificationAudit);
        userNotificationAudit = userNotificationAuditRepository.findOne(userNotificationAudit.getAuditId());
        assertEquals("USER_VIEWED",userNotificationAudit.getAction());
        assertEquals("TEST",userNotificationAudit.getPublisherId());
        assertEquals("TESTUSER",userNotificationAudit.getUun());
        assertEquals(date.getTime(),userNotificationAudit.getAuditDate().getTime());
    }

    @Test
    public void testDeleteUserNotificationAudit()
    {
        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
        userNotificationAudit.setAction("USER_VIEWED");
        userNotificationAudit.setAuditDate(date);
        userNotificationAudit.setPublisherId("TEST");
        userNotificationAudit.setUun("TESTUSER");
        userNotificationAuditRepository.save(userNotificationAudit);
        userNotificationAuditRepository.delete(userNotificationAudit);
        userNotificationAudit = userNotificationAuditRepository.findOne(userNotificationAudit.getAuditId());
        assertNull(userNotificationAudit);
    }


}
