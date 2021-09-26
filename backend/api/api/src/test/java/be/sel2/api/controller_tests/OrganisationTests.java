package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import be.sel2.api.entities.Organisation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A testing class for all `/organisations` paths
 */
class OrganisationTests extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    /**
     * Gets a list of existing organisations
     */
    @Test
    @Order(1)
    void getOrganisationList() throws Exception {
        String uri = "/organisations";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("\"_embedded\":"), "Returned type should be CollectionModel");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
        assertTrue(content.contains("\"total\":"), "The total element count should be included");
        assertTrue(content.contains("\"count\":"), "The current element count should be included");
    }

    /**
     * Creates a new organisation
     */
    @Test
    void createOrganisation() throws Exception {
        String uri = "/organisations";
        Map<String, Object> organisation = new HashMap<>();
        organisation.put("organisationName", "Newly created organisation");
        organisation.put("kboNumber", "1859634795");
        organisation.put("ovoCode", "OVO165697");
        organisation.put("nisNumber", "07653");
        organisation.put("serviceProvider", "google");
        String inputJson = super.mapToJson(organisation);

        //Call POST with body `inputJson`
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Organisation result = super.mapFromJson(content, Organisation.class);
        assertEquals(organisation.get("organisationName"), result.getOrganisationName());
    }

    /**
     * Gets a single existing organisation
     */
    @Test
    void getSingleOrganisation() throws Exception {
        String uri = "/organisations/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * Attempts to get invalid organisation
     */
    @Test
    void failGetOrganisation() throws Exception {
        String uri = "/organisations/999999999999999999";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find organisation with ID 999999999999999999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Updates existing organisation
     */
    @Test
    void updateOrganisation() throws Exception {
        String uri = "/organisations/1";

        Organisation organisation = new Organisation();
        organisation.setOrganisationName("Freshly updated organisation");
        String inputJson = super.mapToJson(organisation);

        //Call PATCH with body inputJson
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Organisation result = super.mapFromJson(content, Organisation.class);
        assertEquals(organisation.getOrganisationName(), result.getOrganisationName());
    }

    /**
     * Creates organisation, then deletes it again
     */
    @Test
    void deleteOrganisation() throws Exception {
        String uri = "/organisations";
        Map<String, Object> organisation = new HashMap<>();
        organisation.put("organisationName", "Organisation to be deleted");
        organisation.put("kboNumber", "1845634705");
        organisation.put("ovoCode", "OVO167635");
        organisation.put("nisNumber", "07653");
        organisation.put("serviceProvider", "google");
        String inputJson = super.mapToJson(organisation);

        //Call POST with body inputJson
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Organisation result = super.mapFromJson(content, Organisation.class);

        uri = uri + "/" + result.getId();

        //Call DELETE
        mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        content = mvcResult.getResponse().getContentAsString();
        assertEquals("", content);
    }

    //---------------------------------

    /**
     * Attempts to delete invalid organisation
     */
    @Test
    void deleteInvalidOrganisation() throws Exception {
        String uri = "/organisations/99999";

        //Call DELETE
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find organisation with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Attempts to update a non existing organisation
     */
    @Test
    void updateInvalidOrganisation() throws Exception {
        String uri = "/organisations/99999";

        Organisation organisation = new Organisation();
        organisation.setOrganisationName("Freshly updated organisation");
        String inputJson = super.mapToJson(organisation);

        //Call PATCH with body inputJson
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find organisation with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Attempts to create an organisation with existing codes
     */
    @Test
    void createOrganisationWithExistingCodes() throws Exception {
        String uri = "/organisations/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> existingOrganisation = super.readObjectAsMap(content);

        uri = "/organisations";

        Map<String, Object> organisation = new HashMap<>();
        organisation.put("organisationName", "Some new organisation");
        organisation.put("kboNumber", existingOrganisation.get("kboNumber"));
        organisation.put("ovoCode", existingOrganisation.get("ovoCode"));
        organisation.put("nisNumber", existingOrganisation.get("nisNumber"));
        organisation.put("serviceProvider", "google");
        String inputJson = super.mapToJson(organisation);

        //Call POST with body `inputJson`
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(409, status);
        content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("The organisation already exists"),
                "Actual content: '" + content + "'");
    }

    //---------------------------------------

    /**
     * Tests GET to path `/users/{id}/organisations`
     */
    @Test
    void getUserOrganisations() throws Exception {
        String uri = "/users/1/organisations";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("\"_embedded\":"), "Returned type should be CollectionModel");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
        assertTrue(content.contains("\"total\":"), "The total element count should be included");
        assertTrue(content.contains("\"count\":"), "The current element count should be included");
    }

    /**
     * Attempts to get organisations for a non existing user
     */
    @Test
    void getInvalidUserOrganisations() throws Exception {
        String uri = "/users/99999/organisations";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find user with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Tries to query users for all of a certain organisation
     */
    @Test
    void queryUsersOfOrganisation() throws Exception {
        String organisationUri = "/organisations/1";
        String userUri = "/users";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(organisationUri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Organisation result = super.mapFromJson(content, Organisation.class);

        String organisationName = result.getOrganisationName();

        //Call GET with query
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(userUri)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("organisation", organisationName)).andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> userList = super.readObjectAsMap(content);

        assertNotEquals(0, userList.get("total"), "There should be at least one member of this organisation");

        //Call GET with gibberish query
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(userUri)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("organisation", "feuiaupfp8543") //No organisation should match this
        ).andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        content = mvcResult.getResponse().getContentAsString();

        userList = super.readObjectAsMap(content);

        assertEquals(0, userList.get("total"), "There should be no members of this organisation");
    }
}
