import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.dto.InputQueryRequest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputQueryRequestTests {

    private static final String validUri = "/events/1";
    private static final String validIp = "192.168.1.1";
    private static final String validApp = "ewm-service";
    private static final LocalDateTime validTimestamp = LocalDateTime.now();

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
    void testAllFieldsCorrectThenValidationSucceeds() {

        InputQueryRequest request = InputQueryRequest.builder()
                .app(validApp)
                .uri(validUri)
                .ip(validIp)
                .timestamp(validTimestamp)
                .build();

        Set<ConstraintViolation<InputQueryRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testAppIsBlankThenValidationFails() {

        InputQueryRequest request = InputQueryRequest.builder()
                .app("")
                .uri(validUri)
                .ip(validIp)
                .timestamp(validTimestamp)
                .build();

        Set<ConstraintViolation<InputQueryRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("app", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testUriIsInvalidThenValidationFails() {

        InputQueryRequest request = InputQueryRequest.builder()
                .app(validApp)
                .uri("invalid uri")
                .ip(validIp)
                .timestamp(validTimestamp)
                .build();

        Set<ConstraintViolation<InputQueryRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("uri", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testUriIsNullThenValidationSucceeds() {

        InputQueryRequest request = InputQueryRequest.builder()
                .app(validApp)
                .uri(null)
                .ip(validIp)
                .timestamp(validTimestamp)
                .build();

        Set<ConstraintViolation<InputQueryRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testIpIsInvalidThenValidationFails() {

        InputQueryRequest request = InputQueryRequest.builder()
                .app(validApp)
                .uri(validUri)
                .ip("invalid.ip")
                .timestamp(validTimestamp)
                .build();

        Set<ConstraintViolation<InputQueryRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("ip", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void testIpIsNullThenValidationSucceeds() {

        InputQueryRequest request = InputQueryRequest.builder()
                .app(validApp)
                .uri(validUri)
                .ip(null)
                .timestamp(validTimestamp)
                .build();

        Set<ConstraintViolation<InputQueryRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testTimestampIsNullThenValidationFails() {

        InputQueryRequest request = InputQueryRequest.builder()
                .app(validApp)
                .uri(validUri)
                .ip(validIp)
                .timestamp(null)
                .build();

        Set<ConstraintViolation<InputQueryRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("timestamp", violations.iterator().next().getPropertyPath().toString());
    }


    @Test
    void testUriWithMultipleSegmentsThenValidationSucceeds() {

        InputQueryRequest request = InputQueryRequest.builder()
                .app(validApp)
                .uri("/events/1/comments/2")
                .ip(validIp)
                .timestamp(validTimestamp)
                .build();

        Set<ConstraintViolation<InputQueryRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testUriEndsWithSlashThenValidationSucceeds() {

        InputQueryRequest request = InputQueryRequest.builder()
                .app(validApp)
                .uri("/events/")
                .ip(validIp)
                .timestamp(validTimestamp)
                .build();

        Set<ConstraintViolation<InputQueryRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}