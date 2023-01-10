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

import org.apache.johnzon.jsonb.test.JsonbRule;
import org.junit.Rule;
import org.junit.Test;

import jakarta.json.bind.annotation.JsonbProperty;

import static org.junit.Assert.assertEquals;

public class EnumConverterTest {
    @Rule
    public final JsonbRule jsonb = new JsonbRule();

    @Test
    public void fromEnumToString() {
        assertEquals(AnEnum.B, jsonb.fromJson("{\"value\":\"-2\"}", Wrapper.class).value);
    }

    @Test
    public void fromStringToEnum() {
        final Wrapper wrapper = new Wrapper();
        wrapper.value = AnEnum.A;
        assertEquals("{\"value\":\"-1\"}", jsonb.toJson(wrapper));
    }

    public static class Wrapper {
        public AnEnum value;
    }

    public enum AnEnum {
        @JsonbProperty("-1")
        A,

        @JsonbProperty("-2")
        B
    }
}
