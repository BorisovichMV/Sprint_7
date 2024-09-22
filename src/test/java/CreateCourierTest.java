import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.example.Courier;
import org.example.RandomStringGenerator;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest {

    String firstName;
    String login;
    String password;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
        firstName = RandomStringGenerator.generateFirstName();
        login = RandomStringGenerator.generateLogin();
        password = RandomStringGenerator.generatePassword();
    }

    @Test
    @DisplayName("Курьера можно создать, успешный запрос возвращает ok: true")
    public void testCourierCanToCreate() {
        Courier courier = new Courier(login, password, firstName);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void testCreationSameCouriersNotAllowed() {
        Courier courier = new Courier(login, password, firstName);

        Integer[] statusCodes = {201, 409};
        for (Integer statusCode : statusCodes) {
        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/courier")
                .then()
                .statusCode(statusCode);
        }
    }

    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    public void testRequiredFields() {
        Courier validCourier = new Courier(login, password, null);
        Courier invalidPasswordCourier = new Courier(login, null, firstName);
        Courier invalidLoginCourier = new Courier(null, password, firstName);

        Object[][] couriers = {
                {validCourier, 201},
                {invalidPasswordCourier, 400},
                {invalidLoginCourier, 400}
        };

        for (Object[] item : couriers) {
            given()
                    .header("Content-Type", "application/json")
                    .and()
                    .body(item[0])
                    .when()
                    .post("/courier")
                    .then()
                    .statusCode((Integer) item[1]);
        }
    }

    @Test
    @DisplayName("Запросы возвращают правильный код ответа")
    public void testCreationReturnsRightCode() {
        Courier validCourier = new Courier(login, password, null);
        Courier invalidPasswordCourier = new Courier(login, null, firstName);
        Courier invalidLoginCourier = new Courier(null, password, firstName);

        Object[][] couriers = {
                {invalidLoginCourier, 400},
                {invalidPasswordCourier, 400},
                {validCourier, 201},
                {validCourier, 409}
        };

        for (Object[] item : couriers) {
            given()
                    .header("Content-Type", "application/json")
                    .and()
                    .body(item[0])
                    .when()
                    .post("/courier")
                    .then()
                    .statusCode((Integer) item[1]);
        }
    }

    @Test
    @DisplayName("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void testCreationReturnsRightMessageWithExistingFields() {
        String anotherFirstName = RandomStringGenerator.generateFirstName();
        String anotherPassword = RandomStringGenerator.generatePassword();

        Courier courier = new Courier(login, password, firstName);
        Courier anotherCourier = new Courier(login, anotherPassword, anotherFirstName);

        Object[][] couriers = {
                {courier, 201},
                {anotherCourier, 409}
        };

        for (Object[] item : couriers) {
            given()
                    .header("Content-Type", "application/json")
                    .and()
                    .body(item[0])
                    .when()
                    .post("/courier")
                    .then()
                    .statusCode((Integer) item[1]);
        }
    }
}
