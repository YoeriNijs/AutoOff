package app;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import tornadofx.control.DateTimePicker;
import tornadofx.control.Fieldset;
import tornadofx.control.Form;

import java.time.format.DateTimeFormatter;

class ShutdownForm {

    private final Form form = new Form();

    private ShutdownTimer shutdownTimer;
    private boolean initialized;

    ShutdownForm() {
        build();
    }

    Form getForm() {
        return this.form;
    }

    private void build() {
        Fieldset fieldset = form.fieldset("Schedule automatic computer shutdown");

        final DateTimePicker shutdownDateTimePicker = new DateTimePicker();
        fieldset.field("Date and time", shutdownDateTimePicker);

        final CheckBox sendMailCheckbox = new CheckBox();
        fieldset.field("Send e-mail", sendMailCheckbox);

        Button startButton = new Button("Schedule");
        startButton.setOnMouseClicked(start -> start(fieldset, shutdownDateTimePicker, sendMailCheckbox, startButton));

        fieldset.field(startButton);
    }

    private void start(final Fieldset fieldset, final DateTimePicker shutdownDateTimePicker,
            final CheckBox sendMailCheckbox, final Button startButton) {
        shutdownDateTimePicker.setDisable(true);
        sendMailCheckbox.setDisable(true);
        startButton.setDisable(true);

        if (!initialized) {
            initializeCounterAndCancel(fieldset, shutdownDateTimePicker, sendMailCheckbox, startButton);
        }

        shutdownTimer = new ShutdownTimer(shutdownDateTimePicker, sendMailCheckbox);
        shutdownTimer.start();
    }

    private void initializeCounterAndCancel(final Fieldset fieldset, final DateTimePicker shutdownDateTimePicker,
                                            final CheckBox sendMailCheckbox, final Button startButton) {
        Label counter = new Label(formatScheduleLabel(shutdownDateTimePicker));
        fieldset.field("Shutdown computer", counter);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(cancel -> cancel(shutdownDateTimePicker, sendMailCheckbox, startButton));
        fieldset.field("Abort", cancelButton);

        initialized = true;
    }

    private String formatScheduleLabel(DateTimePicker shutdownDateTimePicker) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return shutdownDateTimePicker.getDateTimeValue().format(formatter);
    }

    private void cancel(DateTimePicker shutdownDateTimePicker, final CheckBox sendMailCheckbox, Button startButton) {
        shutdownDateTimePicker.setDisable(false);
        sendMailCheckbox.setDisable(false);
        startButton.setDisable(false);
        shutdownTimer.stop();
    }
}
