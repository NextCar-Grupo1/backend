package org.pe.nextcar.iam.infrastructure.hashing.bcrypt.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HashingServiceImpl is a thin wrapper over BCryptPasswordEncoder with no external
 * collaborators, so it is exercised directly rather than through Mockito mocks.
 */
class HashingServiceImplTest {

    private HashingServiceImpl hashingService;

    @BeforeEach
    void setUp() {
        // Arrange (shared)
        hashingService = new HashingServiceImpl();
    }

    @Test
    void encode_ShouldReturnNonNullHash_DifferentFromRawPassword() {
        // Arrange
        String rawPassword = "NextCar123";

        // Act
        String encoded = hashingService.encode(rawPassword);

        // Assert
        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoded.startsWith("$2"));
    }

    @Test
    void encode_ShouldProduceDifferentHashes_ForSamePasswordOnDifferentCalls() {
        // Arrange
        String rawPassword = "NextCar123";

        // Act
        String encodedFirst = hashingService.encode(rawPassword);
        String encodedSecond = hashingService.encode(rawPassword);

        // Assert
        assertNotEquals(encodedFirst, encodedSecond, "BCrypt should salt each hash differently");
    }

    @Test
    void matches_ShouldReturnTrue_WhenRawPasswordMatchesEncodedPassword() {
        // Arrange
        String rawPassword = "NextCar123";
        String encoded = hashingService.encode(rawPassword);

        // Act
        boolean result = hashingService.matches(rawPassword, encoded);

        // Assert
        assertTrue(result);
    }

    @Test
    void matches_ShouldReturnFalse_WhenRawPasswordDoesNotMatchEncodedPassword() {
        // Arrange
        String encoded = hashingService.encode("NextCar123");

        // Act
        boolean result = hashingService.matches("WrongPassword", encoded);

        // Assert
        assertFalse(result);
    }
}
