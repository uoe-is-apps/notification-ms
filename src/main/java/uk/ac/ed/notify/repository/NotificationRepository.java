package uk.ac.ed.notify.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.ed.notify.entity.Notification;

import java.util.Date;
import java.util.List;

/**
 * Created by rgood on 18/09/2015.
 */

public interface NotificationRepository extends CrudRepository<Notification,String>{

    List<Notification> findByPublisherId(String publisherId);

    List<Notification> findByUun(String uun);

    List<Notification> findByUunAndDate(String uun,Date date);

    List<Notification> findByUunAndTopic(String uun, String topic);

    List<Notification> findByUunTopicAndDate(String uun,String topic, Date date);

    List<Notification> findByPublisherIdAndDate(String publisherId, Date date);

    List<Notification> findByPublisherIdTopicAndDate(String publisherId, String topic, Date date);

}
