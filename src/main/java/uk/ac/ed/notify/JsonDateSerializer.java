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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

/**
 * Apparently there is no standard format for dates in JSON.  JsonLib (which 
 * this portlet previously used) essentially serialized an instance of 
 * <code>java.util.Date</code> like any other object.  Jackson, however, (the
 * current method) converts the date to "epoch time" (a long representing 
 * milliseconds since January 1, 1970, 00:00:00 GMT).  The Jackson approach 
 * isn't that useful, since it becomes hard to detect what is a date.  This 
 * class is responsible for telling Jackson to use the former method.
 * 
 * @author awills
 */
public class JsonDateSerializer extends JsonSerializer<Date> {


    @SuppressWarnings("deprecation")
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        
        gen.writeStartObject();
        gen.writeNumberField("date", date.getDate());
        gen.writeNumberField("day", date.getDay());
        gen.writeNumberField("hours", date.getHours());
        gen.writeNumberField("minutes", date.getMinutes());
        gen.writeNumberField("month", date.getMonth());
        gen.writeNumberField("seconds", date.getSeconds());
        gen.writeNumberField("time", date.getTime());
        gen.writeNumberField("timezoneOffset", date.getTimezoneOffset());
        gen.writeNumberField("year", date.getYear());
        gen.writeEndObject();
        
    }

}
