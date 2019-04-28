package app;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tornadofx.control.DateTimePicker;
import tornadofx.control.Fieldset;
import tornadofx.control.Form;

import java.time.format.DateTimeFormatter;

class TaskForm {

    private final Form form = new Form();

    private Countdown countdown;
    private boolean counterAndCancelInitialized;

    TaskForm() {
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
        startButton.setOnMouseClicked(start -> {
            shutdownDateTimePicker.setDisable(true);
            startButton.setDisable(true);

            if (!counterAndCancelInitialized) {
                initializeCounterAndCancel(fieldset, shutdownDateTimePicker, startButton);
            }

            countdown = new Countdown(shutdownDateTimePicker);
            countdown.start();
        });

        fieldset.field(startButton);
    }

    private void initializeCounterAndCancel(final Fieldset fieldset, final DateTimePicker shutdownDateTimePicker,
                                            final Button startButton) {
        Label counter = new Label(formatLabel(shutdownDateTimePicker));
        fieldset.field("Shutdown computer", counter);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(cancel -> cancel(shutdownDateTimePicker, startButton));
        fieldset.field("Abort", cancelButton);

        counterAndCancelInitialized = true;
    }

    private String formatLabel(DateTimePicker shutdownDateTimePicker) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return shutdownDateTimePicker.getDateTimeValue().format(formatter);
    }

    private void cancel(DateTimePicker shutdownDateTimePicker, Button startButton) {
        shutdownDateTimePicker.setDisable(false);
        startButton.setDisable(false);
        countdown.stop();
    }
}
