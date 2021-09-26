package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityTest extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @ParameterizedTest
    @MethodSource("provideCustomerUris")
    @WithUserDetails(
            value = "ambethie@artevelde3.be",
            userDetailsServiceBeanName = "userService"
    )
    void testPathStatusAsCustomer(String uri, int status) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertEquals(status, mvcResult.getResponse().getStatus());
    }

    static Stream<Arguments> provideCustomerUris(){
        return Stream.of(
                Arguments.of("/", 200),
                Arguments.of("/config", 200),
                Arguments.of("/auth", 200),
                Arguments.of("/users", 403),
                Arguments.of("/users/5", 403),
                Arguments.of("/users/1", 200),
                Arguments.of("/users/5/organisations", 403),
                Arguments.of("/users/1/organisations", 200),
                Arguments.of("/organisations", 403),
                Arguments.of("/organisations/1", 200),
                Arguments.of("/organisations/2", 403),
                Arguments.of("/organisations/1/members", 200),
                Arguments.of("/organisations/2/members", 403),
                Arguments.of("/organisations/1/members/1", 200),
                Arguments.of("/organisations/1/members/2", 200),
                Arguments.of("/organisations/2/members/1", 403),
                Arguments.of("/organisations/1/contacts", 200),
                Arguments.of("/organisations/2/contacts", 403),
                Arguments.of("/organisations/1/contacts/1", 200),
                Arguments.of("/organisations/2/contacts/1", 403),
                Arguments.of("/proposals", 200),
                Arguments.of("/proposals/1", 200),
                Arguments.of("/services", 200),
                Arguments.of("/services/1", 200),
                Arguments.of("/packages", 200),
                Arguments.of("/packages/1", 200),
                Arguments.of("/permissions", 200),
                Arguments.of("/permissions/1", 200),
                Arguments.of("/certificates", 200),
                Arguments.of("/certificates/1", 200),
                Arguments.of("/files/1", 200),
                Arguments.of("/statistics", 403),
                Arguments.of("/statistics/users", 403),
                Arguments.of("/statistics/organisations", 403),
                Arguments.of("/statistics/organisations/1", 403),
                Arguments.of("/statistics/proposals", 403),
                Arguments.of("/statistics/services", 403),
                Arguments.of("/statistics/services/1", 403),
                Arguments.of("/statistics/packages", 403),
                Arguments.of("/statistics/packages/1", 403),
                Arguments.of("/statistics/permissions", 403),
                Arguments.of("/statistics/certificates", 403),
                Arguments.of("/statistics/files", 403)
        );
    }

    @ParameterizedTest
    @MethodSource("provideEmployeeUris")
    @WithUserDetails(
            value = "peter.selie@gmail.com",
            userDetailsServiceBeanName = "userService"
    )
    void testPathStatusAsEmployee(String uri, int status) throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertEquals(status, mvcResult.getResponse().getStatus());
    }

    static Stream<Arguments> provideEmployeeUris(){
        return Stream.of(
                Arguments.of("/", 200),
                Arguments.of("/config", 200),
                Arguments.of("/auth", 200),
                Arguments.of("/users", 200),
                Arguments.of("/users/5", 200),
                Arguments.of("/users/1", 200),
                Arguments.of("/users/5/organisations", 200),
                Arguments.of("/users/1/organisations", 200),
                Arguments.of("/organisations", 200),
                Arguments.of("/organisations/1", 200),
                Arguments.of("/organisations/2", 200),
                Arguments.of("/organisations/1/members", 200),
                Arguments.of("/organisations/2/members", 200),
                Arguments.of("/organisations/1/members/1", 200),
                Arguments.of("/organisations/1/contacts", 200),
                Arguments.of("/organisations/2/contacts", 200),
                Arguments.of("/organisations/1/contacts/1", 200),
                Arguments.of("/proposals", 200),
                Arguments.of("/proposals/1", 200),
                Arguments.of("/services", 200),
                Arguments.of("/services/1", 200),
                Arguments.of("/packages", 200),
                Arguments.of("/packages/1", 200),
                Arguments.of("/permissions", 200),
                Arguments.of("/permissions/1", 200),
                Arguments.of("/certificates", 200),
                Arguments.of("/certificates/1", 403),
                Arguments.of("/files/1", 200),
                Arguments.of("/statistics", 200),
                Arguments.of("/statistics/users", 200),
                Arguments.of("/statistics/organisations", 200),
                Arguments.of("/statistics/organisations/1", 200),
                Arguments.of("/statistics/proposals", 200),
                Arguments.of("/statistics/services", 200),
                Arguments.of("/statistics/services/1", 200),
                Arguments.of("/statistics/packages", 200),
                Arguments.of("/statistics/packages/1", 200),
                Arguments.of("/statistics/permissions", 200),
                Arguments.of("/statistics/certificates", 200),
                Arguments.of("/statistics/files", 200)
        );
    }
}
