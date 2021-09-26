package be.sel2.api;

import org.springframework.beans.factory.annotation.Value;
import org.subethamail.wiser.Wiser;

/**
 * Abstract class with the basic implementation for tests, including
 * a {@link Wiser} that catches emails.
 *
 * All tests that could send emails should extend from this.
 */
public abstract class AbstractMailTest extends AbstractTest {

    protected Wiser wiser;

    @Value("${spring.mail.port}")
    private int portNr;

    @Override
    protected void setUp() {
        wiser = new Wiser();
        wiser.setPort(portNr);
        wiser.start();
        super.setUp();
    }

    protected void cleanup() {
        wiser.stop();
    }
}
