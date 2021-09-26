package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileTests extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    void emptyFileUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", "text/plain",new byte[]{});
        String uri = "/files";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart(uri).file(file)).andReturn();

        assertEquals(400, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Upload was empty"));
    }

    @Test
    void failUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", "text/plain", "test".getBytes());
        String uri = "/files";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.multipart(uri).file(file)).andReturn();

        assertEquals(400, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Was unable to save file"));
    }

    @Test
    void getFile() throws Exception {
        String uri = "/files/1";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("/uploads/permissionFile"));
    }

    @Test
    void failGetFile() throws Exception {
        String uri = "/files/99999";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertEquals(404, mvcResult.getResponse().getStatus());
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("Could not find file with ID 99999"), "Recieved: " + response);
    }
}
