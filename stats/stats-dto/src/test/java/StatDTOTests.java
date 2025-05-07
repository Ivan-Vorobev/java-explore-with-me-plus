import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.dto.StatDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StatDTOTests {

    private static final String VALID_APP = "ewm-service";
    private static final String INVALID_APP = "";
    private static final String VALID_URI = "/events/1";
    private static final String INVALID_URI = "invalid uri";
    private static final Long VALID_HITS = 100L;
    private static final Long INVALID_HITS = null;
    private static final String[] VALID_URIS = {
            "/events",
            "/events/1",
            "/events/1/comments",
            "/events/1/comments/",
            "/user-profile",
            "/user_profile",
            "/user-1/profile-2"
    };

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    @Test
    void testAllFieldsValid() {

        StatDTO dto = StatDTO.builder()
                .app(VALID_APP)
                .uri(VALID_URI)
                .hits(VALID_HITS)
                .build();

        Set<ConstraintViolation<StatDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }

    @Test
    void testAppNotBlank() {

        StatDTO dto = StatDTO.builder()
                .app(INVALID_APP)
                .uri(VALID_URI)
                .hits(VALID_HITS)
                .build();

        Set<ConstraintViolation<StatDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
    }

    @Test
    void testUriPatternInvalid() {

        StatDTO dto = StatDTO.builder()
                .app(VALID_APP)
                .uri(INVALID_URI)
                .hits(VALID_HITS)
                .build();

        Set<ConstraintViolation<StatDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("Invalid endpoint format (e.g., '/events/1')", violations.iterator().next().getMessage());
    }

    @Test
    void testUriPatternValidVariations() {

        for (String uri : VALID_URIS) {
            StatDTO dto = StatDTO.builder()
                    .app(VALID_APP)
                    .uri(uri)
                    .hits(VALID_HITS)
                    .build();

            Set<ConstraintViolation<StatDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), "URI '" + uri + "' должен быть валидным");
        }
    }

    @Test
    void testHitsNotNull() {

        StatDTO dto = StatDTO.builder()
                .app(VALID_APP)
                .uri(VALID_URI)
                .hits(INVALID_HITS)
                .build();

        Set<ConstraintViolation<StatDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
    }

    @Test
    void testLombokAnnotations() {

        StatDTO dto1 = StatDTO.builder()
                .app("app1")
                .uri("/uri1")
                .hits(10L)
                .build();

        StatDTO dto2 = StatDTO.builder()
                .app("app1")
                .uri("/uri1")
                .hits(10L)
                .build();

        assertEquals(dto1, dto2, "Объекты должны быть равны");
        assertEquals(dto1.hashCode(), dto2.hashCode(), "Хэш-коды должны быть равны");
        assertNotNull(dto1.toString(), "toString не должен возвращать null");
        assertTrue(dto1.toString().contains("app=app1"), "toString должен содержать значение app");
    }
}