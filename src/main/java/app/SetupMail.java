package app;

import app.actions.Countdown;
import app.util.AutoOffUtil;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tornadofx.control.Fieldset;
import tornadofx.control.Form;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static app.Main.APP_HEIGHT;
import static app.Main.APP_WIDTH;

public class SetupMail {
    public static final String PROPERTY_SENDER = "sender";
    public static final String PROPERTY_RECIPIENT = "recipient";
    public static final String PROPERTY_HOST_ADDRESS = "mail.smtp.host.address";
    public static final String PROPERTY_HOST_PASSWORD = "mail.smtp.host.password";
    private static final String PROPERTY_HOST = "mail.smtp.host";
    private static final String PROPERTY_TRUSTED_HOST = "mail.smtp.ssl.trust";
    private static final String PROPERTY_PORT = "mail.smtp.port";
    private static final String PROPERTY_SSL_ENABLED = "mail.smtp.ssl.enable";
    private static final String PROPERTY_AUTH_ENABLED = "mail.smtp.auth";
    static final String PROPERTIES_FILE = "mail.properties";
    private static final Logger LOGGER = Logger.getLogger(Countdown.class.getName());

    static void display() {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(createForm(stage), APP_WIDTH, APP_HEIGHT));
        stage.show();
    }

    public static Properties getMailProperties(Logger logger) {
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot retrieve properties: " + ex);
        }
        throw new IllegalStateException("Cannot retrieve properties");
    }

    private static GridPane createForm(final Stage stage) {
        final boolean hasMailSettings = hasMailSettings();
        final Properties properties = hasMailSettings ? getMailProperties(LOGGER) : null;

        final Form form = new Form();
        form.setMinWidth(APP_WIDTH - 100);
        Fieldset fieldset = form.fieldset("Mail settings");

        final TextField recipient = new TextField(getValue(properties, hasMailSettings, PROPERTY_RECIPIENT));
        fieldset.field("Recipient", recipient);

        final TextField sender = new TextField(getValue(properties, hasMailSettings, PROPERTY_SENDER));
        fieldset.field("Sender", sender);

        final TextField smtpHostMailAddress = new TextField(getValue(properties, hasMailSettings, PROPERTY_HOST_ADDRESS));
        fieldset.field("Smtp host mail address", smtpHostMailAddress);

        final PasswordField smtpHostMailPassword = new PasswordField();
        final int currentPasswordLength = getValue(properties, hasMailSettings, PROPERTY_SENDER).length();
        final String passwordFiller = IntStream.range(0, currentPasswordLength).mapToObj(i -> "*").collect(Collectors.joining(""));
        smtpHostMailPassword.setPromptText(passwordFiller);
        fieldset.field("Smtp host mail password", smtpHostMailPassword);

        final TextField host = new TextField(getValue(properties, hasMailSettings, PROPERTY_HOST));
        fieldset.field("Smtp host", host);

        final TextField trustedHost = new TextField(getValue(properties, hasMailSettings, PROPERTY_TRUSTED_HOST));
        fieldset.field("Smtp trusted host", trustedHost);

        final TextField port = new TextField(getValue(properties, hasMailSettings, PROPERTY_PORT));
        fieldset.field("Smtp port", port);

        final CheckBox sslEnabled = new CheckBox();
        sslEnabled.setSelected(!"".equals(getValue(properties, hasMailSettings, PROPERTY_SSL_ENABLED)));
        fieldset.field("Ssl enabled", sslEnabled);

        final Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setOnMouseClicked(clicked -> {
            save(
                    recipient.getCharacters().toString(),
                    sender.getCharacters().toString(),
                    smtpHostMailAddress.getCharacters().toString(),
                    smtpHostMailPassword.getCharacters(),
                    host.getCharacters().toString(),
                    trustedHost.getCharacters().toString(),
                    port.getCharacters().toString(),
                    sslEnabled.isSelected()
            );
            stage.close();
        });

        final Button cancelButton = new Button("Cancel");
        cancelButton.setOnMouseClicked(clicked -> stage.close());

        final HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll(saveButton, cancelButton);
        buttonBar.setSpacing(10);
        fieldset.field(buttonBar);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(form, 1, 1);

        return gridPane;
    }

    private static void save(final String recipient, final String sender, final String smtpHostMailAddress,
                             final CharSequence smtpHostMailPassword, final String host, final String trustedHost,
                             final String port, final boolean sslEnabled) {
        try {
            Properties props = new Properties();
            props.setProperty(PROPERTY_RECIPIENT, recipient);
            props.setProperty(PROPERTY_SENDER, sender);
            props.setProperty(PROPERTY_HOST_ADDRESS, smtpHostMailAddress);
            props.setProperty(PROPERTY_HOST_PASSWORD, smtpHostMailPassword);
            props.setProperty(PROPERTY_HOST, host);
            props.setProperty(PROPERTY_TRUSTED_HOST, trustedHost);
            props.setProperty(PROPERTY_PORT, port);
            props.setProperty(PROPERTY_SSL_ENABLED, sslEnabled);

            final boolean hasAuthEnabled = !AutoOffUtil.isEmpty(smtpHostMailAddress)
                    && !AutoOffUtil.isEmpty(smtpHostMailPassword.toString());
            props.setProperty(PROPERTY_AUTH_ENABLED, String.valueOf(hasAuthEnabled));

            File f = new File(PROPERTIES_FILE);
            OutputStream out = new FileOutputStream(f);
            props.store(out, "AutoOff mail settings");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot store mail properties: " + e);
        }
    }

    private static boolean hasMailSettings() {
        File f = new File(PROPERTIES_FILE);
        return f.exists() && !f.isDirectory();
    }

    private static String getValue(final Properties properties, final boolean hasMailSettings, final String value) {
        return hasMailSettings ? properties.getProperty(value) : "";
    }
}
