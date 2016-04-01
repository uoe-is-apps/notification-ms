package uk.ac.ed.notify.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ed.notify.entity.NotificationError;

/**
 * Created by rgood on 20/10/2015.
 */
@RepositoryRestResource(exported = false)
public interface NotificationErrorRepository extends CrudRepository<NotificationError,String> {
}
