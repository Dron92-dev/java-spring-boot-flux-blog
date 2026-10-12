package io.hexlet;

import static org.assertj.core.api.Assertions.assertThat;

import io.hexlet.model.Article;
import io.hexlet.model.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {

    @Autowired private WebTestClient webTestClient;

    @Test
    @Order(1)
    public void testGetUsers() {
        webTestClient
                // Указываем метод запроса (GET) и адрес (*/users*)
                .get()
                .uri("/users")
                // Указываем, какой тип контента мы хотим получить
                .accept(MediaType.APPLICATION_JSON)
                // Выполняем запрос
                .exchange()
                // Проверяем полученный ответ
                .expectStatus()
                .isOk()
                .expectBodyList(User.class)
                .hasSize(4);
    }

    @Test
    @Order(2)
    public void testGetUser() {
        var existingUser = "a@b";
        var existingUserId = TestUtils.getUserId(existingUser, webTestClient);
        webTestClient
                .get()
                .uri("/users/{id}", existingUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(User.class)
                .value(
                        user -> {
                            assertThat(user.getFirstName()).isEqualTo("John");
                            assertThat(user.getLastName()).isEqualTo("Smith");
                        });
    }

    @Test
    @Order(3)
    public void testUpdateUser() {
        var existingUser = "a@b";
        var existingUserId = TestUtils.getUserId(existingUser, webTestClient);
        User newUserData = new User("Test", "User", "test@test");
        webTestClient
                .patch()
                .uri("/users/{id}", existingUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newUserData), User.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/users/{id}", existingUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(User.class)
                .value(
                        user -> {
                            assertThat(user.getFirstName()).isEqualTo("Test");
                            assertThat(user.getLastName()).isEqualTo("User");
                        });
    }

    @Test
    @Order(4)
    public void testAddUser() {
        User newUserData = new User("New", "TestUser", "t@t");
        webTestClient
                .post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newUserData), User.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .value(
                        body -> {
                            assertThat(body).contains("TestUser", "New", "t@t");
                        });
    }

    @Test
    @Order(5)
    public void testDeleteUser() {
        var existingUser = "c@d";
        var existingUserId = TestUtils.getUserId(existingUser, webTestClient);

        webTestClient.delete().uri("/users/{id}", existingUserId).exchange().expectStatus().isOk();

        webTestClient
                .get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .value(
                        body -> {
                            assertThat(body).doesNotContain(existingUser);
                        });
    }

    @Test
    @Order(6)
    public void testAddArticle() {
        Article newArticleData = new Article("New", "Test content");
        webTestClient
                .post()
                .uri("/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(newArticleData), Article.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/articles")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .value(
                        body -> {
                            assertThat(body).contains("New", "Test content");
                        });
    }

    @Test
    @Order(7)
    public void testGetArticle() {
        Article newArticleData = new Article("New", "Test content");
        Article createArticle =
                webTestClient
                        .post()
                        .uri("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(Mono.just(newArticleData), Article.class)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(Article.class)
                        .returnResult()
                        .getResponseBody();

        long articleId = createArticle.getId();
        webTestClient
                .get()
                .uri("/articles/{id}", articleId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Article.class)
                .value(
                        article -> {
                            assertThat(article.getTitle()).isEqualTo("New");
                            assertThat(article.getContent()).isEqualTo("Test content");
                        });
    }

    @Test
    @Order(8)
    public void testUpdateArticle() {
        Article newArticleData = new Article("New", "Test content");
        Article createArticle =
                webTestClient
                        .post()
                        .uri("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(Mono.just(newArticleData), Article.class)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(Article.class)
                        .returnResult()
                        .getResponseBody();

        long articleId = createArticle.getId();
        Article updatedArticle = new Article("Updated", "Updated content");

        webTestClient
                .patch()
                .uri("/articles/{id}", articleId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedArticle), Article.class)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient
                .get()
                .uri("/articles/{id}", articleId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Article.class)
                .value(
                        article -> {
                            assertThat(article.getTitle()).isEqualTo("Updated");
                            assertThat(article.getContent()).isEqualTo("Updated content");
                        });
    }

    @Test
    @Order(9)
    public void testDeleteArticle() {
        Article newArticleData = new Article("New", "Test content");
        Article createArticle =
                webTestClient
                        .post()
                        .uri("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .body(Mono.just(newArticleData), Article.class)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(Article.class)
                        .returnResult()
                        .getResponseBody();

        long articleId = createArticle.getId();

        webTestClient.delete().uri("/articles/{id}", articleId).exchange().expectStatus().isOk();

        webTestClient
                .get()
                .uri("/articles/{id}", articleId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();
    }
}
