package api;

import io.qameta.allure.*;
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
        given()
                .header("Content-type", "application/json")
                .body("{\"email\":\"test" + System.currentTimeMillis() + "@mail.com\",\"password\":\"password\",\"name\":\"User\"}")
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
        registerUser(email, "password", "User");

        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"email\":\"%s\",\"password\":\"password\",\"name\":\"User\"}", email))
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
        given()
                .header("Content-type", "application/json")
                .body("{\"password\":\"password\",\"name\":\"User\"}")
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

        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"email\":\"%s\",\"password\":\"password\"}", email))
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
        given()
                .header("Content-type", "application/json")
                .body("{\"email\":\"wrong@mail.com\",\"password\":\"wrong\"}")
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}