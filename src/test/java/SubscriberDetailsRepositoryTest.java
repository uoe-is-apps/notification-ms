import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ed.notify.entity.SubscriberDetails;
import uk.ac.ed.notify.repository.SubscriberDetailsRepository;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by rgood on 21/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class SubscriberDetailsRepositoryTest {

    @Autowired
    SubscriberDetailsRepository subscriberDetailsRepository;

    private Date date;

    @Before
    public void setup()
    {
        date = new Date();
        date.setTime(1445347648);
    }

    @Test
    public void testCreateSubscriberDetails()
    {
        SubscriberDetails subscriberDetails = new SubscriberDetails();
        subscriberDetails.setSubscriberId("TESTSUB");
        subscriberDetails.setDescription("SUBDETAILS");
        subscriberDetails.setLastUpdated(date);
        subscriberDetails.setStatus("A");
        subscriberDetails.setType("PULL");
        subscriberDetailsRepository.save(subscriberDetails);
    }

    @Test
    public void testGetSubscriberDetails()
    {
        SubscriberDetails subscriberDetails = new SubscriberDetails();
        subscriberDetails.setSubscriberId("TESTSUB");
        subscriberDetails.setDescription("SUBDETAILS");
        subscriberDetails.setLastUpdated(date);
        subscriberDetails.setStatus("A");
        subscriberDetails.setType("PULL");
        subscriberDetailsRepository.save(subscriberDetails);
        subscriberDetails = subscriberDetailsRepository.findOne(subscriberDetails.getSubscriberId());
        assertEquals("SUBDETAILS",subscriberDetails.getDescription());
        assertEquals(date,subscriberDetails.getLastUpdated());
        assertEquals("A",subscriberDetails.getStatus());
        assertEquals("PULL",subscriberDetails.getType());
    }

    @Test
    public void testDeleteSubscriberDetails()
    {

        SubscriberDetails subscriberDetails = new SubscriberDetails();
        subscriberDetails.setSubscriberId("TESTSUB");
        subscriberDetails.setDescription("SUBDETAILS");
        subscriberDetails.setLastUpdated(date);
        subscriberDetails.setStatus("A");
        subscriberDetails.setType("PULL");
        subscriberDetailsRepository.save(subscriberDetails);
        subscriberDetailsRepository.delete(subscriberDetails);
        subscriberDetails = subscriberDetailsRepository.findOne(subscriberDetails.getSubscriberId());
        assertNull(subscriberDetails);

    }
}
