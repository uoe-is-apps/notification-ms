package uk.ac.ed.notify;

import org.springframework.data.repository.CrudRepository;
import uk.ac.ed.notify.entity.Notification;

/**
 * Created by rgood on 18/09/2015.
 */
public interface NotificationRepository extends CrudRepository<Notification,String>{
}
