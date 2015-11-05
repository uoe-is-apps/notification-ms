package uk.ac.ed.notify.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ed.notify.repository.UserNotificationAuditRepository;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by rgood on 18/09/2015.
 */
@Entity
@Table(name="NOTIFICATIONS", schema = "NOTIFY")
@NamedQueries({
        @NamedQuery(name = "Notification.findByPublisherId", query = "SELECT a FROM Notification a WHERE a.publisherId = (?1)"),
        @NamedQuery(name = "Notification.findByPublisherIdAndDate", query = "SELECT a FROM Notification a WHERE a.publisherId = (?1) and a.startDate <=(?2) and (a.endDate >= (?2) or a.endDate = NULL)"),
        @NamedQuery(name = "Notification.findByUun", query = "SELECT a FROM Notification a WHERE a.uun = (?1)"),
        @NamedQuery(name = "Notification.findByUunAndDate", query = "SELECT a FROM Notification a WHERE a.uun = (?1) and a.startDate <=(?2) and (a.endDate >= (?2) or a.endDate = NULL)"),
        @NamedQuery(name = "Notification.findByUunAndTopic", query = "SELECT a FROM Notification a WHERE a.uun = (?1) and a.topic = (?2)"),
        @NamedQuery(name = "Notification.findByUunTopicAndDate", query = "SELECT a FROM Notification a WHERE a.uun = (?1) and a.topic = (?2) and a.startDate <=(?3) and (a.endDate >= (?3) or a.endDate = NULL)")
})
public class Notification {

        @Autowired
        @Transient
        UserNotificationAuditRepository userNotificationAuditRepository;

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

        @Column(name="uun")
        private String uun;

        @JsonSerialize(using=DatePartSerializer.class)
        @Column(name="LAST_UPDATED")
        @Temporal(TemporalType.TIMESTAMP)
        private Date lastUpdated;

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

                String cleaned = Jsoup.clean(topic,Whitelist.basic());
                if (!cleaned.equals(topic))
                {
                        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
                        userNotificationAudit.setAction(AuditActions.CLEANED_HTML);
                        userNotificationAudit.setAuditDate(new Date());
                        userNotificationAudit.setPublisherId(this.getPublisherId());
                        userNotificationAudit.setUun(this.getUun());
                        userNotificationAudit.setAuditDescription("Was ("+topic.substring(1,248)+")");
                        userNotificationAuditRepository.save(userNotificationAudit);
                }
                this.topic = cleaned;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                String cleaned = Jsoup.clean(title,Whitelist.basic());
                if (!cleaned.equals(title))
                {
                        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
                        userNotificationAudit.setAction(AuditActions.CLEANED_HTML);
                        userNotificationAudit.setAuditDate(new Date());
                        userNotificationAudit.setPublisherId(this.getPublisherId());
                        userNotificationAudit.setUun(this.getUun());
                        userNotificationAudit.setAuditDescription("Was ("+title.substring(1,248)+")");
                        userNotificationAuditRepository.save(userNotificationAudit);
                }
                this.title = cleaned;
        }

        public String getBody() {
                return body;
        }

        public void setBody(String body) {
                String cleaned = Jsoup.clean(body, Whitelist.basic());
                if (!cleaned.equals(body))
                {
                        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
                        userNotificationAudit.setAction(AuditActions.CLEANED_HTML);
                        userNotificationAudit.setAuditDate(new Date());
                        userNotificationAudit.setPublisherId(this.getPublisherId());
                        userNotificationAudit.setUun(this.getUun());
                        userNotificationAudit.setAuditDescription("Was ("+body.substring(1,248)+")");
                        userNotificationAuditRepository.save(userNotificationAudit);
                }
                this.body = cleaned;
        }

        public String getUrl() { return url; }

        public void setUrl(String url) {

                String cleaned = Jsoup.clean(url,Whitelist.basic());
                if (!cleaned.equals(url))
                {
                        UserNotificationAudit userNotificationAudit = new UserNotificationAudit();
                        userNotificationAudit.setAction(AuditActions.CLEANED_HTML);
                        userNotificationAudit.setAuditDate(new Date());
                        userNotificationAudit.setPublisherId(this.getPublisherId());
                        userNotificationAudit.setUun(this.getUun());
                        userNotificationAudit.setAuditDescription("Was ("+url.substring(1,248)+")");
                        userNotificationAuditRepository.save(userNotificationAudit);
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

        public String getUun() {
                return uun;
        }

        public void setUun(String uun) {
                this.uun = uun;
        }

        public Date getLastUpdated() {
                return lastUpdated;
        }

        public void setLastUpdated(Date lastUpdated) {
                this.lastUpdated = lastUpdated;
        }
}
