package uk.ac.ed.notify.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.hibernate.annotations.GenericGenerator;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

/**
 * Created by rgood on 18/09/2015.
 */
@Entity
@Table(name="NOTIFICATIONS", schema = "NOTIFY")
@NamedQueries({
        @NamedQuery(name = "Notification.findByPublisherId", query = "SELECT n FROM Notification n WHERE n.publisherId = (?1)"),
        @NamedQuery(name = "Notification.findByPublisherIdAndDate", query = "SELECT n FROM Notification n WHERE n.publisherId = (?1) and (n.startDate <=(?2) or n.startDate = NULL) and (n.endDate >= (?2) or n.endDate = NULL)"),
        @NamedQuery(name = "Notification.findByUun", query = "SELECT n FROM Notification n JOIN n.notificationUsers b WHERE b.uun = (?1)"),
        @NamedQuery(name = "Notification.findByUunAndDate", query = "SELECT n FROM Notification n JOIN n.notificationUsers b WHERE b.uun = (?1) and (n.startDate <=(?2) or n.startDate = NULL) and (n.endDate >= (?2) or n.endDate = NULL)"),
        @NamedQuery(name = "Notification.findByUunAndTopic", query = "SELECT n FROM Notification n JOIN n.notificationUsers b WHERE b.uun = (?1) and n.topic = (?2)"),
        @NamedQuery(name = "Notification.findByUunTopicAndDate", query = "SELECT n FROM Notification n JOIN n.notificationUsers b WHERE b.uun = (?1) and n.topic = (?2) and (n.startDate <=(?3) or n.startDate = NULL) and (n.endDate >= (?3) or n.endDate = NULL)")
})
public class Notification {

        private static final Logger logger = LoggerFactory.getLogger(Notification.class);

        //TODO Add status value validation
        @Id
        @GeneratedValue(generator="system-uuid")
        @GenericGenerator(name="system-uuid",
                strategy = "uuid")
        @Column(name="NOTIFICATION_ID", nullable = false)
        private String notificationId;

        @Column(name="PUBLISHER_ID", nullable = false)
        private String publisherId;

        @Column(name="PUBLISHER_NOTIFICATION_ID")
        private String publisherNotificationId;

        @Column(name="TOPIC", nullable = false)
        private String topic;

        @Column(name="TITLE", nullable = false)
        private String title;

        @Column(name="NOTIFICATION_BODY", nullable = false)
        private String body;

        @Column(name="NOTIFICATION_URL")
        private String url;

        @JsonSerialize(using=DatePartSerializer.class)
        @Column(name="START_DATE")
        @Temporal(TemporalType.TIMESTAMP)
        private Date startDate;

        @JsonSerialize(using=DatePartSerializer.class)
        @Column(name="END_DATE")
        @Temporal(TemporalType.TIMESTAMP)
        private Date endDate;

        @JsonSerialize(using=DatePartSerializer.class)
        @Column(name="LAST_UPDATED")
        @Temporal(TemporalType.TIMESTAMP)
        private Date lastUpdated;

        @OneToMany(fetch = FetchType.EAGER, mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<NotificationUser> notificationUsers;
        
        public String getNotificationId() {
                return notificationId;
        }

        public void setNotificationId(String notificationId) {
                this.notificationId = notificationId;
        }

        public String getPublisherId() {
                return publisherId;
        }

        public void setPublisherId(String publisherId) {
                this.publisherId = publisherId;
        }

        public String getPublisherNotificationId() {
                return publisherNotificationId;
        }

        public void setPublisherNotificationId(String publisherNotificationId) {
                this.publisherNotificationId = publisherNotificationId;
        }

        public String getTopic() {
                return topic;
        }

        public void setTopic(String topic) {

                String cleaned = Jsoup.clean(topic, Whitelist.basic());
                if (!cleaned.equals(topic)) {
                        logger.warn("notification topic for "+notificationId+"cleaned, was ("+topic);
                }
                this.topic = cleaned;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                String cleaned = Jsoup.clean(title, Whitelist.basic());
                if (!cleaned.equals(title)) {
                        logger.warn("notification title for "+notificationId+"cleaned, was ("+title);
                }
                this.title = cleaned;
        }

        public String getBody() {
                return body;
        }

        public void setBody(String body) {
                String cleaned = Jsoup.clean(body, Whitelist.basic());
                if (!cleaned.equals(body)) {
                        logger.warn("notification body for "+notificationId+"cleaned, was ("+body);
                }
                this.body = cleaned;
        }

        public String getUrl() {
                return url;
        }

        public void setUrl(String url) {

                String cleaned = Jsoup.clean(url, Whitelist.basic());
                if (!cleaned.equals(url)) {
                        logger.warn("notification url for "+notificationId+"cleaned, was ("+url);
                }
                this.url = cleaned;
        }
        public Date getStartDate() {
                return startDate;
        }

        public void setStartDate(Date startDate) {
                this.startDate = startDate;
        }

        public Date getEndDate() {
                return endDate;
        }

        public void setEndDate(Date endDate) {
                this.endDate = endDate;
        }

        public Date getLastUpdated() {
                return lastUpdated;
        }

        public void setLastUpdated(Date lastUpdated) {
                this.lastUpdated = lastUpdated;
        }

		public List<NotificationUser> getNotificationUsers() {
			return notificationUsers;
		}

		public void setNotificationUsers(List<NotificationUser> notificationUsers) {
			this.notificationUsers = notificationUsers;
		}
}
