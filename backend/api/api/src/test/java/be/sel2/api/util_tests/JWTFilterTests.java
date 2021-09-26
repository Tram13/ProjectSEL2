package be.sel2.api.util_tests;

import be.sel2.api.ApiApplication;
import be.sel2.api.util.filter_chain.FilterChainExceptionHandler;
import be.sel2.api.util.jwt.JwtFilter;
import be.sel2.api.util.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ApiApplication.class})
@ActiveProfiles("test") //Set the active profile to use application-test.properties
@WebAppConfiguration
@Transactional
class JWTFilterTests {

    protected MockMvc mvc;
    @Autowired
    private JwtFilter filter;
    @Autowired
    private FilterChainExceptionHandler exceptionHandler;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    protected void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilters(exceptionHandler, filter)
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideNoFilterUris")
    void testNoFilter(boolean methodPost, String uri, String body) throws Exception {
        MvcResult mvcResult;
        if (methodPost) {
            mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(body)
                    .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        } else {
            mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                    .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        }

        assertNotEquals(403, mvcResult.getResponse().getStatus());
        assertNotEquals(500, mvcResult.getResponse().getStatus());
    }

    static Stream<Arguments> provideNoFilterUris() {
        return Stream.of(
                Arguments.of(false, "/", ""),
                Arguments.of(false, "/auth", ""),
                Arguments.of(true, "/auth/login", "{\"email\":\"tester@admin.com\", \"password\":\"A1aaaaaa\"}"),
                Arguments.of(true, "/auth/register", "{\"firstName\": \"Michael\",\"lastName\": \"Jackson\",\"email\": \"Michael.Jackson@UGent.be\",\"password\": \"SurroundedHunter2\"}")
        );
    }

    @Test
    void testNoAuthorizedWithoutToken() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/loginData")).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
    }

    @Test
    void testCorrectlyReadsUserId() throws Exception {
        String token = jwtUtil.generateToken(5L);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/loginData")
        .header("Authorization", "Bearer " + token)).andReturn();

        assertEquals(302, mvcResult.getResponse().getStatus());
        assertNotNull(mvcResult.getResponse().getRedirectedUrl());
        assertTrue(mvcResult.getResponse().getRedirectedUrl().matches("^.*users/5$"));
    }

    @Test
    void forgedTokenShouldNotBeAllowed() throws Exception {
        String token = jwtUtil.generateToken(5L);

        token = token.replaceAll(".{10}$", "aAaAaA123Z");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/loginData")
                .header("Authorization", "Bearer " + token)).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("signature"));
    }

    @Test
    void malformedTokenShouldNotBeAllowed() throws Exception {
        String token = jwtUtil.generateToken(5L);

        token = token.replaceAll("\\.[^.]+\\.", "aAaAaA123Z");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/loginData")
                .header("Authorization", "Bearer " + token)).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("malformed"));
    }

    @Test
    void nonBearerTokenShouldNotBeAllowed() throws Exception {
        String token = jwtUtil.generateToken(5L);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/loginData")
                .header("Authorization", "AWS4-HMAC-SHA256 " + token)).andReturn();

        assertEquals(403, mvcResult.getResponse().getStatus());
    }
}
