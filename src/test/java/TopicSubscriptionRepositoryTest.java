import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ed.notify.entity.TopicSubscription;
import uk.ac.ed.notify.repository.TopicSubscriptionRepository;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by rgood on 21/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
public class TopicSubscriptionRepositoryTest {

    @Autowired
    TopicSubscriptionRepository topicSubscriptionRepository;

    private Date date;

    @Before
    public void setup()
    {
        date = new Date();
        date.setTime(1445347648);
    }

    @Test
    public void testCreateTopicSubscription()
    {
        TopicSubscription topicSubscription = new TopicSubscription();
        topicSubscription.setSubscriberId("TESTSUB");
        topicSubscription.setTopic("TEST TOPIC");
        topicSubscription.setStatus("A");
        topicSubscription.setLastUpdated(date);
        topicSubscriptionRepository.save(topicSubscription);

    }

    @Test
    public void testGetTopicSubscription() {

        TopicSubscription topicSubscription = new TopicSubscription();
        topicSubscription.setSubscriberId("TESTSUB");
        topicSubscription.setTopic("TEST TOPIC");
        topicSubscription.setStatus("A");
        topicSubscription.setLastUpdated(date);
        topicSubscriptionRepository.save(topicSubscription);
        topicSubscription = topicSubscriptionRepository.findOne(topicSubscription.getSubscriptionId());
        assertEquals("TESTSUB",topicSubscription.getSubscriberId());
        assertEquals("TEST TOPIC",topicSubscription.getTopic());
        assertEquals("A",topicSubscription.getStatus());
        assertEquals(date,topicSubscription.getLastUpdated());


    }



    @Test
    public void testDeleteTopicSubscription()
    {
        TopicSubscription topicSubscription = new TopicSubscription();
        topicSubscription.setSubscriberId("TESTSUB");
        topicSubscription.setTopic("TEST TOPIC");
        topicSubscription.setStatus("A");
        topicSubscription.setLastUpdated(date);
        topicSubscriptionRepository.save(topicSubscription);
        topicSubscriptionRepository.delete(topicSubscription);
        topicSubscription = topicSubscriptionRepository.findOne(topicSubscription.getSubscriptionId());
        assertNull(topicSubscription);

    }


}
