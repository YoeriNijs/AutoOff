package app;

import javafx.scene.control.Button;
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

        Button startButton = new Button("Schedule");
        startButton.setOnMouseClicked(start -> start(fieldset, shutdownDateTimePicker, startButton));

        fieldset.field(startButton);
    }

    private void start(final Fieldset fieldset, final DateTimePicker shutdownDateTimePicker, final Button startButton) {
        shutdownDateTimePicker.setDisable(true);
        startButton.setDisable(true);

        if (!initialized) {
            initializeCounterAndCancel(fieldset, shutdownDateTimePicker, startButton);
        }

        shutdownTimer = new ShutdownTimer(shutdownDateTimePicker);
        shutdownTimer.start();
    }

    private void initializeCounterAndCancel(final Fieldset fieldset, final DateTimePicker shutdownDateTimePicker,
                                            final Button startButton) {
        Label counter = new Label(formatScheduleLabel(shutdownDateTimePicker));
        fieldset.field("Shutdown computer", counter);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(cancel -> cancel(shutdownDateTimePicker, startButton));
        fieldset.field("Abort", cancelButton);

        initialized = true;
    }

    private String formatScheduleLabel(DateTimePicker shutdownDateTimePicker) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return shutdownDateTimePicker.getDateTimeValue().format(formatter);
    }

    private void cancel(DateTimePicker shutdownDateTimePicker, Button startButton) {
        shutdownDateTimePicker.setDisable(false);
        startButton.setDisable(false);
        shutdownTimer.stop();
    }
}
