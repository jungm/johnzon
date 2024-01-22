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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.util.Arrays.asList;

public class AdvancedGenericsTest {
    public static Iterable<String> modes() {
        return asList("field", "method", "both", "strict-method");
    }

    @ParameterizedTest
    @MethodSource("modes")
    public void testSerializeHierarchyOne(String accessMode) {
        Mapper mapper = new MapperBuilder().setAccessModeName(accessMode)
                                           .build();

        {
            String customerAsString = mapper.writeObjectAsString(new Customer("Bruce", "Wayne"));

            Assertions.assertTrue(customerAsString.contains("\"firstName\":\"Bruce\""), "Serialized String must contain \"firstName\":\"Bruce\"");
            Assertions.assertTrue(customerAsString.contains("\"lastName\":\"Wayne\""), "Serialized String must contain \"lastName\":\"Wayne\"");
            Assertions.assertFalse(customerAsString.contains("\"id\""), "Serialized String must not contain \"id\": " + customerAsString);
            Assertions.assertFalse(customerAsString.contains("\"version\""), "Serialized String must not contain \"version\"");
        }

        {
            String customerAsString = mapper.writeObjectAsString(new Customer(160784L, 35, "Clark", "Kent"));

            Assertions.assertTrue(customerAsString.contains("\"firstName\":\"Clark\""), "Serialized String must contain \"firstName\":\"Bruce\"");
            Assertions.assertTrue(customerAsString.contains("\"lastName\":\"Kent\""), "Serialized String must contain \"lastName\":\"Wayne\"");
            Assertions.assertTrue(customerAsString.contains("\"id\":160784"), "Serialized String must contain \"id\":160784");
            Assertions.assertTrue(customerAsString.contains("\"version\":35"), "Serialized String must contain \"version\":35");
        }
    }

    @ParameterizedTest
    @MethodSource("modes")
    public void testDeserializeHierarchyOne(String accessMode) {
        Mapper mapper = new MapperBuilder().setAccessModeName(accessMode)
                                           .build();

        {
            Customer customer = mapper.readObject("{ \"lastName\":\"Odinson\", \"firstName\":\"Thor\" }", Customer.class);

            Assertions.assertNotNull(customer);
            Assertions.assertNull(customer.getId());
            Assertions.assertNull(customer.getVersion());
            Assertions.assertEquals("Thor", customer.getFirstName());
            Assertions.assertEquals("Odinson", customer.getLastName());
        }

        {
            // id as JsonString
            Customer customer = mapper.readObject("{ \"firstName\":\"Loki\", \"lastName\":\"Laufeyson\", \"id\":\"160883\" }", Customer.class);

            Assertions.assertNotNull(customer);
            Assertions.assertEquals(160883L, customer.getId().longValue());
            Assertions.assertNull(customer.getVersion());
            Assertions.assertEquals("Loki", customer.getFirstName());
            Assertions.assertEquals("Laufeyson", customer.getLastName());
        }

        {
            // id as JsonNumber, version as JsonString
            Customer customer = mapper.readObject("{ \"lastName\":\"Banner\", \"firstName\":\"Bruce\", \"id\":7579, \"version\":\"74\" }", Customer.class);

            Assertions.assertNotNull(customer);
            Assertions.assertEquals(7579L,  customer.getId().longValue());
            Assertions.assertEquals(74, customer.getVersion().intValue());
            Assertions.assertEquals("Bruce", customer.firstName);
            Assertions.assertEquals("Banner", customer.lastName);

        }
    }

    @ParameterizedTest
    @MethodSource("modes")
    public void testSerializeHierarchyTwo(String accessMode) {
        Mapper mapper = new MapperBuilder().setAccessModeName(accessMode)
                                           .build();

        String vipCustomerAsString = mapper.writeObjectAsString(new VIPCustomer(new Customer(15L, 37, "Lois", "Lane"), 12.5));

        Assertions.assertNotNull(vipCustomerAsString);
        Assertions.assertTrue(vipCustomerAsString.contains("\"firstName\":\"Lois\""), "Serialized String must contain \"firstName\":\"Lois\"");
        Assertions.assertTrue(vipCustomerAsString.contains("\"lastName\":\"Lane\""), "Serialized String must contain \"lastName\":\"Lane\"");
        Assertions.assertTrue(vipCustomerAsString.contains("\"id\":15"), "Serialized String must contain \"id\":15");
        Assertions.assertTrue(vipCustomerAsString.contains("\"version\":37"), "Serialized String must contain \"version\":37");
        Assertions.assertTrue(vipCustomerAsString.contains("\"discount\":12.5"), "Serialized String must contain \"discount\":12.5");
    }

