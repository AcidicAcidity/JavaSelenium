package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO-модель (Plain Old Java Object) для пользователя.
 *
 * ЗАЧЕМ НУЖНА ЭТА МОДЕЛЬ?
 * Вместо работы с "сырым" JSON (строками) мы работаем с объектами Java.
 * Jackson автоматически превращает JSON в объект User и обратно.
 *
 * АННОТАЦИИ LOMBOK:
 * @Data      — генерирует геттеры, сеттеры, toString(), equals(), hashCode()
 * @Builder   — позволяет создавать объект через цепочку методов:
 *               User user = User.builder().name("John").email("john@test.com").build();
 * @NoArgsConstructor  — конструктор без параметров (нужен Jackson)
 * @AllArgsConstructor — конструктор со всеми параметрами
 *
 * АННОТАЦИИ JACKSON:
 * @JsonProperty("first_name") — маппит JSON-поле first_name на Java-поле firstName
 * @JsonIgnoreProperties(ignoreUnknown = true) — игнорирует неизвестные поля в JSON
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private Integer id;
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String avatar;
    private String name;
    private String job;

    @JsonProperty("createdAt")
    private String createdAt;
}
