package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import be.sel2.api.entities.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactTests extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    /**
     * Gets a list of existing contacts
     */
    @Test
    @Order(1)
    void getContacts() throws Exception {
        String uri = "/organisations/1/contacts";

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
     * Creates a new contact
     */
    @Test
    void createContact() throws Exception {
        String uri = "/organisations/1/contacts";
        Map<String, Object> contact = new HashMap<>();
        contact.put("firstName", "Tester");
        contact.put("lastName", "Contact");
        contact.put("email", "contact.mail@live.be");
        contact.put("phoneNumber", "056 66 78 95");
        String inputJson = super.mapToJson(contact);

        //Call POST with body `inputJson`
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Contact result = super.mapFromJson(content, Contact.class);
        assertEquals(contact.get("firstName"), result.getFirstName());
        assertEquals(contact.get("lastName"), result.getLastName());
        assertEquals(contact.get("email"), result.getEmail());
        assertEquals(contact.get("phoneNumber"), result.getPhoneNumber());
    }

    /**
     * Gets a single existing contact
     */
    @Test
    void getSingleContact() throws Exception {
        String uri = "/organisations/1/contacts/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * Gets a single invalid contact
     */
    @Test
    void getInvalidContact() throws Exception {
        String uri = "/organisations/1/contacts/9999";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Could not find contact with ID 9999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Updates existing contact
     */
    @ParameterizedTest
    @MethodSource("providePatchData")
    void updateContact(String variableName, Object variable) throws Exception {
        String uri = "/organisations/1/contacts/1";

        Map<String, Object> contact = new HashMap<>();
        contact.put(variableName, variable);
        String inputJson = super.mapToJson(contact);

        //Call PATCH with body inputJson
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
                Arguments.of("firstName", "Newton"),
                Arguments.of("lastName", "Nackle"),
                Arguments.of("phoneNumber", "056 73 16 76")
        );
    }

    /**
     * Creates contact, then deletes it again
     */
    @Test
    void deleteContact() throws Exception {
        String uri = "/organisations/1/contacts";
        Map<String, Object> contact = new HashMap<>();
        contact.put("firstName", "Ash");
        contact.put("lastName", "Ketchum");
        contact.put("email", "ash.ketchum@kanto.com");
        contact.put("phoneNumber", "056 63 15 73");
        String inputJson = super.mapToJson(contact);

        //Call POST with body `inputJson`
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Contact result = super.mapFromJson(content, Contact.class);

        uri = uri + "/" + result.getId();

        //Call DELETE
        mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        content = mvcResult.getResponse().getContentAsString();
        assertEquals("", content);
    }


    // Invalid organisations

    /**
     * Gets a single contact of an invalid organisation
     */
    @Test
    void attemptGetInvalidContact() throws Exception {
        String uri = "/organisations/99999/contacts/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find organisation with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Gets a list of contacts of an invalid organisation
     */
    @Test
    void attemptGetInvalidContacts() throws Exception {
        String uri = "/organisations/99999/contacts";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find organisation with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Updates invalid contact
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUrls")
    void updateInvalidContact(String uri, String expectedContent) throws Exception {

        Map<String, Object> contact = new HashMap<>();
        contact.put("firstName", "Newton");
        String inputJson = super.mapToJson(contact);

        //Call PATCH with body inputJson
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains(expectedContent),
                "Actual content: '" + content + "'");
    }

    /**
     * Deletes invalid contact
     */
    @ParameterizedTest
    @MethodSource("provideInvalidUrls")
    void deleteInvalidContact(String uri, String expectedContent) throws Exception {

        //Call DELETE
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains(expectedContent),
                "Actual content: '" + content + "'");
    }

    /**
     * Provides arguments for the above tests
     *
     * @return {@link Stream} of arguments
     */
    private static Stream<Arguments> provideInvalidUrls() {
        return Stream.of(
                Arguments.of("/organisations/99999/contacts/1", "Could not find organisation with ID 99999"),
                Arguments.of("/organisations/1/contacts/99999", "Could not find contact with ID 99999")
        );
    }

    //---------------------------------------------------------

    /**
     * Gets a single existing contact
     * and attempts to create a new one with the same email
     */
    @Test
    void createContactWithExistingEmail() throws Exception {
        String uri = "/organisations/1/contacts/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        Contact existingContact = super.mapFromJson(content, Contact.class);

        Map<String, Object> contact = new HashMap<>();

        contact.put("email", existingContact.getEmail());
        contact.put("firstName", "Newton");
        contact.put("lastName", "Contact");
        contact.put("phoneNumber", "056 64 01 70");

        String json = super.mapToJson(contact);

        uri = "/organisations/1/contacts";

        //Call POST
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andReturn();

        status = mvcResult.getResponse().getStatus();

        assertEquals(409, status);
        content = mvcResult.getResponse().getContentAsString();

        assertTrue(content
                .contains("A contact already exists with this email inside this organisation"),
                "Actual content: '" + content + "'");
    }
}
