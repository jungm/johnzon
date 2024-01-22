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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.config.BinaryDataStrategy;
import org.apache.johnzon.jsonb.test.JsonbJunitExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonbReadTest {
    @RegisterExtension
    private JsonbJunitExtension jsonb = new JsonbJunitExtension()
            .configure(jsonbConfig -> jsonbConfig.withBinaryDataStrategy(BinaryDataStrategy.BASE_64));
    
    @Test
    public void simpleArrayMapping() throws Exception {
        final List<String> expectedResult = asList("Test String");
        try (final Jsonb jsonb = JsonbBuilder.create()) {
            final Object unmarshalledObject = jsonb.fromJson("[ \"Test String\" ]", (Type) List.class);
            assertEquals(expectedResult, unmarshalledObject);
        }
    }

    @Test
    public void simpleArrayMappingReader() throws Exception {
        final List<String> expectedResult = asList("Test String");
        try (final Jsonb jsonb = JsonbBuilder.create()) {
            final Object unmarshalledObject = jsonb.fromJson(new StringReader("[ \"Test String\" ]"), (Type) List.class);
            assertEquals(expectedResult, unmarshalledObject);
        }
    }

    @Test
    public void simpleArrayMappingInputStream() throws Exception {
        final List<String> expectedResult = asList("Test String");
        try (final Jsonb jsonb = JsonbBuilder.create()) {
            final Object unmarshalledObject = jsonb.fromJson(new ByteArrayInputStream("[ \"Test String\" ]".getBytes(
                StandardCharsets.UTF_8)), (Type) List.class);
            assertEquals(expectedResult, unmarshalledObject);
        }
    }

    @Test
    public void boolFromString() {
        assertTrue(jsonb.fromJson("true", Boolean.class));
    }

    @Test
    public void boolFromReader() {
        assertTrue(jsonb.fromJson(new StringReader("true"), Boolean.class));
    }

    @Test
    public void boolFromStream() {
        assertTrue(jsonb.fromJson(new ByteArrayInputStream("true".getBytes()), Boolean.class));
    }

    @Test
    public void simple() {
        assertEquals("test", jsonb.fromJson(new StringReader("{\"value\":\"test\"}"), Simple.class).value);
    }

    @Test
    public void propertyMapping() {
        assertEquals("test", jsonb.fromJson(new StringReader("{\"simple\":\"test\"}"), SimpleProperty.class).value);
    }

    @Test
    public void date() { // ok, can fail at midnight, acceptable risk
        final String date = DateTimeFormatter.ofPattern("d. LLLL yyyy").format(LocalDate.now());
        assertEquals(
            LocalDate.now().getYear(),
            jsonb.fromJson(new StringReader("{\"date\":\"" + date + "\"}"), DateFormatting.class).date.getYear());
    }

    @Test
    public void propertyMappingNewLine() {
        String json = "{\n" +
                "  \"simple\":\"test\"\n" +
                "}\n";

        assertEquals("test", jsonb.fromJson(new StringReader(json), SimpleProperty.class).value);
    }

    @Test
    public void propertyMappingNewLineCr() {
        String json = "{\r\n" +
                "  \"simple\":\"test\"\r\n" +
                "}\r\n";

        assertEquals("test", jsonb.fromJson(new StringReader(json), SimpleProperty.class).value);
    }

    @Test
    public void propertyMappingNewLineTabs() {
        String json = "{\n" +
                "\t\"simple\":\"test\"\n" +
                "}\n";

        assertEquals("test", jsonb.fromJson(new StringReader(json), SimpleProperty.class).value);
    }

    @Test
    public void testValidBase64() {
        String json = "{\"blob\":\"" + Base64.getEncoder().encodeToString("test".getBytes(StandardCharsets.UTF_8)) + "\"}";

        SimpleBinaryDto simpleBinaryDto = jsonb.fromJson(new StringReader(json), SimpleBinaryDto.class);
        assertEquals("test", new String(simpleBinaryDto.getBlob(), StandardCharsets.UTF_8));
    }

    @Test
    public void testInvalidBase64() {
        String jsonWithIllegalBase64 = "{\"blob\":\"dGVXz@dA==\"}";
        
        assertThrows(JsonbException.class, () -> jsonb.fromJson(jsonWithIllegalBase64, SimpleBinaryDto.class));
    }


    public static class Simple {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }

    public static class SimpleProperty {
        @JsonbProperty("simple")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }

    public static class SimplePropertyNillable {
        @JsonbProperty(nillable = true)
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }
    }

    public static class DateFormatting {
        @JsonbDateFormat(value = "d. LLLL yyyy")
        @JsonbProperty(nillable = true)
        private LocalDate date;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(final LocalDate value) {
            this.date = value;
        }
    }

    public static class SimpleBinaryDto {
        private byte[] blob;

        public byte[] getBlob() {
            return blob;
        }

        public void setBlob(byte[] blob) {
            this.blob = blob;
        }
    }
}
