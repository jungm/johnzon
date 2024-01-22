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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.johnzon.jsonb.model.Holder;
import org.apache.johnzon.jsonb.test.JsonbJunitExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;

public class TransientTest {
    @RegisterExtension
    public final JsonbJunitExtension jsonb = new JsonbJunitExtension();

    @Test
    public void transientFieldIsIgnored() {
        final String json = jsonb.toJson(new TransientHolder() {{ setInstance("String Value"); }});
        assertEquals("{}", json);

        final TransientHolder value = jsonb.fromJson("{ \"instance\" : \"whatever\" }", TransientHolder.class);
        assertNull(value.getInstance());
    }

    public static class TransientHolder implements Holder<String> {
        private transient String instance;

        @Override
        public String getInstance() {
            return instance;
        }

        @Override
        public void setInstance(String instance) {
            this.instance = instance;
        }
    }
}
