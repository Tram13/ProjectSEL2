package be.sel2.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * A test that simply attempts to build the application
 */
@SpringBootTest(classes = {ApiApplication.class})
@ActiveProfiles("test")
        //Set the active profile to use application-test.properties
class ApiApplicationTests {

    @Value("${application-version}")
    private String applicationVersion;

    // Running all tests: "mvn test" / "mvnw test"

    //Note to self: JUnit 5 does not work with maven, Junit jupiter does
    @Test
    @DisplayName("If this test fails, the server itself could not be run")
    void contextLoads() {
        assertNotNull(applicationVersion);
    }

}
