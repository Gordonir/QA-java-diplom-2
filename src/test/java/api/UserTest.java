package api;

import io.qameta.allure.*;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API Тесты")
@Feature("Пользователь")
public class UserTest extends BaseApiTest {

    @Test
    @Story("Обновление данных")
    @Description("Изменение email пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateUserEmail() {
        accessToken = registerUser("user" + System.currentTimeMillis() + "@mail.com", "password", "Name");
        String newEmail = "updated" + System.currentTimeMillis() + "@mail.com";

        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(String.format("{\"email\":\"%s\"}", newEmail))
                .when()
                .patch(USER_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail));
    }

    @Test
    @Story("Обновление данных")
    @Description("Изменение имени пользователя")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUserName() {
        accessToken = registerUser("user" + System.currentTimeMillis() + "@mail.com", "password", "OldName");

        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body("{\"name\":\"NewName\"}")
                .when()
                .patch(USER_ENDPOINT)
                .then()
                .statusCode(200)
                .body("user.name", equalTo("NewName"));
    }

    @Test
    @Story("Безопасность")
    @Description("Попытка изменения данных без авторизации")
    @Severity(SeverityLevel.BLOCKER)
    public void testUpdateUnauthorized() {
        given()
                .header("Content-type", "application/json")
                .body("{\"name\":\"Hacker\"}")
                .when()
                .patch(USER_ENDPOINT)
                .then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @Story("Заказы")
    @Description("Получение заказов авторизованного пользователя")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserOrdersWithAuth() {
        accessToken = registerUser("user" + System.currentTimeMillis() + "@mail.com", "password", "Name");

        given()
                .header("Authorization", accessToken)
                .when()
                .get("/orders")
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Story("Заказы")
    @Description("Попытка получить заказы без авторизации")
    @Severity(SeverityLevel.NORMAL)
    public void testGetUserOrdersWithoutAuth() {
        given()
                .when()
                .get("/orders")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}