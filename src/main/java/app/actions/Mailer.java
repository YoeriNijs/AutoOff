package app.actions;

import app.util.AutoOffUtil;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class Mailer implements IAction {

    private static final Logger LOGGER = Logger.getLogger(Mailer.class.getName());

    private Properties m_properties;
    private Session m_session;
    private Message m_message;

    @Override
    public boolean run() {
        LOGGER.log(Level.INFO, "Send mail...");
        try {
            final Message message = setupMessage();
            Transport.send(message);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Message setupMessage() throws Exception {
        createSession();
        createMessage();
        return AutoOffUtil.nullChecked(m_message);
    }

    private void createSession() {
        m_properties = getProperties();
        m_session = Session.getInstance(AutoOffUtil.nullChecked(m_properties), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        AutoOffUtil.nullChecked(m_properties).getProperty("mail.smtp.host.address"),
                        AutoOffUtil.nullChecked(m_properties).getProperty("mail.smtp.host.password")
                );
            }
        });
    }

    private void createMessage() throws MessagingException {
        final Session session = AutoOffUtil.nullChecked(m_session);
        m_message = new MimeMessage(session);
        m_message.setFrom(new InternetAddress("computer@localhost"));
        m_message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(AutoOffUtil.nullChecked(m_properties).getProperty("recipient")));
        m_message.setSubject("Computer turned off");

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String msg = "Computer turned off successfully at " + formatter.format(LocalDateTime.now());

        final MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html");

        final Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        m_message.setContent(multipart);
    }

    private Properties getProperties() {
        try (InputStream input = new FileInputStream("mail.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot retrieve properties: " + ex);
        }
        throw new IllegalStateException("Cannot retrieve properties");
    }
}
