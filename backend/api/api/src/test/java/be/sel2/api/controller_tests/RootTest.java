package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class RootTest extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    void testRootResponse() throws Exception {
        String uri = "/";

        //Call GET
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


        assertEquals(baseUrl + "organisations", ((Map<?, ?>) links.get("organisations")).get("href"));
        assertEquals(baseUrl + "packages", ((Map<?, ?>) links.get("packages")).get("href"));
        assertEquals(baseUrl + "permissions", ((Map<?, ?>) links.get("permissions")).get("href"));
        assertEquals(baseUrl + "proposals", ((Map<?, ?>) links.get("proposals")).get("href"));
        assertEquals(baseUrl + "services", ((Map<?, ?>) links.get("services")).get("href"));
        assertEquals(baseUrl + "users", ((Map<?, ?>) links.get("users")).get("href"));
        assertEquals(baseUrl + "auth", ((Map<?, ?>) links.get("auth")).get("href"));
        assertEquals(baseUrl + "config", ((Map<?, ?>) links.get("config")).get("href"));
        assertEquals(baseUrl + "certificates", ((Map<?, ?>) links.get("certificates")).get("href"));
    }

    @Test
    void testConfigResponse() throws Exception {
        String uri = "/config";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> result = super.readObjectAsMap(content);

        String userRoleKey = "userRoles";
        String memberRolesKey = "memberRoles";
        String proposalStatusesKey = "proposalStatuses";
        String proposalRolesKey = "proposalRoles";
        String tokenTypesKey = "tokenTypes";

        String enumKey = "enum";

        assertTrue(result.get(userRoleKey) instanceof Map);
        assertTrue(((Map<?, ?>) result.get(userRoleKey)).get(enumKey) instanceof List);
        assertIterableEquals(List.of(
                "customer",
                "employee",
                "admin"),
                (List<?>)((Map<?, ?>) result.get(userRoleKey)).get(enumKey));

        assertTrue(result.get(memberRolesKey) instanceof Map);
        assertTrue(((Map<?, ?>) result.get(memberRolesKey)).get(enumKey) instanceof List);
        assertIterableEquals(List.of(
                "manager",
                "member"),
                (List<?>)((Map<?, ?>) result.get(memberRolesKey)).get(enumKey));

        assertTrue(result.get(proposalRolesKey) instanceof Map);
        assertTrue(((Map<?, ?>) result.get(proposalRolesKey)).get(enumKey) instanceof List);
        assertIterableEquals(List.of(
                "submitter",
                "business",
                "business_backup",
                "technical",
                "technical_backup",
                "safety_consultant",
                "service_provider",
                "responsible_d2d_management_customer",
                "manager_geosecure"),
                (List<?>)((Map<?, ?>) result.get(proposalRolesKey)).get(enumKey));

        assertTrue(result.get(proposalStatusesKey) instanceof Map);
        assertTrue(((Map<?, ?>) result.get(proposalStatusesKey)).get(enumKey) instanceof List);
        assertIterableEquals(List.of(
                "draft",
                "completed",
                "in_review",
                "pending_feedback",
                "accepted",
                "denied",
                "cancelled"),
                (List<?>)((Map<?, ?>) result.get(proposalStatusesKey)).get(enumKey));

        assertTrue(result.get(tokenTypesKey) instanceof Map);
        assertTrue(((Map<?, ?>) result.get(tokenTypesKey)).get(enumKey) instanceof List);
        assertIterableEquals(List.of(
                "refreshToken",
                "sessionToken"),
                (List<?>)((Map<?, ?>) result.get(tokenTypesKey)).get(enumKey));
    }
}
