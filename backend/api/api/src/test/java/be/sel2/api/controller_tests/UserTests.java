package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import be.sel2.api.entities.UserInfo;
import be.sel2.api.dtos.UserInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for all `/users` paths
 */
class UserTests extends AbstractTest {
    private final String[] validFirstNames = {
    		"Peter",
    		"Marie-Jeanne",
    		"X &#230 A-12", // html entity
    		"Am&#201lie", // html entity
    		"Hans" // html entity
    };
    private final String[] validLastNames = {
    		"File",
    		"Van Der Linden",
    		"Van Beethoven",
    		"Lagrange",
    		"Gr&#252ber"
    };
    private final String[] validEmails = {
    		"peter.file@hotmail.com",
    		"yxing@icloud.com",
    		"frostman@live.com",
    		"a.a@a.com",
    		"hans.gruber@bratwurst.de"
    };
    private final String[] validPasswords = {
    		"Ips7sdfgN7",
    		"7Ar_dmkl6",
    		"Apmsdf-qs81",
    		"A1aaaaaa",
    		"I_O_I3tr"
    };
    private final String[] invalidNames = {
    		"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    		+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    		+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    		+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    		+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    		+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    		"\n",
    		""
    };
    private final String[] invalidEmails = {
    		"peter.file@hotmail",
    		"peter.filehotmail.com",
    		"peter.file",
    		"yxing",
    		"@live.com",
    		"a@",
    		""
    };
    private final String[] invalidPasswords = {
    		"123456789",
    		"abcdefghijk",
    		"Aar7",
    		"6Ko",
    		"az6d1zf61",
    		"RG6DG51E3",
    		"oSOiojNSOp",
    		"qef6Pds"
    };
    private final String[] fieldNames = {
    		"firstName",
    		"lastName",
    		"email",
    		"password"
    };

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    /**
     * Gets list of existing users
     */
    @Test
    void getUserList() throws Exception {
        String uri = "/users";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        int startList = content.indexOf('[');
        int endList = content.indexOf(']', startList);
        assertTrue(startList > 0 && endList > 0, "UserList not found or empty");


        Map<?,?>[] userList = super.mapFromJson(content.substring(startList, endList + 1), Map[].class);

        assertTrue(userList.length > 0);

        for (Map<?,?> info : userList) {
            assertFalse(info.containsKey("password"), "Should not include password in response");
        }

        assertTrue(content.contains("\"_embedded\":"), "Returned type should be CollectionModel");
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
    }

    /**
     * test valid user
     */
    private void createUserValidTestCase(String firstName, String lastName, String email, String password) throws Exception {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("role", UserInfo.Userrole.CUSTOMER);
        user.put("password", password);
        String inputJson = super.mapToJson(user);
        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();

        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();

        UserInfo result = super.mapFromJson(content, UserInfoDTO.class).getEntity();
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(email, result.getEmail());
        assertEquals(user.get("role"), result.getRole());
    }

