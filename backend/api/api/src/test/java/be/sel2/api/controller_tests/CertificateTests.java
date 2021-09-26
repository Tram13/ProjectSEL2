package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests GETTING and DELETING of certificates
 *
 *  WARNING: POST requires extra steps in order to sign the certificate
 */
class CertificateTests extends AbstractTest {
    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    /**
     * Gets list of existing certificates
     */
    @Test
    void getCertificateList() throws Exception {
        String uri = "/certificates";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        int startList = content.indexOf('[');
        int endList = content.indexOf(']', startList);
        assertTrue(startList > 0 && endList > 0, "CertificateList not found or empty");

        assertTrue(content.contains("\"_embedded\":"), "Returned type should be CollectionModel");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * Gets list of existing certificates of an organisation
     */
    @Test
    void getOrganisationCertificateList() throws Exception {
        String uri = "/certificates";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .queryParam("organisationId", "1")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        int startList = content.indexOf('[');
        int endList = content.indexOf(']', startList);
        assertTrue(startList > 0 && endList > 0, "CertificateList not found or empty");

        assertTrue(content.contains("\"_embedded\":"), "Returned type should be CollectionModel");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * Gets a single existing certificate
     */
    @Test
    void getSingleCertificate() throws Exception {
        String uri = "/certificates/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
        assertTrue(content.contains("\"created\""));
        assertTrue(content.contains("\"lastUpdated\""));
        assertTrue(content.contains("/files/1"));
    }

    /**
     * Attempt getting an invalid certificate
     */
    @Test
    void failGetCertificate() throws Exception {
        String uri = "/certificates/999999999999999999";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find certificate with ID 999999999999999999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Tries to update an existing certificate
     */
    @Test
    void updateCertificate() throws Exception {
        String uri = "/certificates/1";

        //Call PATCH
        MvcResult mvcGetResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"file\": 5}")).andReturn();

        assertEquals(405, mvcGetResult.getResponse().getStatus());
    }

    /**
     * Deletes existing certificate
     */
    @Test
    void deleteCertificate() throws Exception {
        String uri = "/certificates/1";

        //Calls DELETE
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals("", content);
    }

    @WithUserDetails(
            value = "william.wallace@freedom.scot",
            userDetailsServiceBeanName = "userService"
    )
    @Test
    void testGetCertificatesAsCustomer() throws Exception {
        String uri = "/certificates";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("\"total\":0"), "A customer without permissions should not see this");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    @WithUserDetails(
            value = "ambethie@artevelde3.be",
            userDetailsServiceBeanName = "userService"
    )
    @Test
    void testGetCertificatesAsMember() throws Exception {
        String uri = "/certificates";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("\"total\":1"), "A manager of the organisation should see this");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    @WithUserDetails(
            value = "paus.urbanus@vatican.com",
            userDetailsServiceBeanName = "userService"
    )
    @Test
    void testGetCertificatesAsOrganisationManager() throws Exception {
        String uri = "/certificates";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("\"total\":1"), "A manager of the organisation should see this");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    @WithUserDetails(
            value = "gandalf.grey@wizard.com",
            userDetailsServiceBeanName = "userService"
    )
    @Test
    void testGetCertificatesAsContact() throws Exception {
        String uri = "/certificates";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("\"total\":1"), "A contact of the proposal should see this");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }
}
