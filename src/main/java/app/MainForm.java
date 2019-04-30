package app;

import app.actions.Countdown;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import tornadofx.control.DateTimePicker;
import tornadofx.control.Fieldset;
import tornadofx.control.Form;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class MainForm {

    private final Form m_form = new Form();

    private Countdown m_countdown;
    private boolean m_initialized;

    MainForm() {
        build();
    }

    Form getForm() {
        return m_form;
    }

    private void build() {
        Fieldset fieldset = m_form.fieldset("Schedule automatic computer m_countdown");

        final DateTimePicker shutdownDateTimePicker = new DateTimePicker();
        shutdownDateTimePicker.setDateTimeValue(LocalDateTime.now().plusMinutes(1)); // Initial value
        fieldset.field("Date and time", shutdownDateTimePicker);

        final CheckBox mailCheckbox = new CheckBox();
        mailCheckbox.setDisable(!hasMailSettings());
        fieldset.field("Send e-mail", mailCheckbox);

        final Button startButton = new Button("Schedule");
        startButton.setDefaultButton(true);
        startButton.setOnMouseClicked(start -> start(fieldset, shutdownDateTimePicker, mailCheckbox, startButton));

        final Button mailSettingsButton = new Button("Settings");
        mailSettingsButton.setOnAction(clicked -> SetupMail.display());

        final HBox buttonBar = new HBox();
        buttonBar.getChildren().addAll(startButton, mailSettingsButton);
        buttonBar.setSpacing(10);

        fieldset.field(buttonBar);
    }

    private boolean hasMailSettings() {
        File f = new File("mail.properties");
        return f.exists() && !f.isDirectory();
    }

    private void start(final Fieldset fieldset, final DateTimePicker shutdownDateTimePicker,
                       final CheckBox mailCheckbox, final Button startButton) {
        shutdownDateTimePicker.setDisable(true);
        mailCheckbox.setDisable(true);
        startButton.setDisable(true);

        if (!m_initialized) {
            initializeCounterAndCancel(fieldset, shutdownDateTimePicker, mailCheckbox, startButton);
        }

        final LocalDateTime scheduledTime = shutdownDateTimePicker.getDateTimeValue();
        final boolean shouldSendEmail = mailCheckbox.isSelected();
        m_countdown = new Countdown(scheduledTime, shouldSendEmail);
        m_countdown.run();
    }

    private void initializeCounterAndCancel(final Fieldset fieldset, final DateTimePicker shutdownDateTimePicker,
                                            final CheckBox sendMailCheckbox, final Button startButton) {
        Label counter = new Label(formatScheduleLabel(shutdownDateTimePicker));
        fieldset.field("Shutdown computer", counter);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(cancel -> cancel(shutdownDateTimePicker, sendMailCheckbox, startButton));
        fieldset.field("Abort", cancelButton);

        m_initialized = true;
    }

    private String formatScheduleLabel(DateTimePicker shutdownDateTimePicker) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return shutdownDateTimePicker.getDateTimeValue().format(formatter);
    }

    private void cancel(DateTimePicker shutdownDateTimePicker, final CheckBox sendMailCheckbox, Button startButton) {
        shutdownDateTimePicker.setDisable(false);
        sendMailCheckbox.setDisable(false);
        startButton.setDisable(false);
        m_countdown.stop();
    }
}
