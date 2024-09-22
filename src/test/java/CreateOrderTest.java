import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.example.Order;
import org.junit.Before;
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

    @Parameterized.Parameters
    public static Object[][] getParameters() {
        return new Object[][]{
                {Order.getOrder(new String[]{"BLACK"})},
                {Order.getOrder(new String[]{"GRAY"})},
                {Order.getOrder(new String[]{"BLACK", "GRAY"})},
                {Order.getOrder(new String[]{})},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/api/v1";
    }

    @Test
    @DisplayName("Можно создать заказ с одним, двумя цветами или без цвета")
    public void testColors() {
        given()
                .header("Content-Type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/orders")
                .then()
                .statusCode(201);
    }
}
