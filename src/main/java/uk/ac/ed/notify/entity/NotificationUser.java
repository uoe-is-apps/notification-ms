package uk.ac.ed.notify.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@IdClass(NotificationUserPK.class)
@Table(name="notification_users")
public class NotificationUser {

	@Id
    @Column(name="notification_id", nullable = false)
	@JsonIgnore
	private String notificationId;
	
	@Id
	@Column(name="uun", nullable = false)
	private String uun;
	
	@ManyToOne
	@JoinColumn(name = "notification_id", nullable = false, insertable=false, updatable = false)
	@JsonIgnore
	private Notification notification;

	
	public String getNotificationId() {
		return notificationId;
	}

	public String getUun() {
		return uun;
	}
	
	public Notification getNotification() {
		return notification;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}
	
	public void setUun(String uun) {
		this.uun = uun;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
}