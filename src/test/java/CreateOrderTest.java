import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.Order;
import org.example.OrderTrackModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final Order order;

    public CreateOrderTest(Order order) {
        this.order = order;
    }

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Parameterized.Parameters
    public static Object[][] getParameters() {
        return new Object[][]{
                {Order.getOrder(new String[]{"BLACK"})},
                {Order.getOrder(new String[]{"GRAY"})},
                {Order.getOrder(new String[]{"BLACK", "GRAY"})},
                {Order.getOrder(new String[]{})},
        };
    }

    @Test
    @DisplayName("Можно создать заказ с одним, двумя цветами или без цвета")
    public void testColors() {
        Response response = createOrder(order);
        checkStatusCode(response, 201);
        if (response.statusCode() == 201) {
            Integer orderId = getOrderId(response);
            cancelOrder(orderId);
        }
    }

    @Step("Отправляем  запрос")
    private static Response sendRequest(Object obj, String reqType ,String uri) {
        RequestSpecification body = given()
                .header("Content-Type", "application/json")
                .and()
                .body(obj);

        switch (reqType) {
            case "POST":
                return body.when().post(uri);
            case "PUT":
                return body.when().put(uri);
            case "DELETE":
                return body.when().delete(uri);
            default:
                return null;
        }
    }

    @Step("Создаём заказ")
    private static Response createOrder(Order order) {
        return sendRequest(order, "POST", "/orders");
    }

    @Step("Проверяем код ответа")
    private static void checkStatusCode(Response response, Integer expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Step("Получаем трек номер заказа")
    private static Integer getOrderId(Response response) {
        return response.then().extract().response().jsonPath().get("track");
    }

    @Step("Отменяем заказ")
    private static Response cancelOrder(Integer orderId) {
        return sendRequest(OrderTrackModel.fromOrderTrack(orderId), "PUT", "/orders/cancel");
    }
}
