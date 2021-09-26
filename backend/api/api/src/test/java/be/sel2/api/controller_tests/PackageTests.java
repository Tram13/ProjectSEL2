package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import be.sel2.api.entities.Package;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A testing class for each `/packages` path
 */
class PackageTests extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    /**
     * Gets a list of existing packages
     */
    @Test
    @Order(1)
    void getPackageList() throws Exception {
        String uri = "/packages";

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
     * Creates a new package
     */
    @ParameterizedTest
    @MethodSource("providePostData")
    void createPackage(Map<String, Object> pack, int expectedServiceCount) throws Exception {
        String uri = "/packages";


        String inputJson = super.mapToJson(pack);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> result = super.readObjectAsMap(content);
        assertEquals(pack.get("name"), result.get("name"));
        assertTrue(result.get("services") instanceof List);
        assertEquals(expectedServiceCount, ((List<?>)result.get("services")).size());
    }

    /**
     * Provides a series of test data to check various options in post
     */
    private static Stream<Arguments> providePostData() {

        Map<String, Object> pack1 = new HashMap<>();
        pack1.put("name", "Newly created package");
        pack1.put("services", new ArrayList<>());

        Map<String, Object> pack2 = new HashMap<>();
        pack2.put("name", "Newly created package with no services");

        Map<String, Object> pack3 = new HashMap<>();
        pack3.put("name", "Newly created package with services");
        List<Map<String, Object>> services = new ArrayList<>();
        Map<String, Object> validService = new HashMap<>();
        validService.put("source", "AIV");
        validService.put("deliveryMethod", "FTP");
        validService.put("serviceId", 1L);
        services.add(validService);
        pack3.put("services", services);

        return Stream.of(
                Arguments.of(pack1, 0),
                Arguments.of(pack2, 0),
                Arguments.of(pack3, 1)
        );

    }

    /**
     * Get a single existing package
     */
    @Test
    void getSinglePackage() throws Exception {
        String uri = "/packages/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * Attempt to get an invalid package
     */
    @Test
    void failGetPackage() throws Exception {
        String uri = "/packages/999999999999999999";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find package with ID 999999999999999999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Updates existing package
     */
    @Test
    void updatePackage() throws Exception {
        String uri = "/packages/1";

        Package pack = new Package();
        pack.setName("Freshly updated package");

        String inputJson = super.mapToJson(pack);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Package result = super.mapFromJson(content, Package.class);
        assertEquals(pack.getName(), result.getName());
    }

    /**
     * Creates then deletes package
     */
    @Test
    void deletePackage() throws Exception {
        String uri = "/packages";
        Map<String, Object> pack = new HashMap<>();
        pack.put("name", "Package to be deleted");
        pack.put("services", new ArrayList<>());
        String inputJson = super.mapToJson(pack);

        //Call post
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Package result = super.mapFromJson(content, Package.class);

        uri = uri + "/" + result.getId();

        //Call DELETE
        mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        content = mvcResult.getResponse().getContentAsString();
        assertEquals("", content);
    }

    /**
     * Creates a new package with an invalid service
     */
    @Test
    void incorrectlyCreatePackage() throws Exception {
        String uri = "/packages";
        Map<String, Object> pack = new HashMap<>();
        pack.put("name", "Newly created package");
        List<Map<String, Object>> services = new ArrayList<>();
        Map<String, Object> validService = new HashMap<>();
        validService.put("source", "AIV");
        validService.put("deliveryMethod", "FTP");
        validService.put("serviceId", 1L);
        Map<String, Object> invalidService = new HashMap<>();
        invalidService.put("source", "AIV");
        invalidService.put("deliveryMethod", "FTP");
        invalidService.put("serviceId", 999999L);
        services.add(validService);
        services.add(invalidService);
        pack.put("services", services);

        String inputJson = super.mapToJson(pack);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find service with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Updates existing package with invalid service
     */
    @Test
    void incorrectlyUpdatePackage() throws Exception {
        String uri = "/packages/1";

        Map<String, Object> pack = new HashMap<>();
        List<Map<String, Object>> services = new ArrayList<>();
        Map<String, Object> invalidService = new HashMap<>();
        invalidService.put("source", "AIV");
        invalidService.put("deliveryMethod", "FTP");
        invalidService.put("serviceId", 99999L);
        services.add(invalidService);
        pack.put("services", services);
        String inputJson = super.mapToJson(pack);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find service with ID 99999"),
                "Actual content: '" + content + "'");
    }

    @Test
    void getTestPackageServices() throws Exception {
        String uri = "/packages/1/services";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());

        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> res = super.readObjectAsMap(content);

        assertTrue(res.containsKey("total"));
        assertTrue(res.containsKey("count"));
        assertTrue(res.containsKey("_links"));
        assertTrue(res.containsKey("_embedded"));
    }
}
