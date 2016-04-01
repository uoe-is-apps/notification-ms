package uk.ac.ed.notify.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ed.notify.entity.TopicSubscription;

import java.util.List;

/**
 * Created by rgood on 21/10/2015.
 */
@RepositoryRestResource(exported = false)
public interface TopicSubscriptionRepository extends CrudRepository<TopicSubscription,String> {

    List<TopicSubscription> findBySubscriberId(String subscriberId);

}
