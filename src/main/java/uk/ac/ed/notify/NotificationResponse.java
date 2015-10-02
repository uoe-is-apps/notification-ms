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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This class contains all the categories and errors
 * retrieved by an INotificationService. It is also
 * used to aggregate all the NotificationResponses from
 * various services into a single NotificationResponse.
 * The data from the overall NotificationResponse instance
 * is returned to the portlet to be rendered.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_EMPTY)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationResponse implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    //private final Logger log = LoggerFactory.getLogger(getClass());

    private List<NotificationCategory> categories;
    private List<NotificationError> errors;

    /**
     * Field indicating the response is a cloned defensive copy and can be
     * modified (specifically,you can replace the collections with new
     * collections)
     */
    @JsonIgnore
    @XmlTransient
    private boolean cloned = false;

    public NotificationResponse() {
        categories = new ArrayList<NotificationCategory>();
        errors = new ArrayList<NotificationError>();
    }

    public NotificationResponse(NotificationResponse response) {
        this(response.getCategories(), response.getErrors());
    }

    public NotificationResponse(List<NotificationCategory> categories, List<NotificationError> errors) {
        this.categories = new ArrayList<NotificationCategory>(categories);  // defensive copy
        this.errors = new ArrayList<NotificationError>(errors);  // defensive copy
    }

    public List<NotificationCategory> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void setCategories(List<NotificationCategory> categories) {
        this.categories = new ArrayList<NotificationCategory>(categories);  // defensive copy
    }

    public List<NotificationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void setErrors(List<NotificationError> errors) {
        this.errors = new ArrayList<NotificationError>(errors); // defensive copy
    }

    /**
     * Returns the {@link NotificationEntry} with the specified id, or null if
     * not present.
     */
    public NotificationEntry findNotificationEntryById(final String notificationId) {

        // Assertions
        if (notificationId == null) {
            String msg = "Argument 'notificationId' cannot be null";
            throw new IllegalArgumentException(msg);
        }

        // Providing a brute-force implementation for
        // now;  we can improve it if it becomes important.
        NotificationEntry rslt = null;  // default -- means not present
        for (NotificationCategory category : categories) {
            for (NotificationEntry entry : category.getEntries()) {
                if (notificationId.equals(entry.getId())) {
                    rslt = entry;
                    break;
                }
            }
            if (rslt != null) {
                break;
            }
        }

        return rslt;

    }

    /**
     * Combine the contents of this response with the provided response and
     * return a <b>new instance</b> of {@link NotificationResponse}.  The
     * original instances are unchanged.
     *
     * @param response A new {@link NotificationResponse} that contains data from both originals
     */
    public NotificationResponse combine(NotificationResponse response) {
        NotificationResponse rslt = new NotificationResponse(this);
        rslt.addCategories(response.getCategories());
        rslt.addErrors(response.getErrors());
        return rslt;
    }

    /**
     * Return a <b>new instance</b> of {@link NotificationResponse} from which
     * the specified errors have been removed.  The original instances are
     * unchanged.
     *
     * @param hiddenErrorKeys set of error keys to exclude from result
     */
    public NotificationResponse filterErrors(Set<Integer> hiddenErrorKeys) {
        NotificationResponse rslt = new NotificationResponse(this);
        List<NotificationError> filteredErrors = new ArrayList<NotificationError>();
        for (NotificationError r : errors) {
            if (!hiddenErrorKeys.contains(r.getKey())) {
                filteredErrors.add(r);
            }
        }
        rslt.setErrors(filteredErrors);
        return rslt;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NotificationResponse [categories=");
        builder.append(categories);
        builder.append(", errors=");
        builder.append(errors);
        builder.append("]");
        return builder.toString();
    }

    /**
     * Implements deep-copy clone.
     *
     * @throws CloneNotSupportedException Not really, but it's on the method
     * signature we're overriding.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {

        // Start with superclass impl (handles immutables and primitives)
        final NotificationResponse rslt = (NotificationResponse) super.clone();

        // Adjust to satisfy deep-copy strategy
        List<NotificationCategory> cList = new ArrayList<NotificationCategory>(categories.size());
        for (NotificationCategory category : categories) {
            cList.add((NotificationCategory) category.clone());
        }
        rslt.setCategories(cList);
        List<NotificationError> eList = new ArrayList<NotificationError>(errors.size());
        for (NotificationError error : errors) {
            eList.add((NotificationError) error.clone());
        }
        rslt.setErrors(eList);
        rslt.setCloned(true);

        return rslt;

    }

    /*
     * Implementation
     */

    /**
     * Insert the given categories and their entries into the any existing
     * categories of the same title. If a category doesn't match an existing
     * one, add it to the list.
     * @param newCategories collection of new categories and their entries.
     */
    private void addCategories(List<NotificationCategory> newCategories) {

        //check if an existing category (by the same title) already exists
        //if so, add the new categories entries to the existing category
        for(NotificationCategory newCategory : newCategories) {
            boolean found = false;

            for(NotificationCategory myCategory : categories) {
                if(myCategory.getTitle().toLowerCase().equals(newCategory.getTitle().toLowerCase())){
                    found = true;
                    myCategory.addEntries(newCategory.getEntries());
                }
            }

            if(!found) {
                categories.add(newCategory);
            }
        }
    }

    private void addErrors(List<NotificationError> newErrors) {
        for(NotificationError error : newErrors)
            errors.add(error);
    }

    public NotificationResponse cloneIfNotCloned() {
        try {
            return isCloned() ? this : (NotificationResponse) this.clone();
        } catch (CloneNotSupportedException e) {
            //log.error("Failed to clone() the sourceResponse", e);
        }
        return this;
    }

    public boolean isCloned() {
        return cloned;
    }

    private void setCloned(boolean cloned) {
        this.cloned = cloned;
    }
}
