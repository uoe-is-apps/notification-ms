import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ed.notify.entity.PublisherDetails;
import uk.ac.ed.notify.repository.PublisherDetailsRepository;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by rgood on 20/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class PublisherDetailsRepositoryTest {


    @Autowired
    PublisherDetailsRepository publisherDetailsRepository;

    private Date date;

    @Before
    public void setup()
    {
        date = new Date();
        date.setTime(1445347648);
    }

    @Test
    public void testCreatePublisherDetails()
    {

        PublisherDetails publisherDetails = new PublisherDetails();
        publisherDetails.setPublisherId("TESTPUB");
        publisherDetails.setDescription("DESCRIPTION");
        publisherDetails.setKey("12345");
        publisherDetails.setLastUpdated(date);
        publisherDetails.setPublisherType("PULL");
        publisherDetails.setStatus("A");

        publisherDetailsRepository.save(publisherDetails);

    }

    @Test
    public void testGetPublisherDetails()
    {
        PublisherDetails publisherDetails = new PublisherDetails();
        publisherDetails.setPublisherId("TESTPUB");
        publisherDetails.setDescription("DESCRIPTION");
        publisherDetails.setKey("12345");
        publisherDetails.setLastUpdated(date);
        publisherDetails.setPublisherType("PULL");
        publisherDetails.setStatus("A");

        publisherDetailsRepository.save(publisherDetails);
        publisherDetails = publisherDetailsRepository.findOne(publisherDetails.getPublisherId());
        assertEquals("DESCRIPTION",publisherDetails.getDescription());
        assertEquals("12345",publisherDetails.getKey());
        assertEquals(date.getTime(),publisherDetails.getLastUpdated().getTime());
        assertEquals("PULL",publisherDetails.getPublisherType());
        assertEquals("A",publisherDetails.getStatus());
    }

    @Test
    public void testDeletePublisherDetails()
    {
        PublisherDetails publisherDetails = new PublisherDetails();
        publisherDetails.setPublisherId("TESTPUB");
        publisherDetails.setDescription("DESCRIPTION");
        publisherDetails.setKey("12345");
        publisherDetails.setLastUpdated(date);
        publisherDetails.setPublisherType("PULL");
        publisherDetails.setStatus("A");

        publisherDetailsRepository.save(publisherDetails);
        publisherDetailsRepository.delete(publisherDetails);
        publisherDetails = publisherDetailsRepository.findOne(publisherDetails.getPublisherId());
        assertNull(publisherDetails);
    }
}
