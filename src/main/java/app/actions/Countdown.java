package app.actions;

import app.util.AutoOffUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Countdown implements IAction {

    private static final Logger LOGGER = Logger.getLogger(Countdown.class.getName());

    private final Timer timer = new Timer("ShutdownTimer");
    private TimerTask m_task;
    private LocalDateTime m_scheduled;
    private boolean m_shouldSendEmail;

    public Countdown(final LocalDateTime scheduled, final boolean shouldSendEmail) {
        m_scheduled = scheduled;
        m_shouldSendEmail = shouldSendEmail;
    }

    @Override
    public boolean run() {
        LOGGER.log(Level.INFO, "Start countdown...");
        final TimerTask task = m_task = createTask();
        timer.schedule(task, 1000L, 60000L);
        return true;
    }

    public void stop() {
        m_task.cancel();
    }

    private TimerTask createTask() {
        return new TimerTask() {
            public void run() {
                if (isTimePassed()) {
                    handleTimePassed();
                }
            }
        };
    }

    private void handleTimePassed() {
        try {
            // Check if we have to send a notification mail
            if (m_shouldSendEmail) {
                new Mailer().run();
            }

            // Shutdown this machine...
            new Shutdown().run();
        } catch (Exception e) {
            // Oops, something went wrong
            LOGGER.log(Level.SEVERE, "Cannot execute task after countdown " + e);
            displayErrorMessage();
            stop();
        }
    }

    private boolean isTimePassed() {
        final LocalDateTime scheduled = AutoOffUtil.nullChecked(m_scheduled);
        final LocalDateTime now = LocalDateTime.now();
        return now.isEqual(scheduled) || now.isAfter(scheduled);
    }

    private void displayErrorMessage() {
        Stage alertStage = new Stage();
        alertStage.initModality(Modality.WINDOW_MODAL);

        VBox vbox = new VBox(new Text("Unable to shutdown the computer"), new Button("Ooh-noo!"));
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15));

        alertStage.setScene(new Scene(vbox));
        alertStage.show();
    }
}
