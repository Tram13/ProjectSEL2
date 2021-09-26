package be.sel2.api.serialization_tests;

import be.sel2.api.entities.Contact;
import be.sel2.api.entities.Organisation;
import be.sel2.api.entities.Package;
import be.sel2.api.entities.Proposal;
import be.sel2.api.entities.relations.ContactProposal;
import be.sel2.api.entities.relations.PackageService;
import be.sel2.api.entities.relations.ProposalService;
import be.sel2.api.entities.Service;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.models.BooleanModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SerializationTests { //Not extending abstract test: no need to start server

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializePackage() throws JsonProcessingException {
        Package serializablePackage = new Package();
        serializablePackage.setId(76L);
        serializablePackage.setName("Test name");

        Set<Service> serviceSet = new HashSet<>();
        Service service1 = new Service();
        service1.setId(137L);
        serviceSet.add(service1);

        Service service2 = new Service();
        service2.setId(7614L);
        serviceSet.add(service2);

        serializablePackage.setServices(serviceSet
                .stream().map(s -> new PackageService(serializablePackage, s, "Somewhere", Service.DeliveryMethod.WEBSERVICE))
                .collect(Collectors.toSet()));

        String json = objectMapper.writeValueAsString(serializablePackage);

        assertTrue(json.contains("\"id\":" + serializablePackage.getId()));
        assertTrue(json.contains("\"name\":\"" + serializablePackage.getName() + '"'));

        assertTrue(json.contains("\"services\":["));
        int startListLoc = json.indexOf('[');
        int endListLoc = json.indexOf(']', startListLoc);

        String serviceList = json.substring(startListLoc, endListLoc + 1);

        System.out.println(serviceList);
        assertTrue(serviceList.matches(".*\"href\":\".*/services/" + service1.getId() + "\".*"));
        assertTrue(serviceList.matches(".*\"href\":\".*/services/" + service2.getId() + "\".*"));
    }

    @Test
    void serializeContactProposal() throws JsonProcessingException {
        ContactProposal contactProposal = new ContactProposal();

        String contactRoleString = "technical";
        contactProposal.setRole(ContactProposal.Contactrole.fromString(contactRoleString));
        Contact contact = new Contact();
        contact.setId(76L);
        Proposal proposal = new Proposal();
        proposal.setId(796L);
        Organisation organisation = new Organisation();
        organisation.setId(24L);
        proposal.setOrganisation(organisation);
        contact.setOrganisation(organisation);
        contactProposal.setContact(contact);
        contactProposal.setProposal(proposal);

        String json = objectMapper.writeValueAsString(contactProposal);

        assertTrue(json.contains("\"role\":\"" + contactRoleString + '"'));
        assertTrue(json.matches(".*\"href\":\".*/organisations/" +
                organisation.getId() + "/contacts/" + contact.getId() + "\".*"));
    }

    @Test
    void serializeProposalService() throws JsonProcessingException {
        ProposalService proposalService = new ProposalService();

        String deliveryMethodString = "FTP";
        proposalService.setDeliveryMethod(Service.DeliveryMethod.fromString(deliveryMethodString));
        proposalService.setSource("Test source");

        Service service = new Service();
        service.setId(76L);
        Proposal proposal = new Proposal();
        proposal.setId(796L);
        proposalService.setService(service);
        proposalService.setProposal(proposal);

        String json = objectMapper.writeValueAsString(proposalService);
        assertTrue(json.contains("\"deliveryMethod\":\"" + deliveryMethodString + '"'));
        assertTrue(json.contains("\"source\":\"" + proposalService.getSource() + '"'));

        assertTrue(json.matches(".*\"href\":\".*/services/" + proposalService.getService().getId() + "\".*"));
    }

    @Test
    void noPasswordInUserJson() throws JsonProcessingException {
        UserInfo user = new UserInfo();
        user.setPassword("ItsASecretToEverybody");

        String json = objectMapper.writeValueAsString(user);

        assertFalse(json.contains(user.getPassword()));
    }

    @ParameterizedTest
    @MethodSource("provideBooleanModelValues")
    void serializeBooleanModel(String serializedValue, BooleanModel modelValue, String reserializedValue) throws JsonProcessingException {
        BooleanModel model = objectMapper.readValue(serializedValue, BooleanModel.class);

        assertEquals(modelValue, model);

        String serialized = objectMapper.writeValueAsString(model);

        assertEquals(reserializedValue, serialized);
    }

    static Stream<Arguments> provideBooleanModelValues(){
        return Stream.of(
                Arguments.of("true", BooleanModel.TRUE, "true"),
                Arguments.of("false", BooleanModel.FALSE, "false"),
                Arguments.of("null", BooleanModel.NOT_SET, "null")
        );
    }
}
