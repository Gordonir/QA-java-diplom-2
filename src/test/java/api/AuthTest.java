package api;

import api.models.User;
import api.models.LoginRequest;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API Тесты")
@Feature("Авторизация")
public class AuthTest extends BaseApiTest {

    @Test
    @Story("Регистрация")
    @Description("Успешная регистрация пользователя")
    @Severity(SeverityLevel.BLOCKER)
    public void testSuccessfulRegistration() {
        User user = new User(
                "test" + System.currentTimeMillis() + "@mail.com",
                "password",
                "User"
        );

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @Story("Регистрация")
    @Description("Регистрация существующего пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void testRegisterExistingUser() {
        String email = "existing" + System.currentTimeMillis() + "@mail.com";
        User user = new User(email, "password", "User");

        registerUser(email, "password", "User");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Story("Регистрация")
    @Description("Регистрация без обязательного поля")
    @Severity(SeverityLevel.CRITICAL)
    public void testRegisterWithoutRequiredField() {
        User user = new User(null, "password", "User");

        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(REGISTER_ENDPOINT)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @Story("Авторизация")
    @Description("Успешная авторизация")
    @Severity(SeverityLevel.BLOCKER)
    public void testSuccessfulLogin() {
        String email = "test" + System.currentTimeMillis() + "@mail.com";
        registerUser(email, "password", "User");

        LoginRequest loginRequest = new LoginRequest(email, "password");

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Story("Авторизация")
    @Description("Авторизация с неверными данными")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest("wrong@mail.com", "wrong");

        given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}