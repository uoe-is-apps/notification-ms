package uk.ac.ed.notify.entity;

/**
 * Created by rgood on 25/11/2016.
 */
public class SubscriptionMessage {

    private String uun;

    private String endPoint;

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getUun() {
        return uun;
    }

    public void setUun(String uun) {
        this.uun = uun;
    }
}
