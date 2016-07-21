package uk.ac.ed.notify.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="notification_users")
public class NotificationUser {

	@EmbeddedId
	private NotificationUserPK user;
	
	@ManyToOne
	@JoinColumn(name = "notification_id", nullable = false, insertable=false, updatable = false)
	@JsonIgnore
	private Notification notification;

	public NotificationUserPK getUser() {
		return user;
	}

	public void setUser(NotificationUserPK user) {
		this.user = user;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
}