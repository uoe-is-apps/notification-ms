package uk.ac.ed.notify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.ac.ed.notify.entity.Notification;
import uk.ac.ed.notify.entity.NotificationUser;
import uk.ac.ed.notify.entity.NotificationUserPK;

public class NotificationStubResponse {
	
	public static Notification getSingleNotification() {
		
		Notification notification = new Notification();
        notification.setBody("Test notification");
        notification.setTitle("Test title");
        notification.setStartDate(new Date());
        notification.setEndDate(new Date());
        notification.setTopic("Test topic");
        notification.setUrl("http://www.ed.ac.uk");
        
        List<NotificationUser> users = new ArrayList<NotificationUser>();
        
        NotificationUser user = new NotificationUser();
        user.setId(new NotificationUserPK(null,"user1"));
        users.add(user);
        user = new NotificationUser();
        user.setId(new NotificationUserPK(null,"user2"));
        users.add(user);
        
        notification.setNotificationUsers(users);
        notification.setLastUpdated(new Date());
        
        return notification;
	}
	
	public static List<Notification> getNotificationsList() {
		
		List<NotificationUser> users = new ArrayList<NotificationUser>();
        
        NotificationUser user = new NotificationUser();
        user.setId(new NotificationUserPK(null,"user1"));
        users.add(user);
        user = new NotificationUser();
        user.setId(new NotificationUserPK(null,"user2"));
        users.add(user);
        
        List<Notification> notifications = new ArrayList<>();
        
        Notification notification = new Notification();
        notification.setBody("Test notification");
        notification.setTitle("Test title");
        notification.setStartDate(new Date());
        notification.setEndDate(new Date());
        notification.setTopic("Test topic");
        notification.setUrl("http://www.ed.ac.uk");
        notification.setNotificationUsers(users);
        notification.setLastUpdated(new Date());
        notifications.add(notification);
        
        notification.setTitle("Test title 2");
        notification.setBody("Test notification 2");
        notifications.add(notification);
        
        return notifications;
	}
	
	public static NotificationResponse getNotificationResponse() {
		
		NotificationResponse notificationResponse = new NotificationResponse();
		
		Date dateNow = new Date();
        List<NotificationCategory> categories = new ArrayList<NotificationCategory>();
        NotificationCategory category;
        NotificationEntry entry;
        List<NotificationEntry> entries;

        category = new NotificationCategory();
        category.setTitle("Emergency");
        entries = new ArrayList<NotificationEntry>();

            entry = new NotificationEntry();
            entry.setBody("This is a test");
            entry.setTitle("Test title");
            entry.setDueDate(dateNow);
            entry.setUrl("http://www.ed.ac.uk");
            entries.add(entry);


        category.setEntries(entries);
        categories.add(category);

        notificationResponse.setCategories(categories);
        
        return notificationResponse;
	}

}
