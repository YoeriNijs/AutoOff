package app.actions;

import app.util.AutoOffUtil;

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
    private final boolean m_forceShutdown;

    public Countdown(final LocalDateTime scheduled, final boolean shouldSendEmail, final boolean forceShutdown) {
        m_scheduled = scheduled;
        m_shouldSendEmail = shouldSendEmail;
        m_forceShutdown = forceShutdown;
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
            if (m_shouldSendEmail) {
                final boolean sent = new Mailer().run();
                if (!sent && !m_forceShutdown) {
                    LOGGER.log(Level.SEVERE, "Cannot send mail");
                    return;
                }
            }

            new Shutdown().run();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot execute task after countdown " + e);
            stop();
        }
    }

    private boolean isTimePassed() {
        final LocalDateTime scheduled = AutoOffUtil.nullChecked(m_scheduled);
        final LocalDateTime now = LocalDateTime.now();
        return now.isEqual(scheduled) || now.isAfter(scheduled);
    }
}
