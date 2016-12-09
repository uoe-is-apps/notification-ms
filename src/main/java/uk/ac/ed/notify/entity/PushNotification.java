package uk.ac.ed.notify.entity;

/**
 * Created by rgood on 28/11/2016.
 */
public class PushNotification {

    private String title;

    private String body;

    private String url;

    public PushNotification(Notification notification)
    {
        this.setTitle(notification.getTitle());
        this.setBody(notification.getBody());
        this.setUrl(notification.getUrl());
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
}