    /**
     * test invalid user
     */
    private void createUserInvalidTestCase(String firstName, String lastName, String email, String password) throws Exception {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("role", UserInfo.Userrole.CUSTOMER);
        user.put("password", password);
        String inputJson = super.mapToJson(user);
        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    /**
     * Creates new user
     */
    @Test
    void createUser() throws Exception {
        for (int i=0; i<validFirstNames.length; i++) {
        	try {
        		createUserValidTestCase(validFirstNames[i], validLastNames[i], validEmails[i], validPasswords[i]);
        	} catch(AssertionError e) {
        		throw new Exception(
        			String.format(
        				"create valid user failed: { firstName: %s, lastName: %s, email: %s, password: %s }",
        				validFirstNames[i],
        				validLastNames[i],
        				validEmails[i],
        				validPasswords[i]
        			),
        			e
        		);
        	}
        }
        for (int i=0; i<invalidNames.length; i++) {
        	try {
        		createUserInvalidTestCase(invalidNames[i], validLastNames[0], validEmails[0], validPasswords[0]);
        	} catch(AssertionError e) {
        		throw new AssertionError(String.format("invalid firstName accepted: %s", invalidNames[i]), e);
        	}
        }
        for (int i=0; i<invalidNames.length; i++) {
        	try {
        		createUserInvalidTestCase(validFirstNames[0], invalidNames[i], validEmails[0], validPasswords[0]);
        	} catch(AssertionError e) {
        		throw new AssertionError(String.format("invalid lastName accepted: %s", invalidNames[i]), e);
        	}
        }
        for (int i=0; i<invalidEmails.length; i++) {
        	try {
        		createUserInvalidTestCase(validFirstNames[0], validLastNames[0], invalidEmails[i], validPasswords[0]);
        	} catch(AssertionError e) {
        		throw new AssertionError(String.format("invalid email accepted: %s", invalidEmails[i]), e);
        	}
        }
        for (int i=0; i<invalidPasswords.length; i++) {
        	try {
        		createUserInvalidTestCase(validFirstNames[0], validLastNames[0], validEmails[0], invalidPasswords[i]);
        	} catch(AssertionError e) {
        		throw new AssertionError(String.format("invalid password accepted: %s", invalidPasswords[i]), e);
        	}
        }
    }

    /**
     * Gets a single existing user
     */
    @Test
    void getSingleUser() throws Exception {
        String uri = "/users/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("\"_links\":"), "Hyperlinks should be included");
        try {
            super.mapFromJson(content, UserInfoDTO.class);
        } catch (Exception ex) {
            fail("Could not receive user with id 1");
        }
    }

    /**
     * Attempt getting an invalid user
     */
    @Test
    void failGetUser() throws Exception {
        String uri = "/users/999999999999999999";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find user with ID 999999999999999999"),
                "Actual content: '" + content + "'");
    }

    private void updateUserValidTestCase(String... data) throws Exception {
    	String uri = "/users/1";

        //Call GET
        MvcResult mvcGetResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertEquals(200, mvcGetResult.getResponse().getStatus());

        String getResult = mvcGetResult.getResponse().getContentAsString();
        UserInfo user = super.mapFromJson(getResult, UserInfoDTO.class).getEntity();
        if (data[0] == null) {
        	data[0] = user.getFirstName();
        } else {
        	user.setFirstName(data[0]);
        }
        if (data[1] == null) {
        	data[1] = user.getLastName();
        } else {
        	user.setLastName(data[1]);
        }
        if (data[2] == null) {
        	data[2] = user.getEmail();
        } else {
        	user.setEmail(data[2]);
        }
        if (data[3] == null) {
        	data[3] = user.getPassword();
        } else {
        	user.setPassword(data[3]);
        }
        String inputJson = super.mapToJson(user);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        UserInfo res = super.mapFromJson(content, UserInfoDTO.class).getEntity();
        assertEquals(res.getFirstName(), data[0]);
        assertEquals(res.getLastName(), data[1]);
        assertEquals(res.getEmail(), data[2]);
    }

    private void updateUserInvalidTestCase(String... data) throws Exception {
    	String uri = "/users/1";

        Map<String, String> user = new HashMap<>();
        for (int i=0; i<fieldNames.length; i++) {
        	if (data[i] != null) {
            	user.put(fieldNames[i], data[i]);
            }
        }
        String inputJson = super.mapToJson(user);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    /**
     * Gets and updates existing user
     */
    @Test
    void updateUser() throws Exception {
        for (String firstName : validFirstNames) {
        	try {
        		updateUserValidTestCase(firstName, null, null, null);
        	} catch (AssertionError e) {
        		throw new AssertionError(String.format("failed to update firstname: %s", firstName), e);
        	}
        }
        for (String lastName : validLastNames) {
        	try {
        		updateUserValidTestCase(null, lastName, null, null);
        	} catch (AssertionError e) {
        		throw new AssertionError(String.format("failed to update lastname: %s", lastName), e);
        	}
        }
        for (String email : validEmails) {
        	try {
        		updateUserValidTestCase(null, null, email, null);
        	} catch (AssertionError e) {
        		throw new AssertionError(String.format("failed to update email: %s", email), e);
        	}
        }
        for (String password : validPasswords) {
        	try {
        		updateUserValidTestCase(null, null, null, password);
        	} catch (AssertionError e) {
        		throw new AssertionError(String.format("failed to update password: %s", password), e);
        	}
        }
        for (String firstName : invalidNames) {
        	try {
        		updateUserInvalidTestCase(firstName, null, null, null);
        	} catch (AssertionError e) {
        		throw new AssertionError(String.format("invalid firstname accepted: %s", firstName), e);
        	}
        }
        for (String lastName : invalidNames) {
        	try {
        		updateUserInvalidTestCase(null, lastName, null, null);
        	} catch (AssertionError e) {
        		throw new AssertionError(String.format("invalid lastname accepted: %s", lastName), e);
        	}
        }
        for (String email : invalidEmails) {
        	try {
        		updateUserInvalidTestCase(null, null, email, null);
        	} catch (AssertionError e) {
        		throw new AssertionError(String.format("invalid email accepted: %s", email), e);
        	}
        }
        for (String password : invalidPasswords) {
        	try {
        		updateUserInvalidTestCase(null, null, null, password);
        	} catch (AssertionError e) {
        		throw new AssertionError(String.format("invalid password accepted: %s", password), e);
        	}
        }
    }

    /**
     * Deletes a user
     */
    @Test
    void deleteUser() throws Exception {
        String uri = "/users/1";
        //Calls DELETE
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals("", content);
    }


    //-----------------------------------

    /**
     * Attempts to delete an invalid user
     */
    @Test
    void deleteInvalidUser() throws Exception {
        String uri = "/users/99999";

        //Calls DELETE
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find user with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Attempts updating an invalid user
     */
    @Test
    void updateInvalidUser() throws Exception {

        String uri = "/users/99999";

        Map<String, String> input = new HashMap<>();
        input.put("name", "lemon");

        String inputJson = super.mapToJson(input);

        //Call PATCH
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.patch(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        String content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("Could not find user with ID 99999"),
                "Actual content: '" + content + "'");
    }

    /**
     * Gets a single existing user
     * and attempts to create a new one with the same email
     */
    @Test
    void createUserWithExistingEmail() throws Exception {
        String uri = "/users/1";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        UserInfo existingUser = super.mapFromJson(content, UserInfoDTO.class).getEntity();

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", "Ginger");
        user.put("lastName", "Bread");
        user.put("email", existingUser.getEmail());
        user.put("role", UserInfo.Userrole.CUSTOMER);
        user.put("password", "H@lpMé843652");

        String json = super.mapToJson(user);

        uri = "/users";

        //Call POST
        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(409, status);
        content = mvcResult.getResponse().getContentAsString();

        assertTrue(content.contains("A user already exists with this email address"),
                "Actual content: '" + content + "'");
    }

    /**
     * Attempts to create a user, but does not fill in all fields
     */
    @Test // Interface was not yet descided
    void createIncompleteUser() throws Exception {

        String uri = "/users";

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", "Ginger");
        user.put("lastName", "Bread");

        String json = super.mapToJson(user);

        //Call POST
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        String content = mvcResult.getResponse().getContentAsString();
        Map<String, Object> parsed = super.readObjectAsMap(content);
        assertTrue(parsed.get("errors") instanceof List );
        List<?> errors = (List<?>) parsed.get("errors");
        assertEquals(3, errors.size()); // email, password & role
        Set<Object> errorParameters = new HashSet<>();
        for (Object error: errors) {
            assertTrue(error instanceof Map);
            assertEquals("must not be null", ((Map<?, ?>) error).get("message"));
            errorParameters.add(((Map<?, ?>) error).get("parameter"));
        }
        assertTrue(errorParameters.contains("email"));
        assertTrue(errorParameters.contains("password"));
        assertTrue(errorParameters.contains("role"));
    }

    //-------------------------------------------------------

    /**
     * Tests the filtering by query parameters
     */
    @Test
    void testQueryParameters() throws Exception {
        String uri = "/users";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Map<String, Object> allUsers = super.readObjectAsMap(content);

        assertNotEquals(0, allUsers.get("total"), "User list was empty");

        String specificUri = "/users/1";

        //Call GET
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(specificUri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        content = mvcResult.getResponse().getContentAsString();

        UserInfo exampleUser = super.mapFromJson(content, UserInfoDTO.class).getEntity();

        //Call GET & filter by name, email and role
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("firstName", exampleUser.getFirstName())
                .queryParam("lastName", exampleUser.getLastName())
                .queryParam("email", exampleUser.getEmail())
                .queryParam("role", exampleUser.getRole().toString().toLowerCase())
        ).andReturn();
        status = mvcResult.getResponse().getStatus();

        assertEquals(200, status);
        content = mvcResult.getResponse().getContentAsString();
        Map<String, Object> queriedUsers = super.readObjectAsMap(content);

        assertEquals(1, queriedUsers.get("total"));
        assertEquals(1, queriedUsers.get("count"));

        //Call GET & filter by gibberish
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("firstName", "fdzEP866/0") //These random strings should never match
                .queryParam("lastName", "fjpoi125He@&")
        ).andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        content = mvcResult.getResponse().getContentAsString();
        queriedUsers = super.readObjectAsMap(content);

        assertEquals(0, queriedUsers.get("total"));
        assertEquals(0, queriedUsers.get("count"));
    }

    /**
     * Tests the skip and limit query parameters
     */
    @Test
    void testSkipAndLimit() throws Exception {
        String uri = "/users";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("skip", "1")
                .queryParam("limit", "100")).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> queriedUsers = super.readObjectAsMap(content);

        assertNotEquals(0, queriedUsers.get("total"));
        assertNotEquals(queriedUsers.get("total"),
                queriedUsers.get("count"));

        //Call GET
        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("skip", "0")
                .queryParam("limit", "1")).andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> queriedUsers2 = super.readObjectAsMap(content);

        assertEquals(1, queriedUsers2.get("count"));
        assertEquals(queriedUsers.get("total"), queriedUsers2.get("total"));
    }

    @Test
    void testGeneralSearchShouldGiveDifferentResults() throws Exception {
        String uri = "/users";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        MvcResult mvcResult2 = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("generalSearch", "test")).andReturn();

        assertNotEquals(mvcResult.getResponse().getContentAsString(),
                mvcResult2.getResponse().getContentAsString());
    }
}
