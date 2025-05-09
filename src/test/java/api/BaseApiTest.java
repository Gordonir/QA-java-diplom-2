package api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.BeforeClass;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BaseApiTest {
    protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";
    protected static final String ORDERS_ENDPOINT = "/orders";
    protected static final String REGISTER_ENDPOINT = "/auth/register";
    protected static final String LOGIN_ENDPOINT = "/auth/login";
    protected static final String USER_ENDPOINT = "/auth/user";
    protected static final String INGREDIENTS_ENDPOINT = "/ingredients";
    protected static final String PASSWORD_RESET_ENDPOINT = "/password-reset";
    protected static final String LOGOUT_ENDPOINT = "/auth/logout";

    protected static List<String> ingredientIds;
    protected String accessToken;
    protected String refreshToken;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        ingredientIds = getIngredients();
    }

    @Step("Получить список ингредиентов")
    protected static List<String> getIngredients() {
        return given()
                .get(INGREDIENTS_ENDPOINT)
                .then()
                .statusCode(200)
                .extract().path("data._id");
    }

    @Step("Регистрация пользователя {email}")
    protected String registerUser(String email, String password, String name) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(String.format("{\"email\":\"%s\",\"password\":\"%s\",\"name\":\"%s\"}",
                        email, password, name))
                .post(REGISTER_ENDPOINT);

        this.accessToken = response.path("accessToken");
        this.refreshToken = response.path("refreshToken");
        return response.path("accessToken");
    }

    @Step("Авторизация пользователя {email}")
    protected void loginUser(String email, String password) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password))
                .post(LOGIN_ENDPOINT);

        this.accessToken = response.path("accessToken");
        this.refreshToken = response.path("refreshToken");
    }

    @Step("Выход из системы")
    protected void logoutUser() {
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"token\":\"%s\"}", refreshToken))
                .post(LOGOUT_ENDPOINT)
                .then()
                .statusCode(200);
    }

    @Step("Сброс пароля для email {email}")
    protected void resetPassword(String email) {
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"email\":\"%s\"}", email))
                .post(PASSWORD_RESET_ENDPOINT)
                .then()
                .statusCode(200);
    }

    @Step("Получение данных пользователя")
    protected Response getUserData() {
        return given()
                .header("Authorization", accessToken)
                .get(USER_ENDPOINT);
    }

    @Step("Создание заказа с ингредиентами {ingredientIds}")
    protected Response createOrder(List<String> ingredientIds) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken != null ? accessToken : "")
                .body(String.format("{\"ingredients\":[\"%s\"]}", String.join("\",\"", ingredientIds)))
                .post(ORDERS_ENDPOINT);
    }

    @Step("Получение заказов пользователя")
    protected Response getUserOrders() {
        return given()
                .header("Authorization", accessToken != null ? accessToken : "")
                .get(ORDERS_ENDPOINT);
    }

    @After
    @Step("Удаление тестового пользователя")
    public void tearDown() {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .delete(USER_ENDPOINT)
                    .then()
                    .statusCode(202);
        }
    }
}