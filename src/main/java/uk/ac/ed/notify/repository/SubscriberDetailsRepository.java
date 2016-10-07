package uk.ac.ed.notify.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.ed.notify.entity.SubscriberDetails;

/**
 * Created by rgood on 21/10/2015.
 */
public interface SubscriberDetailsRepository extends CrudRepository<SubscriberDetails,String> {
}
