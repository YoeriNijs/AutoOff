package app;

import app.actions.Countdown;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tornadofx.control.Fieldset;
import tornadofx.control.Form;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

class SetupMail {
    private static final Logger LOGGER = Logger.getLogger(Countdown.class.getName());
    private static final int SCENE_WIDTH = 600;

    static void display() {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(createForm(stage), SCENE_WIDTH, 500));
        stage.show();
    }

    private static GridPane createForm(final Stage stage) {
        final Form form = new Form();
        form.setMinWidth(SCENE_WIDTH - 100);
        Fieldset fieldset = form.fieldset("Mail settings");

        final TextField recipient = new TextField("");
        fieldset.field("Recipient", recipient);

        final TextField sender = new TextField("");
        fieldset.field("Sender", sender);

        final TextField smtpHostMailAddress = new TextField("");
        fieldset.field("Smtp host mail address", smtpHostMailAddress);

        final TextField smtpHostMailPassword = new TextField("");
        fieldset.field("Smtp host mail password", smtpHostMailPassword);

        final TextField host = new TextField("");
        fieldset.field("Smtp host", host);

        final TextField trustedHost = new TextField("");
        fieldset.field("Smtp trusted host", trustedHost);

        final TextField port = new TextField("");
        fieldset.field("Smtp port", port);

        final CheckBox sslEnabled = new CheckBox();
        fieldset.field("Ssl enabled", sslEnabled);

        final Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setOnMouseClicked(clicked -> {
            save(
                    recipient.getCharacters().toString(),
                    sender.getCharacters().toString(),
                    smtpHostMailAddress.getCharacters().toString(),
                    smtpHostMailPassword.getCharacters().toString(),
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
                             final String smtpHostMailPassword, final String host, final String trustedHost,
                             final String port, final boolean sslEnabled) {
        try {
            Properties props = new Properties();
            props.setProperty("recipient", recipient);
            props.setProperty("sender", sender);
            props.setProperty("mail.smtp.host.address", "" + smtpHostMailAddress);
            props.setProperty("mail.smtp.host.password", "" + smtpHostMailPassword);
            props.setProperty("mail.smtp.host", "" + host);
            props.setProperty("mail.smtp.ssl.trust", "" + trustedHost);
            props.setProperty("mail.smtp.port", "" + port);
            props.setProperty("mail.smtp.ssl.enable", "" + sslEnabled);
            File f = new File("mail.properties");
            OutputStream out = new FileOutputStream(f);
            props.store(out, "AutoOff mail settings");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot store mail properties: " + e);
        }
    }
}