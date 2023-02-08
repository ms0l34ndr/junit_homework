package guru.qa;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ClickTheLinksParametrizedTest {

    @BeforeEach
    void openTab() {
        open("https://demoqa.com/links");
        Configuration.browserSize = "1920x1080";
        executeJavaScript("$('#fixedban').remove()");
        executeJavaScript("$('footer').remove()");
    }

    @ValueSource(strings = {
            "simpleLink",
            "dynamicLink"
    })
    @ParameterizedTest(name = "Проверяем, что при клике на ссылку с id элемента {0}, открывается новая вкладка в браузере")
    void newTabOpenTest(String linkid) {
        $('#' + linkid).click();
        assertTrue(WebDriverRunner.getWebDriver()
                .getWindowHandles()
                .size() > 1, "Новая вкладка не открылась");
    }

    @CsvSource(value = {
            "Created, 201",
            "No Content, 204",
            "Moved, 301"
    })
    @ParameterizedTest(name = "Проверяем, что при клике на ссылку {0}, мы видим текст со статусом {1}")
    void responseTest(String linkText, int responseCode) {
        $$("#linkWrapper a").find(text(linkText)).click();
        $("#linkResponse").shouldHave(text("Link has responded with staus "
                + responseCode
                + " and status text "
                + linkText));
    }

    static Stream<Arguments> linksAndStatuses() {
        return Stream.of(
                Arguments.of("bad-request", List.of(400, "Bad Request")),
                Arguments.of("unauthorized", List.of(401, "Unauthorized")),
                Arguments.of("forbidden", List.of(403, "Forbidden")),
                Arguments.of("invalid-url", List.of(404, "Not Found"))
        );
    }

    @MethodSource("linksAndStatuses")
    @ParameterizedTest(name = "Проверяем, что при клике на ссылку с id {0}, мы видим текст со статусом и текстом {1}")
    void openLinkByIdTest(String linkid, List<Integer> response) {
        $('#' + linkid).click();
        $("#linkResponse").shouldHave(text("Link has responded with staus "
                + response.get(0)
                + " and status text "
                + response.get(1)));
    }
}
