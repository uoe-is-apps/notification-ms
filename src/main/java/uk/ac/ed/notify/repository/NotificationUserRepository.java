package uk.ac.ed.notify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import uk.ac.ed.notify.entity.NotificationUser;
import uk.ac.ed.notify.entity.NotificationUserPK;

@RepositoryRestResource(exported = false)
public interface NotificationUserRepository extends JpaRepository<NotificationUser, NotificationUserPK> {
	
}
