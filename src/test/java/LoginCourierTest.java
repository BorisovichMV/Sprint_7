import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Courier;
import org.example.CourierLoginModel;
import org.example.RandomStringGenerator;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class LoginCourierTest {

    private static Courier courier;

    private final CourierLoginModel courierLoginModel;
    private final Integer statusCode;
    private final String message;
    private final Boolean isLoginSuccessful;

    public LoginCourierTest(CourierLoginModel courierLoginModel, Integer statusCode, String message, Boolean isLoginSuccessful) {
        this.courierLoginModel = courierLoginModel;
        this.statusCode = statusCode;
        this.message = message;
        this.isLoginSuccessful = isLoginSuccessful;
    }

    @Parameterized.Parameters
    public static Object[][] getParameters() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
        if (courier == null) {
            String firstName = RandomStringGenerator.generateFirstName();
            String login = RandomStringGenerator.generateLogin();
            String password = RandomStringGenerator.generatePassword();
            courier = new Courier(login, password, firstName);
            createCourier(courier);
        }
        return new Object[][]{
                {CourierLoginModel.fromCourier(courier), 200, null, true},
                {new CourierLoginModel(courier.getLogin(), ""), 400, "Недостаточно данных для входа", null},
                {new CourierLoginModel("", courier.getPassword()), 400, "Недостаточно данных для входа", null},
                {new CourierLoginModel("", ""), 400, "Недостаточно данных для входа", null},
                {new CourierLoginModel(courier.getLogin(), "wrongPassword"), 404, "Учетная запись не найдена", null},
                {new CourierLoginModel("wrongLogin", courier.getPassword()), 404, "Учетная запись не найдена", null}
        };
    }

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Test
    @DisplayName("Проверяем возможность входа")
    public void testLogin() {
            checkLogin(courierLoginModel, statusCode, message, isLoginSuccessful);
    }

    @AfterClass
    public static void tearDown() {
        Response response = loginCourier(CourierLoginModel.fromCourier(courier));
        if (response.statusCode() == 200) {
            Integer courierId = getCourierId(response);

            Response deleteResponse = sendDeleteRequest(courierId);
            checkStatusCode(deleteResponse, 200);
            checkBoolean(deleteResponse, "ok", true);
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

    @Step("Создаём курьера")
    private static void createCourier(Courier courier) {
        sendRequest(courier, "/courier");
    }

    @Step("Логиним курьера")
    private static Response loginCourier(CourierLoginModel courierModel) {
        return sendRequest(courierModel, "/courier/login");
    }

    @Step("Проверяем тип возвлащаемого поля")
    private static void checkType(Response response, String fieldName, Class<?> expectedType) {
        Class<?> aClass = response
                .then()
                .extract()
                .response()
                .jsonPath()
                .get(fieldName)
                .getClass();
        Assert.assertEquals(expectedType, aClass);
    }

    @Step("Проверяем код ответа")
    private static void checkStatusCode(Response response, Integer expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Step("Проверяем текст сообщения")
    private static void checkMessage(Response response, String expectedField, String expectedMessage) {
        checkType(response, expectedField, String.class);
        response.then().body(expectedField, equalTo(expectedMessage));
    }

    @Step("Проверяем возможность входа, статус ответса и сообщение")
    private void checkLogin(CourierLoginModel model, Integer statusCode, String message, Boolean isLoginSuccessful) {
        Response response = loginCourier(model);
        checkStatusCode(response, statusCode);

        if (message != null) {
            checkMessage(response, "message", message);
        }

        if (isLoginSuccessful != null) {
            checkType(response, "id", Integer.class);
        }
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

    @Step("Удаляем курьера")
    private static Response sendDeleteRequest(Integer courierId) {
        return given()
                .header("Content-Type", "application/json")
                .when()
                .delete("/courier/" + courierId);
    }

    @Step("Проверяем boolean")
    private static void checkBoolean(Response response, String fieldName, Boolean expectedBoolean) {
        checkType(response, fieldName, Boolean.class);
        response.then().body(fieldName, equalTo(expectedBoolean));
    }


}
