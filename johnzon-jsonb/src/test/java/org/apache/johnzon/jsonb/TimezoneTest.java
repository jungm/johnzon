/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.johnzon.jsonb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.TimeZone;

import org.apache.johnzon.jsonb.test.JsonbJunitExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;

public class TimezoneTest {
    @RegisterExtension
    public final JsonbJunitExtension jsonb = new JsonbJunitExtension();

    @Test
    public void exceptionOnDeprecated() {
        final TimeZoneHolder holder = new TimeZoneHolder();
        holder.instance = TimeZone.getTimeZone("CST");
        jsonb.toJson(holder);
    }

    @Test
    public void valid() {
        final TimeZoneHolder holder = new TimeZoneHolder();
        holder.instance = TimeZone.getTimeZone("UTC");
        assertEquals("{\"instance\":\"UTC\"}", jsonb.toJson(holder));
    }

    public static class TimeZoneHolder {
        public TimeZone instance;
    }
}
