package be.sel2.api.util.mails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;

/**
 * A utility class to load email templates and fill them in
 */
@Component
public class MailBuilder {

    private final ResourceLoader loader;

    @Value("${fronted.proposal.href.format}")
    private String proposalUrlFormat;

    public MailBuilder(ResourceLoader loader) {
        this.loader = loader;
    }

    public String resourceAsString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String buildStringFromResource(@NotNull String resourcePath, Object... args) {
        try (Formatter fmt = new Formatter()) {
            Resource mailTemplate = loader.getResource(resourcePath);
            String mail = resourceAsString(mailTemplate);

            return fmt.format(mail, args).toString();
        }
    }

    public String titleFormat(String text) {
        return String.format("MAGDA melding [%s]", text);
    }

    public String applicationProposalUrl(Long proposalId) {
        return String.format(proposalUrlFormat, proposalId);
    }
}
