package uk.ac.ed.notify.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ed.notify.entity.UserNotificationAudit;

/**
 * Created by rgood on 20/10/2015.
 */
@RepositoryRestResource(exported = false)
public interface UserNotificationAuditRepository extends CrudRepository<UserNotificationAudit,String> {
}
