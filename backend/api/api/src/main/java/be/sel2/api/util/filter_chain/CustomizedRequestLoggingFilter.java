package be.sel2.api.util.filter_chain;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

/**
 * Extended filter that logs the incoming messages
 */
public class CustomizedRequestLoggingFilter extends AbstractRequestLoggingFilter {

    @Override
    protected void beforeRequest(@NonNull HttpServletRequest httpServletRequest, @NonNull String message) {
        // Should not log
    }

    @Override
    protected void afterRequest(@NonNull HttpServletRequest httpServletRequest, @NonNull String message) {
        this.logger.info(message);
    }
}
