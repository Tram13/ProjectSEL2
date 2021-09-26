package be.sel2.api;

import be.sel2.api.util.filter_chain.CustomizedRequestLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiApplication {

    //Default spring boot init
    //Run with `mvnw clean spring-boot:run`
    //Or run this class if the IDE allows it
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Bean
    public CustomizedRequestLoggingFilter requestLoggingFilter() {
        CustomizedRequestLoggingFilter loggingFilter = new CustomizedRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(false);
        loggingFilter.setMaxPayloadLength(10000);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeHeaders(false);
        return loggingFilter;
    }

}
