package uk.ac.ed.notify.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="notification_users")
public class NotificationUser {

	@EmbeddedId
	@JsonProperty("user")
	private NotificationUserPK id;
	
	@ManyToOne
	@JoinColumn(name = "notification_id", nullable = false, insertable=false, updatable = false)
	@JsonIgnore
	private Notification notification;

	public NotificationUserPK getId() {
		return id;
	}

	public void setId(NotificationUserPK id) {
		this.id = id;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
}