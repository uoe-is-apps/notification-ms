package uk.ac.ed.notify.repository.test;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import uk.ac.ed.notify.entity.NotificationUser;
import uk.ac.ed.notify.entity.NotificationUserPK;

public interface NotificationUserRepository extends CrudRepository<NotificationUser, NotificationUserPK> {

	List<NotificationUser> findByIdNotificationId(String notificationId);
}
