package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private Integer rentTime;
    private String deliveryDate;
    private String comment;
    private String[] color;

    public Order() {}

    public Order(String firstName, String lastName, String address, String metroStation, String phone, Integer rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getAddress() {
        return address;
    }
    public String getMetroStation() {
        return metroStation;
    }
    public String getPhone() {
        return phone;
    }
    public Integer getRentTime() {
        return rentTime;
    }
    public String getDeliveryDate() {
        return deliveryDate;
    }
    public String getComment() {
        return comment;
    }
    public String[] getColor() {
        return color;
    }

    public static Order getOrder(String[] color) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tomorrow = formatter.format(java.time.LocalDateTime.now().plusDays(1));

        return new Order(
                RandomStringGenerator.generateRussianFirstName(),
                RandomStringGenerator.generateRussianLastName(),
                RandomStringGenerator.generateRussianAddress(),
                RandomStringGenerator.generateMetroStation(),
                RandomStringGenerator.generatePhone(),
                1,
                tomorrow,
                RandomStringGenerator.generateComment(),
                color
        );
    }
}
