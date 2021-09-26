package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import be.sel2.api.entities.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for all `/services` paths
 */
class ServiceTests extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    /**
     * Gets list of existing services
     */
    @Test
    @Order(1)
    void getServiceList() throws Exception {
        String uri = "/services";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("\"_embedded\":"), "Returned type should be CollectionModel");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * Create new service
     */
    @Test
    void createService() throws Exception {
        String uri = "/services";
        Map<String, Object> serv = new HashMap<>();
        serv.put("name", "Newly created service");
        serv.put("domain", "this is a domain");
        serv.put("description", "this is a description");
        serv.put("sources", new ArrayList<>());
        serv.put("deliveryMethods", new ArrayList<>());
        serv.put("needsPermissions", true);

        String inputJson = super.mapToJson(serv);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Service result = super.mapFromJson(content, Service.class);
        assertEquals(serv.get("name"), result.getName());
    }

    /**
     * Gets existing service
     */
    @Test
    void getSingleService() throws Exception {
        String uri = "/services/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
        try {
            super.mapFromJson(content, Service.class);
        } catch (Exception ex) {
            fail("Could not receive user with id 1");
        }
    }

    /**
     * Attempts getting invalid service
     */
    @Test
    void failGetService() throws Exception {
        String uri = "/services/999999999999999999";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find service with ID 999999999999999999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Updates an existing service
     */
    @ParameterizedTest
    @MethodSource("providePatchData")
    void updateService(String variableName, Object variable) throws Exception {
        String uri = "/services/1";

        Map<String, Object> service = new HashMap<>();
        service.put(variableName, variable);
        String inputJson = super.mapToJson(service);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String content = mvcResult.getResponse().getContentAsString();
        Map<String, Object> result = super.readObjectAsMap(content);
        assertEquals(variable, result.get(variableName));
    }

    /**
     * Provides a series of test data to check the patch of each parameter on their own
     */
    private static Stream<Arguments> providePatchData() {
        return Stream.of(
                Arguments.of("name", "Freshly updated service"),
                Arguments.of("domain", "selab2-g2"),
                Arguments.of("description", "A newly updated description for a newly updated service"),
                Arguments.of("needsPermissions", true),
                Arguments.of("needsPermissions", false)
        );
    }

    /**
     * Creates then deletes service
     */
    @Test
    void deleteService() throws Exception {
        String uri = "/services";
        Map<String, Object> serv = new HashMap<>();
        serv.put("name", "Newly created service");
        serv.put("domain", "this is a domain");
        serv.put("description", "this is a description");
        serv.put("sources", new ArrayList<>());
        serv.put("deliveryMethods", new ArrayList<>());
        serv.put("needsPermissions", true);
        String inputJson = super.mapToJson(serv);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Service result = super.mapFromJson(content, Service.class);

        uri = uri + "/" + result.getId();

        //Call DELETE
        mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        content = mvcResult.getResponse().getContentAsString();
        assertEquals("", content);
    }
}
