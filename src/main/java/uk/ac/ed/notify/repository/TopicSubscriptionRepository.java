package uk.ac.ed.notify.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.ed.notify.entity.TopicSubscription;

/**
 * Created by rgood on 21/10/2015.
 */
public interface TopicSubscriptionRepository extends CrudRepository<TopicSubscription,String> {


}
