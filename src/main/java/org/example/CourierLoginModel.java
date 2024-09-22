package org.example;

public class CourierLoginModel {
    private final String login;
    private final String password;

    public CourierLoginModel(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public static CourierLoginModel fromCourier(Courier courier) {
        return new CourierLoginModel(courier.getLogin(), courier.getPassword());
    }
}
