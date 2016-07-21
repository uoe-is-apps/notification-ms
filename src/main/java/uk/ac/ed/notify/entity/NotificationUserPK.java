package uk.ac.ed.notify.entity;

import java.io.Serializable;

public class NotificationUserPK implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String notificationId;
	private String uun;
	
	public NotificationUserPK() {}
	
	public NotificationUserPK(String notificationId, String uun){
		this.notificationId = notificationId;
		this.uun = uun;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public String getUun() {
		return uun;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public void setUun(String uun) {
		this.uun = uun;
	}
	
//implement hashcode and equals if using Sets
}
