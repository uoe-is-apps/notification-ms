package uk.ac.ed.notify;

import java.io.Serializable;
import java.util.List;

import uk.ac.ed.notify.entity.NotificationUser;
/*
 * wrapper for List, temporary solution, ideally configure @JsonCreator
 * error: com.fasterxml.jackson.databind.JsonMappingException: Can not instantiate value of type [simple type, class ...] from String value; no single-String constructor/factory method
 */
public class NotificationUserList implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<NotificationUser> users;

	public List<NotificationUser> getUsers() {
		return users;
	}

	public void setUsers(List<NotificationUser> users) {
		this.users = users;
	}
	
	
}
