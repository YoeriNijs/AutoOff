package app;

import tornadofx.control.DateTimePicker;

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

    ShutdownTimer(final DateTimePicker dateTimePicker) {
        this.dateTimePicker = dateTimePicker;
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
}
