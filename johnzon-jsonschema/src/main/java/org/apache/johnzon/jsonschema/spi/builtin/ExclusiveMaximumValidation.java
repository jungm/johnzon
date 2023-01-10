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
package org.apache.johnzon.jsonschema.spi.builtin;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import jakarta.json.JsonNumber;
import jakarta.json.JsonValue;

import org.apache.johnzon.jsonschema.ValidationResult;
import org.apache.johnzon.jsonschema.spi.ValidationContext;
import org.apache.johnzon.jsonschema.spi.ValidationExtension;

public class ExclusiveMaximumValidation implements ValidationExtension {
    @Override
    public Optional<Function<JsonValue, Stream<ValidationResult.ValidationError>>> create(final ValidationContext model) {
        if (model.getSchema().getString("type", "object").equals("number")) {
            return Optional.ofNullable(model.getSchema().get("exclusiveMaximum"))
                    .filter(v -> v.getValueType() == JsonValue.ValueType.NUMBER)
                    .map(m -> new Impl(model.toPointer(), model.getValueProvider(), JsonNumber.class.cast(m).doubleValue()));
        }
        return Optional.empty();
    }

    private static class Impl extends BaseNumberValidation {
        private Impl(final String pointer, final Function<JsonValue, JsonValue> valueProvider, final double bound) {
            super(pointer, valueProvider, bound);
        }

        @Override
        protected boolean isValid(final double val) {
            return val < this.bound;
        }

        @Override
        protected Stream<ValidationResult.ValidationError> toError(final double val) {
            return Stream.of(new ValidationResult.ValidationError(pointer, val + " is strictly more than " + this.bound));
        }

        @Override
        public String toString() {
            return "ExclusiveMaximum{" +
                    "factor=" + bound +
                    ", pointer='" + pointer + '\'' +
                    '}';
        }
    }
}
