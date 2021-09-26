package be.sel2.api;

import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A testing utility class used to check messages sent from the server
 */
public class WiserAssertions {

    private final List<WiserMessage> messages;

    public static WiserAssertions assertReceivedMessage(Wiser wiser) {
        return new WiserAssertions(wiser.getMessages());
    }

    private WiserAssertions(List<WiserMessage> messages) {
        this.messages = messages;
    }

    public WiserAssertions from(String from) {
        findFirstOrElseThrow(m -> m.getEnvelopeSender().equals(from),
                assertionError("No message from [{0}] found!", from));
        return this;
    }

    public WiserAssertions to(String to) {
        findFirstOrElseThrow(m -> m.getEnvelopeReceiver().equals(to),
                assertionError("No message to [{0}] found!", to));
        return this;
    }

    public WiserAssertions withSubject(String subject) {
        Predicate<WiserMessage> predicate = m -> subject.equals(unchecked(getMimeMessage(m)::getSubject));
        findFirstOrElseThrow(predicate,
                assertionError("No message with subject [{0}] found!", subject));
        return this;
    }

    public WiserAssertions withSubjectMatch(String regex) {
        Predicate<WiserMessage> predicate = m -> unchecked(getMimeMessage(m)::getSubject).matches(regex);
        findFirstOrElseThrow(predicate,
                assertionError("No message with subject matching [{0}] found!", regex));
        return this;
    }

    public WiserAssertions withContent(String content) {
        findFirstOrElseThrow(m -> {
            ThrowingSupplier<String> contentAsString =
                    () -> ((String) getMimeMessage(m).getContent()).trim();
            return content.equals(unchecked(contentAsString));
        }, assertionError("No message with content [{0}] found!", content));
        return this;
    }

    public WiserAssertions withContentMatch(String regex) {
        findFirstOrElseThrow(m -> {
            ThrowingSupplier<String> contentAsString =
                    () -> ((String) getMimeMessage(m).getContent()).trim();
            return unchecked(contentAsString).matches(regex);
        }, assertionError("No message with content matching [{0}] found!", regex));
        return this;
    }



    private void findFirstOrElseThrow(Predicate<WiserMessage> predicate, Supplier<AssertionError> exceptionSupplier) {
        messages.stream().filter(predicate)
                .findFirst().orElseThrow(exceptionSupplier);
    }

    private MimeMessage getMimeMessage(WiserMessage wiserMessage) {
        return unchecked(wiserMessage::getMimeMessage);
    }

    private static Supplier<AssertionError> assertionError(String errorMessage, String... args) {
        return () -> new AssertionError(MessageFormat.format(errorMessage, args));
    }

    public static <T> T unchecked(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }
}
