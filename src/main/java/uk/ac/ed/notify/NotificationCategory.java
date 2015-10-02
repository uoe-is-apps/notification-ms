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

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container class that holds the entries for a given category.
 * The category title is used to determine if entries from other
 * sources should be grouped together in the same category.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationCategory implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	private String title;
	private List<NotificationEntry> entries;

	/**
	 * Constructor.
	 */
	public NotificationCategory() {
	    entries = new ArrayList<NotificationEntry>();
	}

	/**
	 * Constructor.
	 */
	public NotificationCategory(String title, List<NotificationEntry> entries) {
		this.title = title;
		this.entries = new ArrayList<NotificationEntry>(entries);  // defensive copy
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<NotificationEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	public void setEntries(List<NotificationEntry> entries) {
		this.entries = new ArrayList<NotificationEntry>(entries);
	}	                            

	public void addEntries(List<NotificationEntry> newEntries) {
	    this.entries.addAll(newEntries);
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
        final NotificationCategory rslt = (NotificationCategory) super.clone();

        // Adjust to satisfy deep-copy strategy
        List<NotificationEntry> eList = new ArrayList<NotificationEntry>(entries.size());
        for (NotificationEntry entry : entries) {
            eList.add((NotificationEntry) entry.clone());
        }
        rslt.setEntries(eList);

        return rslt;

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NotificationCategory [title=");
        builder.append(title);
        builder.append(", entries=");
        builder.append(entries);
        builder.append("]");
        return builder.toString();
    }

}
