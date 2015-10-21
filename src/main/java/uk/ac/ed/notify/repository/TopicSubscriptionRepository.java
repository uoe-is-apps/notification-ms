package uk.ac.ed.notify.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.ed.notify.entity.TopicSubscription;

import java.util.List;

/**
 * Created by rgood on 21/10/2015.
 */
public interface TopicSubscriptionRepository extends CrudRepository<TopicSubscription,String> {

    List<TopicSubscription> findBySubscriberId(String subscriberId);

}
