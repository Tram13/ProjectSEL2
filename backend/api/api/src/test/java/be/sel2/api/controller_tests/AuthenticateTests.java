package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import be.sel2.api.util.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A testing class for the authentication endpoints `/auth/`
 */
class AuthenticateTests extends AbstractTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    /**
     * Tests the GET to the root of the /auth/
     */
    @Test
    void testAuthRootResponse() throws Exception {
        String uri = "/auth";

        // Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> result = super.readObjectAsMap(content);
        assertTrue(result.get("_links") instanceof Map);

        Map<?, ?> links = (Map<?, ?>) result.get("_links");

        assertTrue(links.get("self") instanceof Map);

        String baseUrl = (String) ((Map<?, ?>) links.get("self")).get("href");

        assertEquals(baseUrl + "/register", ((Map<?, ?>) links.get("register")).get("href"));
        assertEquals(baseUrl + "/login", ((Map<?, ?>) links.get("login")).get("href"));
        assertEquals(baseUrl + "/refreshSessionToken", ((Map<?, ?>) links.get("refreshSessionToken")).get("href"));
        assertEquals(baseUrl + "/refreshToken", ((Map<?, ?>) links.get("refreshToken")).get("href"));
        assertEquals(baseUrl + "/passwordReset", ((Map<?, ?>) links.get("passwordReset")).get("href"));
        assertEquals(baseUrl + "/passwordReset/form", ((Map<?, ?>) links.get("passwordResetForm")).get("href"));
        assertEquals(baseUrl + "/emailConfirmation", ((Map<?, ?>) links.get("emailConfirmation")).get("href"));
        assertEquals(baseUrl + "/resendEmailConfirmation", ((Map<?, ?>) links.get("resendEmailConfirmation")).get("href"));
    }

    /**
     * Tests POST to path `/auth/login` and `/auth/register`
     */
    @Test
    void testRegisterAndLogin() throws Exception {
        String uri = "/auth/register";
        Map<String, Object> registerData = new HashMap<>();
        registerData.put("email", "test@testerken.com");
        registerData.put("password", "Test1234");
        registerData.put("firstName", "Testerken");
        registerData.put("lastName", "Het Testerke");
        String inputJson = super.mapToJson(registerData);

        //Call POST with body inputJson, to register a new user
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);

        uri = "/auth/login";

        Map<String, Object> loginData = new HashMap<>();
        loginData.put("email", "test@testerken.com");
        loginData.put("password", "Test1234");
        inputJson = super.mapToJson(loginData);

        // Call POST with body inputJson, to login a user
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> parsedJson = readObjectAsMap(content);

        assertTrue(parsedJson.containsKey("sessionToken"));
        assertTrue(parsedJson.containsKey("refreshToken"));
        assertTrue(jwtUtil.validateToken((String) parsedJson.get("sessionToken")));
        assertTrue(jwtUtil.validateRefreshToken((String) parsedJson.get("refreshToken")));
    }

    /**
     * Tests POST to path `/auth/refreshToken` and `/auth/refreshSessionToken`
     */
    @Test
    void testRefreshSessionAndRefreshTokenSession() throws Exception {
        String uri = "/auth/login";
        Map<String, Object> loginData = new HashMap<>();
        loginData.put("email", "tester@admin.com");
        loginData.put("password", "A1aaaaaa");
        String inputJson = super.mapToJson(loginData);

        // Call POST with body inputJson, to login a user
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> parsedJson = readObjectAsMap(content);

        String refreshToken = (String) parsedJson.get("refreshToken");

        uri = "/auth/refreshSessionToken";

        Map<String, Object> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);
        inputJson = super.mapToJson(refreshRequest);

        // Call POST with body inputJson, to get a new sessionToken
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        content = mvcResult.getResponse().getContentAsString();

        parsedJson = readObjectAsMap(content);
        assertTrue(parsedJson.containsKey("sessionToken"));
        assertTrue(jwtUtil.validateToken((String) parsedJson.get("sessionToken")));

        uri = "/auth/refreshToken";

        // Call POST with body inputJson, to get a new refreshToken
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        content = mvcResult.getResponse().getContentAsString();

        parsedJson = readObjectAsMap(content);
        assertTrue(parsedJson.containsKey("refreshToken"));
        assertTrue(jwtUtil.validateRefreshToken((String) parsedJson.get("refreshToken")));
    }
    /**
     * Tests error code of POST to path `/auth/refreshToken` and `/auth/refreshSessionToken`
     */
    @Test
    void testExpiredTokens() throws Exception {
        String uri = "/auth/refreshSessionToken";
        String expiredRefreshToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxNCIsInNjb3BlIjoicmVmcmVzaFRva2VuIiwiaWF0IjoxNjIwMjI2MjQ5LCJleHAiOjE2MjAyMjYyNTB9.knr6hV2cUZdE9tmmUXe4fbEBe_HSyhnQ4DScOltN-LGijtngJ8IfRijx-eKIsLQ5JCdKSNz2Xc7XfH28E2T0cSuHXxX_NXtC17WXxizIpvc3KHznZ_rxTAwlgH244BLo4st5nfLWIJ8ULRfp-jeLjziAEBW7N5xCaWt-34VjGhAmJcKRe4bKBu1YUnsSJrmnrHAE8SeLdki_WK1mK6YIeEF9MU9tmQkXXJShzpjkRUTk1XNXjYrfEvtPv2LDzK2MHYKdWMMfk6sxL913tk5OlQBc2p_iIm1hLDW5BQYRU7T-m6Oh3EBs19JwWNhvgNINHYQdOUI-jONyhngiE4BEnQ";

        Map<String, Object> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", expiredRefreshToken);
        String inputJson = super.mapToJson(refreshRequest);

        // Call POST with body inputJson, to refresh the sessionToken
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(403, status);

        uri = "/auth/refreshToken";

        // Call POST with body inputJson, to refresh the refreshToken
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(403, status);
    }

    @Test
    void invalidBodyShouldBeHandled() throws Exception {
        String uri = "/auth/login";

        Map<String, Object> loginData = new HashMap<>();
        loginData.put("email", "test@testerken.com");
        String inputJson = super.mapToJson(loginData);

        // Call POST with body inputJson, to login a user
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        assertEquals(400, mvcResult.getResponse().getStatus());
        String result = mvcResult.getResponse().getContentAsString();

        assertTrue(result.contains("\"parameter\":\"password\""));
        assertTrue(result.contains("\"message\":\"must not be null\""));
    }

    @Test
    void invalidCredentialsShouldBeHandled() throws Exception {
        String uri = "/auth/login";

        Map<String, Object> loginData = new HashMap<>();
        loginData.put("email", "test@testerken.com");
        loginData.put("password", "incorrectPassword");
        String inputJson = super.mapToJson(loginData);

        // Call POST with body inputJson, to login a user
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        assertEquals(400, mvcResult.getResponse().getStatus());
        String result = mvcResult.getResponse().getContentAsString();

        System.out.println(result);

        assertTrue(result.contains("\"Incorrect username or password\""));
    }
}