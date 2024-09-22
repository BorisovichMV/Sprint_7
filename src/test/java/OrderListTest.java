import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Order;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.instanceOf;

public class OrderListTest {

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Test
    @DisplayName("В ответе на get /orders возвращается список заказов")
    public void testOrderListInResponseBody() {
        Response response = sendGetOrderRequest();
        checkStatusCode(response, 200);
        checkOrderList(response);
        checkListOfOrder(response);
    }

    @Step("Отправляем get запрос")
    public Response sendGetOrderRequest() {
        return given()
                .header("Content-Type", "application/json")
                .when()
                .get("/orders");
    }

    @Step("Проверяем код ответа")
    private static void checkStatusCode(Response response, Integer expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Step("Проверяем, что в ответе есть orders и это список")
    private static void checkOrderList(Response response) {
        response.then().body("orders", instanceOf(List.class));
    }

    @Step("Проверяем, что каждый элемент списка - объект Order")
    private static void checkListOfOrder(Response response) {
        List<Order> orders = response.then().extract().body().jsonPath().getList("orders", Order.class);
        Assert.assertFalse(orders.isEmpty());
    }
}
