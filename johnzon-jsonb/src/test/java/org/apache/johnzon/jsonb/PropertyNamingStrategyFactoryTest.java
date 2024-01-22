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

import jakarta.json.bind.config.PropertyNamingStrategy;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertyNamingStrategyFactoryTest {
    public static Stream<Arguments> points() {
        return Stream.of(
                Arguments.of(PropertyNamingStrategy.IDENTITY, "a", "a"),
                Arguments.of(PropertyNamingStrategy.IDENTITY, "aBEOCBDJ4397dkabqWLCd", "aBEOCBDJ4397dkabqWLCd"),
                Arguments.of(PropertyNamingStrategy.CASE_INSENSITIVE, "aBEOCBDJ4397dkabqWLCd", "aBEOCBDJ4397dkabqWLCd"), // not really testable there
                Arguments.of(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES, "lower-dash", "lower-dash"),
                Arguments.of(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES, "lower_dash", "lower_dash"),
                Arguments.of(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES, "lowerDash", "lower-dash"),
                Arguments.of(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES, "lower_under", "lower_under"),
                Arguments.of(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES, "lowerUnder", "lower_under"),
                Arguments.of(PropertyNamingStrategy.UPPER_CAMEL_CASE, "fooBar", "FooBar"),
                Arguments.of(PropertyNamingStrategy.UPPER_CAMEL_CASE_WITH_SPACES, "fooBar", "Foo Bar"));
    }

    @ParameterizedTest
    @MethodSource("points")
    public void valid(String strategy, String input, String expected) {
        assertEquals(expected, new PropertyNamingStrategyFactory(strategy).create().translateName(input));
    }
}
