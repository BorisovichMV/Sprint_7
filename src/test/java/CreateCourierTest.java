import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Courier;
import org.example.CourierLoginModel;
import org.example.RandomStringGenerator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest {

    String firstName;
    String login;
    String password;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Before
    public void setup() {
        firstName = RandomStringGenerator.generateFirstName();
        login = RandomStringGenerator.generateLogin();
        password = RandomStringGenerator.generatePassword();
    }

    @Test
    @DisplayName("Курьера можно создать, успешный запрос возвращает ok: true")
    public void testCourierCanToCreate() {
        Courier courier = new Courier(login, password, firstName);

        Response response = createCourier(courier);
        checkStatusCode(response, 201);
        checkBodyBoolean(response, "ok", true);
        deleteCourier(courier);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void testCreationSameCouriersNotAllowed() {
        Courier courier = new Courier(login, password, firstName);

        Integer[] statusCodes = {201, 409};
        for (Integer statusCode : statusCodes) {
            Response response = createCourier(courier);
            checkStatusCode(response, statusCode);
        }
        deleteCourier(courier);
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
            Response response = createCourier((Courier) item[0]);
            checkStatusCode(response, (Integer) item[1]);
        }

        deleteCourier(validCourier);
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
            Response response = createCourier((Courier) item[0]);
            checkStatusCode(response, (Integer) item[1]);
        }

        deleteCourier(validCourier);
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
            Response response = createCourier((Courier) item[0]);
            checkStatusCode(response, (Integer) item[1]);
        }

        deleteCourier(courier);
    }

    @Step("Удаляем созданного курьера")
    private static void deleteCourier(Courier courier) {
        Response response = loginCourier(courier);
        if (response.statusCode() == 200) {
            Integer courierId = getCourierId(response);
            Response deleteResponse = sendDeleteRequest(courierId);
            checkStatusCode(deleteResponse, 200);
            checkBodyBoolean(deleteResponse, "ok", true);
        }
    }

    @Step("Отправляем post запрос")
    private static Response sendRequest(Object obj, String uri) {
        return given()
                .header("Content-Type", "application/json")
                .and()
                .body(obj)
                .when()
                .post(uri);
    }

    @Step("Создаем курьера (POST /courier)")
    private static Response createCourier(Courier courier) {
        return sendRequest(courier, "/courier");}

    @Step("Проверяем код ответа")
    private static void checkStatusCode(Response response, Integer expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Step("Проверяем формат ответа")
    private static void checkBodyBoolean(Response response, String expectedKey, Boolean expectedValue) {
        response.then().body(expectedKey, equalTo(expectedValue));
    }

    @Step("Получаем ID курьера")
    private static Integer getCourierId(Response response) {
        return response
                .then()
                .extract()
                .response()
                .jsonPath()
                .get("id");
    }

    @Step("Логиним курьера")
    private static Response loginCourier(Courier courier) {
        return sendRequest(CourierLoginModel.fromCourier(courier), "/courier/login");
    }

    @Step("Удаляем курьера")
    private static Response sendDeleteRequest(Integer courierId) {
        return given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/courier/" + courierId);
    }
}
