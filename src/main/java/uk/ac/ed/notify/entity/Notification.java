package uk.ac.ed.notify.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by rgood on 18/09/2015.
 */
@Entity
@Table(name="PUBLISHER_NOTIFICATION")
public class Notification {

        @Id
        @GeneratedValue(generator="system-uuid")
        @GenericGenerator(name="system-uuid",
                strategy = "uuid")
        @Column(name="NOTIFICATION_ID")
        private String notificationId;

        @Column(name="PUBLISHER_ID")
        private String publisherId;

        @Column(name="PUBLISHER_NOTIFICATION_ID")
        private String publisherNotificationId;

        @Column(name="CATEGORY")
        private String category;

        @Column(name="TITLE")
        private String title;

        @Column(name="BODY")
        private String body;

        @Column(name="URL")
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

        public String getCategory() {
                return category;
        }

        public void setCategory(String category) {
                this.category = category;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getBody() {
                return body;
        }

        public void setBody(String body) {
                this.body = body;
        }

        public String getUrl() {
                return url;
        }

        public void setUrl(String url) {
                this.url = url;
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
}
