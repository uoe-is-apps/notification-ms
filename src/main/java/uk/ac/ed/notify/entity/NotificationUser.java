package uk.ac.ed.notify.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@IdClass(NotificationUserPK.class)
@Table(name="notification_users")
public class NotificationUser {

	@Id
    @Column(name="notification_id")
	@JsonIgnore
	private String notificationId;
	
	@Id
	@Column(name="uun")
	private String uun;

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getUun() {
		return uun;
	}

	public void setUun(String uun) {
		this.uun = uun;
	}
}