package app.actions;

import app.util.AutoOffUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Shutdown implements IAction {

    private static final Logger LOGGER = Logger.getLogger(Shutdown.class.getName());

    @Override
    public void run() throws IOException {
        LOGGER.log(Level.INFO, "Shutdown computer...");
        shutdown();
    }

    private void shutdown() throws IOException {
        final String operatingSystem = System.getProperty("os.name").toUpperCase();
        if (operatingSystem.contains("LINUX") || operatingSystem.contains("MAC")) {
            shutdownUnix();
        }
        if (operatingSystem.contains("WINDOWS")) {
            shutdownWindows();
        }

        System.exit(0);
    }

    private void shutdownUnix() throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("bash", "sudo shutdown -h now");
        pb.start();
    }

    private void shutdownWindows() throws IOException {
        Runtime.getRuntime().exec("shutdown.exe -s -t 0");
    }
}
