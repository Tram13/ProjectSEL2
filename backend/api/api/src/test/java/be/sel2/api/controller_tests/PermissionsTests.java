package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import be.sel2.api.entities.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for all `/permissions` paths
 */
class PermissionsTests extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    /**
     * Gets list of existing permissions
     */
    @Test
    @Order(1)
    void getPermissionList() throws Exception {
        String uri = "/permissions";

        //Call get
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("\"_embedded\":"), "Returned type should be CollectionModel");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * Create new permission
     */
    @Test
    void createPermission() throws Exception {
        String uri = "/permissions";
        Map<String, Object> perm = new HashMap<>();
        perm.put("name", "Newly created permission");
        perm.put("description", "descr");
        perm.put("code", "RS194856");
        perm.put("link", "http://www.google.be");
        perm.put("proof", 1L);
        perm.put("organisationId", 1L);

        String inputJson = super.mapToJson(perm);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        //assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
        Permission result = super.mapFromJson(content, Permission.class);
        assertEquals(perm.get("name"), result.getName());
    }

    /**
     * Get a single permission
     */
    @Test
    void getSinglePermission() throws Exception {
        String uri = "/permissions/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * Attempt getting an invalid permission
     */
    @Test
    void failGetPermission() throws Exception {
        String uri = "/permissions/999999999999999999";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find permission with ID 999999999999999999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Updates existing permission
     */
    @Test
    void updatePermission() throws Exception {
        String uri = "/permissions/1";

        Permission perm = new Permission();
        perm.setName("Freshly updated permission");
        String inputJson = super.mapToJson(perm);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Permission result = super.mapFromJson(content, Permission.class);
        assertEquals(perm.getName(), result.getName());
    }

    /**
     * Creates then deletes permission
     */
    @Test
    void deletePermission() throws Exception {
        String uri = "/permissions";

        Map<String, Object> perm = new HashMap<>();
        perm.put("name", "Permission to be deleted");
        perm.put("description", "descr");
        perm.put("code", "RS194856");
        perm.put("link", "http://www.google.be");
        perm.put("proof", 1L);
        perm.put("organisationId", 1L);
        String inputJson = super.mapToJson(perm);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Permission result = super.mapFromJson(content, Permission.class);

        uri = uri + "/" + result.getId();

        //Call DELETE
        mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        content = mvcResult.getResponse().getContentAsString();
        assertEquals("", content);
    }
}
