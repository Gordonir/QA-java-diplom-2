package api;

import api.models.OrderRequest;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

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
        List<String> ingredients = Collections.singletonList(ingredientIds.get(0));
        OrderRequest orderRequest = new OrderRequest(ingredients);

        given()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(orderRequest)
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
        List<String> ingredients = Collections.singletonList(ingredientIds.get(0));
        OrderRequest orderRequest = new OrderRequest(ingredients);

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
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
        OrderRequest orderRequest = new OrderRequest(Collections.emptyList());

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
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
        List<String> ingredients = Collections.singletonList("invalid_id_123");
        OrderRequest orderRequest = new OrderRequest(ingredients);

        given()
                .contentType(ContentType.JSON)
                .body(orderRequest)
                .when()
                .post(ORDERS_ENDPOINT)
                .then()
                .statusCode(500);
    }
}