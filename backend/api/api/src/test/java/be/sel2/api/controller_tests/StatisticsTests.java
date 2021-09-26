package be.sel2.api.controller_tests;

import be.sel2.api.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatisticsTests extends AbstractTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @ParameterizedTest
    @MethodSource("provideUrls")
    void testReturns(String uri, String key, Boolean assertNotEmpty) throws Exception {

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());

        String content = mvcResult.getResponse().getContentAsString();

        if (key == null) {
            List<?> list = super.mapFromJson(content, List.class);
            if (assertNotEmpty) {
                assertTrue(list.size() > 0);
            }
        } else {
            Map<String, Object> map = super.readObjectAsMap(content);
            assertTrue(map.containsKey(key));
            assertTrue(map.get(key) instanceof List<?>);
            if (assertNotEmpty) {
                assertTrue(((List<?>) map.get(key)).size() > 0);
            }
        }

    }

    private static Stream<Arguments> provideUrls() {
        return Stream.of(
                Arguments.of("/statistics/certificates", null, true),
                Arguments.of("/statistics/files", "stats", true),
                Arguments.of("/statistics/organisations", null, true),
                Arguments.of("/statistics/organisations/1", "proposals", true),
                Arguments.of("/statistics/organisations/1", "members", true),
                Arguments.of("/statistics/organisations/1", "permissions", true),
                Arguments.of("/statistics/packages", null, true),
                Arguments.of("/statistics/packages/1", "customers", false),
                Arguments.of("/statistics/permissions", null, true),
                Arguments.of("/statistics/proposals", null, true),
                Arguments.of("/statistics/services", null, true),
                Arguments.of("/statistics/services/1", "customers", false),
                Arguments.of("/statistics/users", null, true)
        );
    }

    @Test
    void testRootResponse() throws Exception {
        String uri = "/statistics";

        //Call GET
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        Map<String, Object> result = super.readObjectAsMap(content);
        assertTrue(result.get("_links") instanceof Map);

        Map<?, ?> links = (Map<?, ?>) result.get("_links");

        assertTrue(links.get("root") instanceof Map);

        String baseUrl = (String) ((Map<?, ?>) links.get("root")).get("href");

        String statisticsUrl = baseUrl + "statistics/";


        assertEquals(baseUrl + "statistics", ((Map<?, ?>) links.get("self")).get("href"));
        assertEquals(statisticsUrl + "permissions", ((Map<?, ?>) links.get("permissions")).get("href"));
        assertEquals(statisticsUrl + "proposals", ((Map<?, ?>) links.get("proposals")).get("href"));
        assertEquals(statisticsUrl + "services", ((Map<?, ?>) links.get("services")).get("href"));
        assertEquals(statisticsUrl + "users", ((Map<?, ?>) links.get("users")).get("href"));
        assertEquals(statisticsUrl + "files", ((Map<?, ?>) links.get("files")).get("href"));
        assertEquals(statisticsUrl + "organisations", ((Map<?, ?>) links.get("organisations")).get("href"));
        assertEquals(statisticsUrl + "certificates", ((Map<?, ?>) links.get("certificates")).get("href"));
    }
}
