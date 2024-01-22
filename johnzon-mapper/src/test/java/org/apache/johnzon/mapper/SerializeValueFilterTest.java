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
package org.apache.johnzon.mapper;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializeValueFilterTest {
    @Test
    public void customIgnore() {
        // in this test we will serialize lists but not sets or other collection impls
        final Mapper mapper = new MapperBuilder().setSerializeValueFilter((name, value) -> !List.class.isInstance(value)).build();
        assertEquals("{\"list\":[\"test\"]}", mapper.writeObjectAsString(new Foo(singletonList("test"))));
        assertEquals("{}", mapper.writeObjectAsString(new Foo(singleton("test"))));
    }

    public static class Foo {
        public Collection<String> list;

        public Foo(final Collection<String> list) {
            this.list = list;
        }
    }
}
