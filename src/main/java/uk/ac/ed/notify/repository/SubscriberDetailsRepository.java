package uk.ac.ed.notify.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ed.notify.entity.SubscriberDetails;

/**
 * Created by rgood on 21/10/2015.
 */
@RepositoryRestResource(exported = false)
public interface SubscriberDetailsRepository extends CrudRepository<SubscriberDetails,String> {
}
