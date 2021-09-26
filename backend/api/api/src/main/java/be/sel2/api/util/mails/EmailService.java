package be.sel2.api.util.mails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * A utility component used to send emails
 */
@Component
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${mail.origin}")
    private String source;

    @Value("${mail.enable}")
    private boolean mailEnabled;

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String subject, String message, String... to) {

        if (mailEnabled) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setFrom(source);
            msg.setSubject(subject);
            msg.setText(message);

            try {
                javaMailSender.send(msg);
            } catch (MailException ex) {
                log.error("WARNING: failed to send email notification.", ex);
            }
        }
    }
}
