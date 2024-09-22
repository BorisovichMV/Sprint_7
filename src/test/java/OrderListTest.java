import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.example.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.instanceOf;

public class OrderListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Test
    @DisplayName("В ответе на get /orders возвращается список заказов")
    public void testOrderListInResponseBody() {
        List<Order> orders =given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("orders", instanceOf(List.class))
                .extract()
                .body()
                .jsonPath()
                .getList("orders", Order.class);
        Assert.assertFalse(orders.isEmpty());
    }
}