    @ParameterizedTest
    @MethodSource("modes")
    public void testDeserializeHierarchyTwo(String accessMode) {
        Mapper mapper = new MapperBuilder().setAccessModeName(accessMode)
                                           .build();

        {
            // id as JsonString
            VIPCustomer customer = mapper.readObject("{ \"discount\":5, \"id\":\"888\", \"lastName\":\"Ross\", \"firstName\":\"Betty\", \"version\":\"5555\" }", VIPCustomer.class);

            Assertions.assertNotNull(customer);
            Assertions.assertEquals(888L, customer.getId().longValue());
            Assertions.assertEquals(5555, customer.getVersion().intValue());
            Assertions.assertEquals("Betty", customer.getFirstName());
            Assertions.assertEquals("Ross", customer.getLastName());
            Assertions.assertEquals(5.0, customer.getDiscount(), 0);
        }

        {
            // id as JsonNumber
            VIPCustomer customer = mapper.readObject("{ \"discount\":25.5, \"id\":478965, \"firstName\":\"Selina\", \"version\":\"3\", \"lastName\":\"Kyle\" }",
                                                     VIPCustomer.class);

            Assertions.assertNotNull(customer);
            Assertions.assertEquals(478965L, customer.getId().longValue());
            Assertions.assertEquals(3, customer.getVersion().intValue());
            Assertions.assertEquals("Selina", customer.getFirstName());
            Assertions.assertEquals("Kyle", customer.getLastName());
            Assertions.assertEquals(25.5, customer.getDiscount(), 0);
        }
    }

    @ParameterizedTest
    @MethodSource("modes")
    public void testSerializeHierarchyThree(String accessMode) {
        Mapper mapper = new MapperBuilder().setAccessModeName(accessMode)
                                           .build();

        String vipCustomerAsString = mapper.writeObjectAsString(new GoldCustomer(new VIPCustomer(new Customer(6547L, 497, "Peter", "Parker"), 4.2), 1));

        Assertions.assertNotNull(vipCustomerAsString);
        Assertions.assertTrue(vipCustomerAsString.contains("\"firstName\":\"Peter\""), "Serialized String must contain \"firstName\":\"Lois\"");
        Assertions.assertTrue(vipCustomerAsString.contains("\"lastName\":\"Parker\""), "Serialized String must contain \"lastName\":\"Lane\"");
        Assertions.assertTrue(vipCustomerAsString.contains("\"id\":6547"), "Serialized String must contain \"id\":6547");
        Assertions.assertTrue(vipCustomerAsString.contains("\"version\":497"), "Serialized String must contain \"version\":497");
        Assertions.assertTrue(vipCustomerAsString.contains("\"discount\":4.2"), "Serialized String must contain \"discount\":4.2");
        Assertions.assertTrue(vipCustomerAsString.contains("\"rating\":1"), "Serialized String must contain \"rating\":1");
    }

    @ParameterizedTest
    @MethodSource("modes")
    public void testDeserializeHierarchyThree(String accessMode) {
        Mapper mapper = new MapperBuilder().setAccessModeName(accessMode)
                                           .build();

        // id as JsonString, without rating
        GoldCustomer customer = mapper.readObject("{ \"discount\":5, \"id\":\"8745321\", \"lastName\":\"Watson\", \"firstName\":\"Mary Jane\", \"version\":\"821\" }",
                                                  GoldCustomer.class);

        Assertions.assertNotNull(customer);
        Assertions.assertEquals(8745321L, customer.getId().longValue());
        Assertions.assertEquals(821, customer.getVersion().intValue());
        Assertions.assertEquals("Mary Jane", customer.getFirstName());
        Assertions.assertEquals("Watson", customer.getLastName());
        Assertions.assertEquals(5.0, customer.getDiscount(), 0);
        Assertions.assertEquals(0, customer.getRating());
    }


    private static abstract class Versioned<T> {

        private T id;
        private Integer version;

        public Versioned() {
        }

        public Versioned(T id, Integer version) {
            this.id = id;
            this.version = version;
        }

        public T getId() {
            return id;
        }

        public void setId(T id) {
            this.id = id;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }
    }

    private static class Customer extends Versioned<Long> {

        private String firstName;
        private String lastName;

        public Customer() {
            this(null, null);
        }

        private Customer(String firstName, String lastName) {
            this(null, null, firstName, lastName);
        }

        private Customer(Long id, Integer version, String firstName, String lastName) {
            super(id, version);
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

    private static class VIPCustomer extends Customer {

        private Double discount;

        private VIPCustomer() {

        }

        public VIPCustomer(String firstName, String lastName, Double discount) {
            super(null, null, firstName, lastName);
            this.discount = discount;
        }

        public VIPCustomer(Customer customer, Double discount) {
            super(customer.getId(), customer.getVersion(), customer.getFirstName(), customer.getLastName());
            this.discount = discount;
        }

        public Double getDiscount() {
            return discount;
        }

        public void setDiscount(Double discount) {
            this.discount = discount;
        }
    }

    private static class GoldCustomer extends VIPCustomer {
        private int rating;

        private GoldCustomer() {
        }

        private GoldCustomer(VIPCustomer customer, int rating) {
            super(customer, customer.getDiscount());
            this.rating = rating;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
    }

}
