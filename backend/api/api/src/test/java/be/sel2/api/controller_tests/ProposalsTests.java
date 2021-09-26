package be.sel2.api.controller_tests;

import be.sel2.api.AbstractMailTest;
import be.sel2.api.dtos.ProposalDTO;
import be.sel2.api.entities.Proposal;
import be.sel2.api.util.InputValidator;
import be.sel2.api.util.mails.LocaliseUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;
import java.util.stream.Stream;

import static be.sel2.api.WiserAssertions.assertReceivedMessage;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for all `/proposals` paths
 */
class ProposalsTests extends AbstractMailTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Override
    @AfterEach
    protected void cleanup() {
        super.cleanup();
    }

    /**
     * Gets list of existing proposals
     */
    @Test
    @Order(1)
    void getProposalList() throws Exception {
        String uri = "/proposals";

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
     * Gets list of existing proposals from a organisation
     */
    @Test
    @Order(1)
    void getOrganisationProposalList() throws Exception {
        String uri = "/proposals";

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
     * Add proposal to organisation
     */
    @ParameterizedTest
    @MethodSource("providePostData")
    void createProposal(Map<String, Object> proposal, int expectedStatus, String expectedFailure) throws Exception {
        String uri = "/proposals";

        String inputJson = super.mapToJson(proposal);

        // For debug purposes
        System.out.println(inputJson);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();

        String content = mvcResult.getResponse().getContentAsString();

        System.out.println(content);
        assertEquals(expectedStatus, status);


        if (expectedStatus < 300) {
            Proposal result = super.mapFromJson(content, ProposalDTO.class).getEntity();
            assertEquals(proposal.get("name"), result.getName());

            assertEquals(proposal.getOrDefault("status", Proposal.ProposalStatus.DRAFT), result.getStatus());
        } else {
            assertTrue(content.contains(expectedFailure),
                    "Actual content: '" + content + "'");
        }
    }

    /**
     * Provides a series of test data to check various options in post
     */
    private static Stream<Arguments> providePostData() {
        String dateBefore = "2021-03-09";
        String dateAfter = "2021-08-30";

        //Creates proposal and fills in some fields
        Map<String, Object> completeProposal = new HashMap<>();
        completeProposal.put("organisationId", 1L);
        completeProposal.put("name", "Newly created proposal");

        //Samples incomplete proposal
        Map<String, Object> incompleteProposal = new HashMap<>(completeProposal);

        completeProposal.put("deadline", dateBefore);
        completeProposal.put("legalDeadline", dateBefore);
        completeProposal.put("tiDeadline", dateBefore);

        completeProposal.put("businessContext", "new business context");
        completeProposal.put("legalContext", "new legal context");
        completeProposal.put("functionalSetup", "new functional setup");
        completeProposal.put("technicalSetup", "new technical setup");

        //Creates a proposal with an invalid contact
        Map<String, Object> invalidContactProposal = new HashMap<>(completeProposal);
        List<Map<String, Object>> invalidContactList = new ArrayList<>();
        Map<String, Object> invalidContact = new HashMap<>();
        invalidContact.put("contactId", 99999L);
        invalidContact.put("role", "submitter");
        invalidContactList.add(invalidContact);
        invalidContactProposal.put("contacts", invalidContactList);

        //Creates a proposal with an invalid service
        Map<String, Object> invalidServiceProposal = new HashMap<>(completeProposal);
        List<Map<String, Object>> invalidServiceList = new ArrayList<>();
        Map<String, Object> invalidService = new HashMap<>();
        invalidService.put("serviceId", 99999L);
        invalidService.put("source", "ipex");
        invalidService.put("deliveryMethod", "FTP");
        invalidServiceList.add(invalidService);
        invalidServiceProposal.put("services", invalidServiceList);

        //Creates a proposal with an invalid package
        Map<String, Object> invalidPackageProposal = new HashMap<>(completeProposal);
        List<Map<String, Object>> invalidPackageList = new ArrayList<>();
        Map<String, Object> invalidPackage = new HashMap<>();
        invalidPackage.put("packageId", 99999L);
        invalidPackageList.add(invalidPackage);
        invalidPackageProposal.put("packages", invalidPackageList);

        completeProposal.put("contacts", new ArrayList<>());
        completeProposal.put("services", new ArrayList<>());
        completeProposal.put("packages", new ArrayList<>());
        completeProposal.put("status", Proposal.ProposalStatus.IN_REVIEW);

        Map<String, Object> invalidDateProposal = new HashMap<>(completeProposal);
        invalidDateProposal.put("tiDeadline", dateAfter);
        invalidDateProposal.put("deadline", dateBefore);

        Map<String, Object> tiDeadlineNullProposal = new HashMap<>(completeProposal);
        tiDeadlineNullProposal.remove("tiDeadline");

        Map<String, Object> deadlineNullProposal = new HashMap<>(completeProposal);
        deadlineNullProposal.remove("deadline");


        return Stream.of(
                Arguments.of(completeProposal, 201, ""),
                Arguments.of(incompleteProposal, 201, ""),
                Arguments.of(invalidServiceProposal, 404, "Could not find service with ID 99999"),
                Arguments.of(invalidContactProposal, 404, "Could not find contact with ID 99999"),
                Arguments.of(invalidPackageProposal, 404, "Could not find package with ID 99999"),
                Arguments.of(invalidDateProposal, 400, InputValidator.INVALID_DATE_ORDER),
                Arguments.of(tiDeadlineNullProposal, 201, ""),
                Arguments.of(deadlineNullProposal, 201, "")
        );
    }

    /**
     * Gets a single proposal
     */
    @Test
    void getSingleProposal() throws Exception {
        String uri = "/proposals/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * Attempt getting an invalid proposal
     */
    @Test
    void failGetProposal() throws Exception {
        String uri = "/proposals/999999999999999999";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find proposal with ID 999999999999999999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Update existing proposal
     */
    @ParameterizedTest
    @MethodSource("providePatchData")
    void updateProposal(String variableName, String variable) throws Exception {
        String uri = "/proposals/1";

        Map<String, String> prop = new HashMap<>();
        prop.put(variableName, variable);
        String inputJson = super.mapToJson(prop);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Map<String, Object> result = super.readObjectAsMap(content);

        assertEquals(prop.get(variableName), result.get(variableName));
    }

    /**
     * Provides a series of test data to check the patch of each parameter on their own
     */
    private static Stream<Arguments> providePatchData() {
        int year = new GregorianCalendar().get(Calendar.YEAR) + 5;
        return Stream.of(
                Arguments.of("name", "Freshly updated proposal"),
                Arguments.of("status", Proposal.ProposalStatus.PENDING_FEEDBACK.toString().toLowerCase()),
                Arguments.of("deadline", "" + year + "-12-30"),
                Arguments.of("legalDeadline", "2021-03-30"),
                Arguments.of("businessContext", "updated business context"),
                Arguments.of("legalContext", "updated legal context"),
                Arguments.of("functionalSetup", "updated functional setup"),
                Arguments.of("technicalSetup", "updated technical setup")
        );
    }

    /**
     * Update deadlines in Proposal
     */
    @ParameterizedTest
    @MethodSource("provideDeadlinesPatchData")
    void updateProposalDeadlines(String tiDeadline, String deadline, int expectedStatus) throws Exception {
        String uri = "/proposals/1";

        Map<String, String> prop = new HashMap<>();
        if (tiDeadline != null) {
            prop.put("tiDeadline", tiDeadline);
        }
        if (deadline != null) {
            prop.put("deadline", deadline);
        }
        String inputJson = super.mapToJson(prop);

        // Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(expectedStatus, status);
    }

    /**
     * Provides a series of test deadlines to check if patch still respects deadline ordering
     */
    private static Stream<Arguments> provideDeadlinesPatchData() {
        String dateBefore = "2021-03-09";
        String dateAfter = "2021-08-30";
        return Stream.of(
                Arguments.of(null, null, 200),
                Arguments.of(dateBefore, null, 200),
                Arguments.of(null, dateAfter, 200),
                Arguments.of(dateBefore, dateAfter, 200),
                Arguments.of(dateBefore, dateBefore, 200),
                Arguments.of(dateAfter, null, 400), // tiDeadline after (preset) deadline
                Arguments.of(dateAfter, dateAfter, 200),
                Arguments.of(null, dateBefore, 400), // deadline before (preset) tiDeadline
                Arguments.of(dateAfter, dateBefore, 400) // tiDeadline after deadline -> Should return 400 BAD REQUEST
        );
    }

    /**
     * Create then delete proposal
     */
    @Test
    void deleteProposal() throws Exception {
        String uri = "/proposals";

        Map<String, Object> proposal = new HashMap<>();
        proposal.put("name", "Proposal to be deleted");
        proposal.put("status", Proposal.ProposalStatus.PENDING_FEEDBACK);
        proposal.put("deadline", "2021-03-09");
        proposal.put("legalDeadline", "2021-03-09");
        proposal.put("businessContext", "new business context");
        proposal.put("legalContext", "new legal context");
        proposal.put("functionalSetup", "new functional setup");
        proposal.put("technicalSetup", "new technical setup");
        proposal.put("contacts", new ArrayList<>());
        proposal.put("services", new ArrayList<>());
        proposal.put("packages", new ArrayList<>());

        proposal.put("organisationId", 1L);

        String inputJson = super.mapToJson(proposal);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> result = super.readObjectAsMap(content);

        uri = uri + "/" + result.get("id");

        //Call DELETE
        mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        content = mvcResult.getResponse().getContentAsString();
        assertEquals("", content);
    }

    //-----------------------------------

    @ParameterizedTest
    @MethodSource("provideForGetProposalListForOrganisation")
    void getProposalListForOrganisation(Long id, int expectedStatus) throws Exception {
        String uri = "/proposals";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("organisationId", id.toString())).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(expectedStatus, status);
        String content = mvcResult.getResponse().getContentAsString();

        if (expectedStatus < 300) {
            Map<String, Object> queriedProposals = super.readObjectAsMap(content);
            assertNotEquals(0, queriedProposals.get("total"));
        } else {
            assertTrue(content.contains("Could not find organisation with ID 99999"),
                    "Actual content: '" + content + "'");
        }
    }

    private static Stream<Arguments> provideForGetProposalListForOrganisation() {
        return Stream.of(
                Arguments.of(1L, 200),
                Arguments.of(99999L, 404)
        );
    }

    /**
     * Attempts to delete a non existing proposal
     */
    @Test
    void deleteInvalidProposal() throws Exception {
        String uri = "/proposals/99999";

        //Call DELETE
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("Could not find proposal with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Attempts to update a non existing proposal
     */
    @Test
    void updateInvalidProposal() throws Exception {
        String uri = "/proposals/99999";

        Proposal prop = new Proposal();
        prop.setName("Freshly updated proposal");
        String inputJson = super.mapToJson(prop);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find proposal with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Tests whether a mail is sent to the proposals contacts upon updating the status
     */
    @Test
    void updateStatusSendsMail() throws Exception {
        String uri = "/proposals/1";

        //Call GET
        MvcResult getResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        String content = getResult.getResponse().getContentAsString();

        Map<String, Object> old = super.readObjectAsMap(content);

        String status = (String) old.get("status");

        Map<String, String> patchBody;
        String expectedStatus;
        if ("accepted".equals(status)) {
            patchBody = Map.of("status", "denied");
            expectedStatus = LocaliseUtil.localize(Proposal.ProposalStatus.DENIED);
        } else {
            patchBody = Map.of("status", "accepted");
            expectedStatus = LocaliseUtil.localize(Proposal.ProposalStatus.ACCEPTED);
        }

        String inputJson = super.mapToJson(patchBody);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int messageStatus = mvcResult.getResponse().getStatus();
        assertEquals(200, messageStatus);

        assertReceivedMessage(wiser)
                .to("jhon@dupont.be")
                .withSubjectMatch("^MAGDA melding .*$")
                .withSubjectMatch("^.*\\[" + old.get("name") + "\\]$")
                .withContentMatch("(?s).+" + old.get("name") + ".+")
                .withContentMatch("(?s).+" + expectedStatus + ".+");
    }

    /**
     * Search for proposals with deadline before Date
     */
    @Test
    void getFilteredProposalList() throws Exception {
        String uri = "/proposals";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .queryParam("deadlineBefore", "1999-5-11") // Should return no elements
                .queryParam("legalDeadlineBefore", "1999-5-11")
                .queryParam("tiDeadlineBefore", "1999-5-11")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("\"total\":0"), "Collection should be empty");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");

        int year = new GregorianCalendar().get(Calendar.YEAR) + 1001;

        //Call GET
        MvcResult mvcResult2 = mvc.perform(MockMvcRequestBuilders.get(uri)
                .queryParam("deadlineBefore", "" + year + "-5-11") // Should return all elements
                .queryParam("legalDeadlineBefore", "" + year + "-5-11")
                .queryParam("tiDeadlineBefore", "" + year + "-5-11")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertEquals(200, mvcResult2.getResponse().getStatus());

        assertNotEquals(content, mvcResult2.getResponse().getContentAsString()); // Result should be different
    }

}
