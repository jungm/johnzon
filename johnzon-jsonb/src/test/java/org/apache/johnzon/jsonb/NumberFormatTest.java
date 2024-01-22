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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.johnzon.jsonb.model.packageformat.FormatFromPackageModel;
import org.apache.johnzon.jsonb.model.packageformat.FormatFromClassModel;
import org.apache.johnzon.jsonb.test.JsonbJunitExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.Test;

public class NumberFormatTest {
    @RegisterExtension
    public final JsonbJunitExtension jsonb = new JsonbJunitExtension();

    @Test
    public void packageFormat() {
        final String json = jsonb.toJson(new FormatFromPackageModel() {{ setInstance(123456.789); }});
        assertTrue(json.matches("\\{\\s*\"instance\"\\s*:\\s*\"123.456,8\"\\s*}"), json);

        final FormatFromPackageModel unmarshalledObject = jsonb.fromJson(
                "{ \"instance\" : \"123.456,789\" }", FormatFromPackageModel.class);
        assertEquals(123456.789, unmarshalledObject.getInstance(), 0);
    }

    @Test
    public void formatType() {
        final String json = jsonb.toJson(new FormatFromClassModel() {{ setInstance(123456.789); }});
        assertTrue(json.matches("\\{\\s*\"instance\"\\s*:\\s*\"123.456,8\"\\s*}"), json);

        final FormatFromPackageModel unmarshalledObject = jsonb.fromJson(
                "{ \"instance\" : \"123.456,789\" }", FormatFromClassModel.class);
        assertEquals(123456.789, unmarshalledObject.getInstance(), 0);
    }
}
