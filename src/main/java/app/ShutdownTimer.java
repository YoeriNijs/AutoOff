package app;

import javafx.scene.control.CheckBox;
import tornadofx.control.DateTimePicker;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

class ShutdownTimer {

    private static final Logger LOGGER = Logger.getLogger(ShutdownTimer.class.getName());

    private final Timer timer = new Timer("ShutdownTimer");

    private final DateTimePicker dateTimePicker;
    private final CheckBox sendMailCheckbox;

    ShutdownTimer(final DateTimePicker dateTimePicker, final CheckBox sendMailCheckbox) {
        this.dateTimePicker = dateTimePicker;
        this.sendMailCheckbox = sendMailCheckbox;
        doCountDown();
    }

    void start() {
        doCountDown();
    }

    void stop() {
        timer.cancel();
    }

    private void doCountDown() {
        final TimerTask task = new TimerTask() {
            public void run() {
                if(isTimePassed()) {
                    stop();
                    sendMail();
                    shutdown();
                }
            }
        };
        timer.schedule(task, 1000L, 60000L);
    }

    private boolean isTimePassed() {
        final LocalDateTime scheduled = this.dateTimePicker.getDateTimeValue();
        final LocalDateTime now = LocalDateTime.now();
        return now.isEqual(scheduled) || now.isAfter(scheduled);
    }

    private boolean shouldSendNotificationMail() {
        return this.sendMailCheckbox.isSelected();
    }

    private void shutdown() {
        LOGGER.log(Level.INFO, "Shutdown computer...");

        String command;
        final String operatingSystem = System.getProperty("os.name").toUpperCase();
        if (operatingSystem.contains("LINUX") || operatingSystem.contains("MAC")) {
            command = "shutdown -h now";
        } else if (operatingSystem.contains("WINDOWS")) {
            command = "shutdown.exe -s -t 0";
        } else {
            throw new RuntimeException("Unsupported operating system: " + operatingSystem);
        }

        try {
            Runtime.getRuntime().exec(command);
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException("Cannot shutdown computer: " + e.getCause());
        }
    }

    private void sendMail() {
        if (this.shouldSendNotificationMail()) {
            LOGGER.log(Level.INFO, "Send email");
            try {
                Mailer.mail();
            } catch (MessagingException e) {
                throw new RuntimeException("Cannot send mail: " + e);
            }
        }
    }
}
