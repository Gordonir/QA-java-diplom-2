package api;

import io.qameta.allure.*;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API Тесты")
@Feature("Заказы")
public class OrderTest extends BaseApiTest {

    @Test
    @Story("Создание заказа")
    @Description("Создание заказа с авторизацией")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateOrderWithAuth() {
        accessToken = registerUser("user" + System.currentTimeMillis() + "@mail.com", "password", "Name");

        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(String.format("{\"ingredients\":[\"%s\"]}", ingredientIds.get(0)))
                .when()
                .post(ORDERS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Story("Создание заказа")
    @Description("Создание заказа без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateOrderWithoutAuth() {
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"ingredients\":[\"%s\"]}", ingredientIds.get(0)))
                .when()
                .post(ORDERS_ENDPOINT)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @Story("Валидация заказа")
    @Description("Создание заказа без ингредиентов")
    @Severity(SeverityLevel.MINOR)
    public void testCreateOrderWithoutIngredients() {
        given()
                .header("Content-type", "application/json")
                .body("{\"ingredients\":[]}")
                .when()
                .post(ORDERS_ENDPOINT)
                .then()
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Story("Валидация заказа")
    @Description("Создание заказа с неверным хешем ингредиента")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateOrderWithInvalidIngredient() {
        given()
                .header("Content-type", "application/json")
                .body("{\"ingredients\":[\"invalid_id_123\"]}")
                .when()
                .post(ORDERS_ENDPOINT)
                .then()
                .statusCode(500);
    }
}