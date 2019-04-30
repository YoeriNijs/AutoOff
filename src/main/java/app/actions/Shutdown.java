package app.actions;

import app.util.AutoOffUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Shutdown implements IAction {

    private static final Logger LOGGER = Logger.getLogger(Shutdown.class.getName());

    @Override
    public boolean run() {
        LOGGER.log(Level.INFO, "Shutdown computer...");
        try {
            final String command = AutoOffUtil.nullChecked(setupCommand());
            Runtime.getRuntime().exec(command);
            System.exit(0);

            return true; // Should not be executed
        } catch (IOException e) {
            return false;
        }
    }

    private String setupCommand() {
        final String operatingSystem = System.getProperty("os.name").toUpperCase();
        if (operatingSystem.contains("LINUX") || operatingSystem.contains("MAC")) {
            return "shutdown -h now";
        }
        if (operatingSystem.contains("WINDOWS")) {
            return "shutdown.exe -s -t 0";
        }

        return null;
    }
}
