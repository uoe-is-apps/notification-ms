package uk.ac.ed.notify.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.ed.notify.entity.Notification;

import java.util.List;

/**
 * Created by rgood on 18/09/2015.
 */
public interface NotificationRepository extends CrudRepository<Notification,String>{

    public List<Notification> findByPublisherId (String publisherId);

    public List<Notification> findByUun (String uun);

    public List<Notification> findByUunAndTopic (String uun, String topic);

}