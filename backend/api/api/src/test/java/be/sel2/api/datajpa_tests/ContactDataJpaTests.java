package be.sel2.api.datajpa_tests;

import be.sel2.api.entities.Contact;
import be.sel2.api.repositories.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // Set the active profile to use application-test.properties
@DataJpaTest
class ContactDataJpaTests {
    @Autowired
    private ContactRepository repo;

    private Contact getDefaultContact() {
        Contact contact = new Contact();
        contact.setFirstName("Jean-Claude");
        contact.setLastName("Van Damme");
        contact.setEmail("jcvd@gmail.com");
        contact.setPhoneNumber("+3292644770");
        return contact;
    }

    @Test
    void checkContactEquals() {
        Contact contact = getDefaultContact();
        repo.save(contact);

        Contact contact2 = new Contact();
        contact2.setFirstName("Gunnar");
        contact2.setLastName("Brinkmann");
        contact2.setEmail("gunnar.brinkmann@ugent.be");
        contact2.setPhoneNumber("092644807");
        repo.save(contact2);
        assertNotEquals(contact, contact2);

        Contact contact3 = getDefaultContact();
        assertNotEquals(contact, contact3); // IDs verschillen
        contact3.setId(contact.getId());
        assertEquals(contact, contact3);

        contact3 = getDefaultContact();
        contact3.setFirstName("Gunnar");
        contact3.setId(contact.getId());
        assertNotEquals(contact, contact3);

        contact3 = getDefaultContact();
        contact3.setLastName("Brinkmann");
        contact3.setId(contact.getId());
        assertNotEquals(contact, contact3);

        contact3 = getDefaultContact();
        contact3.setEmail("gunnar.brinkmann@ugent.be");
        contact3.setId(contact.getId());
        assertNotEquals(contact, contact3);

        contact3 = getDefaultContact();
        contact3.setPhoneNumber("092644807");
        contact3.setId(contact.getId());
        assertNotEquals(contact, contact3);
    }

    @Test
    void checkSaveNewContactToRepository() {
        Contact contact = getDefaultContact();
        assertNull(contact.getId());
        repo.save(contact);
        assertNotNull(contact.getId());
        Contact contactCopy = repo.getOne(contact.getId());
        assertNotNull(contactCopy);
        assertEquals(contact, contactCopy);
    }

    @Test
    void checkSaveUpdateToRepositoryIsEqual() {
        Contact contact = getDefaultContact();
        repo.save(contact);
        contact.setFirstName("Gunnar");
        repo.save(contact);
        Contact contactCopy = repo.getOne(contact.getId());
        assertNotEquals("Jean-Claude", contactCopy.getFirstName());
        assertEquals("Gunnar", contactCopy.getFirstName());
    }

    @Test
    void checkSaveUpdateNotEquals() {
        Contact contact1 = getDefaultContact();
        Contact contact2 = getDefaultContact();
        assertNull(contact1.getId());
        assertNull(contact2.getId());
        assertEquals(contact1, contact2);
        repo.save(contact1);
        repo.save(contact2);
        assertNotEquals(contact1, contact2); // ID is anders
        assertEquals(contact1.getFirstName(), contact2.getFirstName());
        contact1.setFirstName("Gunnar");
        assertNotEquals(contact1.getFirstName(), contact2.getFirstName());
    }

    @Test
    void checkDeleteFromRepositoryById() {
        Contact contact = getDefaultContact();
        repo.save(contact);
        Contact contactCopy = repo.getOne(contact.getId());
        assertNotNull(contactCopy);
        Long id = contact.getId();
        repo.deleteById(id);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(id));
    }

    @Test
    void checkDeleteFromRepositoryByObject() {
        Contact contact = getDefaultContact();
        repo.save(contact);
        Contact contactCopy = repo.getOne(contact.getId());
        assertNotNull(contactCopy);
        Long id = contact.getId();
        repo.delete(contact);
        assertThrows(JpaObjectRetrievalFailureException.class, () -> repo.getOne(id));
    }
}
