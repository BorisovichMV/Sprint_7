import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.example.Courier;
import org.example.CourierLoginModel;
import org.example.RandomStringGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;

public class LoginCourierTest {

    private Courier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";

        String firstName = RandomStringGenerator.generateFirstName();
        String login = RandomStringGenerator.generateLogin();
        String password = RandomStringGenerator.generatePassword();

        courier = new Courier(login, password, firstName);
        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/courier");
    }

    @Test
    @DisplayName("Курьер может авторизоваться, успешный запрос возвращает id")
    public void testLogin() {
        CourierLoginModel loginModel = new CourierLoginModel(courier.getLogin(), courier.getPassword());

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(loginModel)
                .when()
                .post("/courier/login")
                .then()
                .statusCode(200)
                .body("id", instanceOf(Integer.class));
    }

    @Test
    @DisplayName("Для авторизации нужно передать все обязательные поля, если какого-то поля нет, запрос возвращает ошибку")
    public void testLoginWithEmptyRequiredFields() {
        CourierLoginModel invalidPasswordLoginModel = new CourierLoginModel(courier.getLogin(), "");
        CourierLoginModel invalidLoginLoginModel = new CourierLoginModel("", courier.getPassword());
        CourierLoginModel invalidBothLoginModel = new CourierLoginModel("", "");

        Object[][] responses = {
                {invalidPasswordLoginModel, 400, "Недостаточно данных для входа"},
                {invalidLoginLoginModel, 400, "Недостаточно данных для входа"},
                {invalidBothLoginModel, 400, "Недостаточно данных для входа"}
        };

        for (Object[] item : responses) {
            given()
                    .header("Content-Type", "application/json")
                    .and()
                    .body(item[0])
                    .when()
                    .post("/courier/login")
                    .then()
                    .statusCode((Integer) item[1])
                    .body("message", equalTo(item[2]));
        }
    }

    @Test
    @DisplayName("Система вернёт ошибку, если неправильно указать логин (несуществующий пользователь) или пароль")
    public void testLoginWithWrongCredentials() {
        CourierLoginModel wrongPasswordLoginModel = new CourierLoginModel(courier.getLogin(), "wrongPassword");
        CourierLoginModel wrongLoginLoginModel = new CourierLoginModel("wrongLogin", courier.getPassword());

        Object[][] responses = {
                {wrongPasswordLoginModel, 404, "Учетная запись не найдена"},
                {wrongLoginLoginModel, 404, "Учетная запись не найдена"}
        };
        for (Object[] item : responses) {
            given()
                    .header("Content-Type", "application/json")
                    .and()
                    .body(item[0])
                    .when()
                    .post("/courier/login")
                    .then()
                    .statusCode((Integer) item[1])
                    .body("message", equalTo(item[2]));
        }
    }
}
