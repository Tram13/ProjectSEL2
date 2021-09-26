package be.sel2.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class with basic implementation for tests.
 *
 * NOTE: All tests that could send emails should extend from {@link AbstractMailTest} instead.
 */
@SpringBootTest(classes = {ApiApplication.class})
@ActiveProfiles("test") //Set the active profile to use application-test.properties
@WebAppConfiguration
@WithUserDetails(
        value = "tester@admin.com",
        userDetailsServiceBeanName = "userService"
)
@Transactional
public abstract class AbstractTest {
    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper.readValue(json, clazz);
    }

    protected Map<String, Object> readObjectAsMap(String json) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<>() {
        };

        return objectMapper.readValue(json, typeRef);
    }
}
