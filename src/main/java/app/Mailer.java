package app;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

class Mailer {

    // TODO: Replace with your own params
    private static final String HOST_SMTP_MAILADDRESS = "your e-mail address";
    private static final String HOST_SMTP_PASSWORD = "your e-mail password";
    private static final String HOST_SMTP_HOST = "smtp.gmail.com";
    private static final String HOST_SMTP_HOST_TRUSTED = "smtp.gmail.com";
    private static final String HOST_SMTP_PORT = "465";
    private static final boolean SMTP_SSL_ENABLED = true;
    private static final boolean SMTP_AUTH_ENABLED = true;

    private static final String MAIL_RECIPIENT = "your recipient address";

    static void mail() throws MessagingException {
        final Session session = Session.getInstance(getMailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(HOST_SMTP_MAILADDRESS, HOST_SMTP_PASSWORD);
            }
        });
        Transport.send(getMessage(session));
    }

    private static Message getMessage(final Session session) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("computer@localhost"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(MAIL_RECIPIENT));
        message.setSubject("Computer turned off");

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String msg = "Computer turned off successfully at " + formatter.format(LocalDateTime.now());

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        return message;
    }

    private static Properties getMailProperties() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", SMTP_AUTH_ENABLED);
        prop.put("mail.smtp.ssl.enable", SMTP_SSL_ENABLED);
        prop.put("mail.smtp.host", HOST_SMTP_HOST);
        prop.put("mail.smtp.port", HOST_SMTP_PORT);
        prop.put("mail.smtp.ssl.trust", HOST_SMTP_HOST_TRUSTED);
        return prop;
    }
}
