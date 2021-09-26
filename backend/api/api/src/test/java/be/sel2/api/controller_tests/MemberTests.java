package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import be.sel2.api.entities.Organisation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A testing class for member endpoints `/organisations/{organisationId}/members`
 */
class MemberTests extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    /**
     * Tests POST, GET and DELETE to path `/organisations/{organisationId}/members`
     */
    @Test
    void addAndRemoveMember() throws Exception {
        String uri = "/organisations";
        Map<String, Object> organisation = new HashMap<>();
        organisation.put("organisationName", "Organisation to be deleted");
        organisation.put("kboNumber", "0222222222");
        organisation.put("ovoCode", "OVO666666");
        organisation.put("nisNumber", "55555");
        organisation.put("serviceProvider", "google");
        String inputJson = super.mapToJson(organisation);

        //Call POST with body inputJson, to create Organisation
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Organisation result = super.mapFromJson(content, Organisation.class);

        uri = "/organisations/" + result.getId() + "/members";

        Map<String, Object> member = new HashMap<>();
        member.put("role", "member");
        member.put("email", "ambethie@artevelde3.be");

        inputJson = super.mapToJson(member);

        //Call POST, to add member to organisation
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> parsedJson = readObjectAsMap(content);

        assertEquals("member", parsedJson.get("role"));
        assertTrue(parsedJson.get("user") instanceof Map);
        assertEquals(1, ((Map<?, ?>)parsedJson.get("user")).get("id"));

        Integer userId = (Integer) ((Map<?, ?>)parsedJson.get("user")).get("id");

        //Call GET, to get all member relations
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        content = mvcResult.getResponse().getContentAsString();

        parsedJson = readObjectAsMap(content);
        assertTrue(parsedJson.containsKey("_links"));
        assertTrue(parsedJson.containsKey("_embedded"));
        assertEquals(1, parsedJson.get("total"));

        //Call GET, to get one member relation
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        content = mvcResult.getResponse().getContentAsString();

        parsedJson = readObjectAsMap(content);

        assertEquals(userId, ((Map<?, ?>)parsedJson.get("user")).get("id"));
        assertEquals("member", parsedJson.get("role"));
        assertTrue(parsedJson.get("user") instanceof Map);
        assertEquals(1, ((Map<?, ?>)parsedJson.get("user")).get("id"));

        mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        content = mvcResult.getResponse().getContentAsString();
        System.out.println(content);
        assertEquals("", content);
    }


    /**
     * Attempts to add a user to an organisation they are already part of
     * Attempts to add a user to a non existing organisation
     * Attempts to add non existing user to an organisation
     */
    @ParameterizedTest
    @MethodSource("provideForAddToInvalidOrganisation")
    void addToInvalidOrganisation(String uri, String email, int expectedStatus, String expectedContent) throws Exception {

        Map<String, Object> member = new HashMap<>();
        member.put("role", "member");
        member.put("email", email);

        String inputJson = super.mapToJson(member);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(expectedStatus, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains(expectedContent),
                "Actual content: '" + content + "'");
    }

    /**
     * Provides parameters for previous test
     *
     * @return {@link Stream} of parameters
     */
    private static Stream<Arguments> provideForAddToInvalidOrganisation() {
        return Stream.of(
                Arguments.of("/organisations/99999/members", "ambethie@artevelde3.be", 404, "Could not find organisation with ID 99999"),
                Arguments.of("/organisations/1/members", "ambethie@artevelde3.be", 409, "User is already an employee of this organisation"),
                Arguments.of("/organisations/1/members", "email.bestaatniet@lol.com", 404, "Could not find user with field email equal to email.bestaatniet@lol.com")
        );
    }

    /**
     * Attempts to remove a user from an organisation they are not part of
     * Attempts to remove a non existing user from an organisation
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUrls")
    void incorrectlyRemoveFromOrganisation(String uri, int expectedStatus, String expectedContent) throws Exception {

        //Call DELETE
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(expectedStatus, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains(expectedContent),
                "Actual content: '" + content + "'");
    }

    /**
     * Attempts to get a user from an organisation they are not part of
     * Attempts to get a non existing user from an organisation
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUrls")
    void attemptGetUser(String uri, int expectedStatus, String expectedContent) throws Exception {

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(expectedStatus, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains(expectedContent),
                "Actual content: '" + content + "'");
    }

    /**
     * Attempts to update a member from an organisation they are not part of
     * Attempts to update a non existing member from an organisation
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUrls")
    void attemptPatchUser(String uri, int expectedStatus, String expectedContent) throws Exception {

        Map<String, String> input = new HashMap<>();
        input.put("role", "manager");

        String inputJson = super.mapToJson(input);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(expectedStatus, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains(expectedContent),
                "Actual content: '" + content + "'");
    }

    /**
     * Provides parameters for previous test
     *
     * @return {@link Stream} of parameters
     */
    private static Stream<Arguments> provideInvalidUrls() {
        return Stream.of(
                Arguments.of("/organisations/99999/members/1", 404, "Could not find organisation with ID 99999"),
                Arguments.of("/organisations/1/members/99999", 404, "Could not find member with ID 99999")
        );
    }
}
