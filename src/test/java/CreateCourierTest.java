import io.restassured.RestAssured;
import org.example.Courier;
import org.example.RandomStringGenerator;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest {

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void testCourierCanToCreate() {
        String firstName = RandomStringGenerator.generateFirstName();
        String login = RandomStringGenerator.generateLogin();
        String password = RandomStringGenerator.generatePassword();

        Courier courier = new Courier(login, password, firstName);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    @Test
    public void testCreationSameCouriersNotAllowed() {
        String firstName = RandomStringGenerator.generateFirstName();
        String login = RandomStringGenerator.generateLogin();
        String password = RandomStringGenerator.generatePassword();

        Courier courier = new Courier(login, password, firstName);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409);
    }

    @Test
    public void testRequiredFields() {
        String firstName = RandomStringGenerator.generateFirstName();
        String login = RandomStringGenerator.generateLogin();
        String password = RandomStringGenerator.generatePassword();

        Courier validCourier = new Courier(login, password, null);
        Courier invalidPasswordCourier = new Courier(login, null, firstName);
        Courier invalidLoginCourier = new Courier(null, password, firstName);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(invalidLoginCourier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(invalidPasswordCourier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(validCourier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
    }

    @Test
    public void testCreationReturnsRightCode() {
        String firstName = RandomStringGenerator.generateFirstName();
        String login = RandomStringGenerator.generateLogin();
        String password = RandomStringGenerator.generatePassword();

        Courier validCourier = new Courier(login, password, null);
        Courier invalidPasswordCourier = new Courier(login, null, firstName);
        Courier invalidLoginCourier = new Courier(null, password, firstName);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(invalidLoginCourier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(invalidPasswordCourier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(validCourier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(validCourier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409);
    }

    @Test
    public void testCreationReturnsRightMessage() {
        String firstName = RandomStringGenerator.generateFirstName();
        String login = RandomStringGenerator.generateLogin();
        String password = RandomStringGenerator.generatePassword();

        Courier courier = new Courier(login, password, firstName);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .body("ok", equalTo(true));
    }

    @Test
    public void testCreationReturnsRightMessageWithExistingFields() {
        String firstName = RandomStringGenerator.generateFirstName();
        String anotherFirstName = RandomStringGenerator.generateFirstName();
        String login = RandomStringGenerator.generateLogin();
        String anotherPassword = RandomStringGenerator.generatePassword();
        String password = RandomStringGenerator.generatePassword();

        Courier courier = new Courier(login, password, firstName);
        Courier anotherCourier = new Courier(login, anotherPassword, anotherFirstName);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(anotherCourier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409);
    }
}
