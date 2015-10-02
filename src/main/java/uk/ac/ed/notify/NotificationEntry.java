/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package uk.ac.ed.notify;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang.builder.ToStringBuilder;
import uk.ac.ed.notify.entity.DatePartSerializer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.*;

/**
 * This class represents a single notification.  It defines a handful of 
 * strongly-typed members, plus a few open-ended collections.  These collections
 * are for <em>attributes</em>, <em>actions</em>, and <em>states</em>.
 * Strongly-typed items are chosen primarily because they require
 * framework-level special handling.  The open-ended collections make the
 * platform <em>extensible</em>.  Please add additional members only as a last
 * resort.
 * 
 * <p>The {@link NotificationCategory}
 * class contains all the entries for the same category title.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationEntry implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    /*
     * Strongly-typed members
     */

    private String    source;
    private String    id;
    private String    title;
    private String    url;
    private String    linkText;
    private int       priority;

    @JsonSerialize(using=JsonDateSerializer.class)
    private Date      dueDate;

    private String    image;
    private String    body;

    /*
     * Weakly-typed, open-ended attributes collection
     */
    private List<NotificationAttribute> attributes = Collections.emptyList();

    /**
     * Provides the human-readable name for the source of this notification.
     * E.g. 'Office of the Registrar'
     */
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this notification.  Ids are not required 
     * <strong>except for notifications that support actions</strong>.  The 
     * value of the id field can be any javascript-safe String, but must be 
     * unique within a user's collection of notifications.
     * <code>UUID.randomUUID()</code> could be a good way to generate them.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Required one-liner version of this notification.  E.g. 'You have 3
     * overdue library books.'
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Optional web address where the user can deal with or learn more about the
     * details of this notification.
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Optional text of the anchor tag linking to <code>url</code>.
     */
    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    /**
     * Optional numeric representation of the relative importance of the
     * notification.  Vales range from 1 (most important) to 5 (least important).
     */
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Optional date/time indicating by when the action described in this
     * notification must be completed.
     */
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Optional URL of an image file associated with this notification.  Might
     * be the logo of the source system or something more specific.
     */
    public String getImage() {
        return image;
    }

    public void setImage(String imageUrl) {
        this.image = imageUrl;
    }

    /**
     * Optional body text for this notification.  Supports HTML tags.
     */
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Open-ended collection of notification metadata.  The presumption is that
     * attributes will be visible to users.
     */
    public List<NotificationAttribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public void setAttributes(List<NotificationAttribute> attributes) {
        this.attributes = new ArrayList<NotificationAttribute>(attributes);  // defensive copy
    }



    /**
     * Implements deep-copy clone.
     * 
     * @throws CloneNotSupportedException Not really, but it's on the method 
     * signature we're overriding.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {

        // Start with superclass impl (handles immutables and primitives)
        final NotificationEntry rslt = (NotificationEntry) super.clone();

        // Adjust to satisfy deep-copy strategy
        List<NotificationAttribute> atrList = new ArrayList<NotificationAttribute>(attributes.size());
        for (NotificationAttribute attr : attributes) {
            atrList.add((NotificationAttribute) attr.clone());
        }
        rslt.setAttributes(atrList);

        return rslt;

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
