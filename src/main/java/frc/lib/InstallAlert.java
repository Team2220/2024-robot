package frc.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;

public class InstallAlert {
    public static void main(String[] args) throws IOException {
        // checking if alerts exist already
        Path filePathObj = Paths
                .get(System.getProperty("user.home") + File.separator + "Shuffleboard" + File.separator + "plugins"
                        + File.separator + "NetworkAlerts.jar");
        boolean fileExists = Files.exists(filePathObj);
        if (!fileExists) {
            ReadableByteChannel rbcObj = null;
            FileOutputStream fOutStream = null;
            try {
                URL urlObj = new URL(
                        "https://github.com/Mechanical-Advantage/NetworkAlerts/releases/download/v1.0.0/NetworkAlerts.jar");
                rbcObj = Channels.newChannel(urlObj.openStream());
                fOutStream = new FileOutputStream(
                        System.getProperty("user.home") + File.separator + "Shuffleboard" + File.separator + "plugins"
                                + File.separator + "NetworkAlerts.jar");

                fOutStream.getChannel().transferFrom(rbcObj, 0, Long.MAX_VALUE);
                System.out.println("! Shuffleboard Alerts Succesfully Downloaded !");
            } catch (IOException ioExObj) {
                System.out.println("Problem Occured While Downloading Shuffleboard Alerts= " + ioExObj.getMessage());
            } finally {
                try {
                    if (fOutStream != null) {
                        fOutStream.close();
                    }
                    if (rbcObj != null) {
                        rbcObj.close();
                    }
                } catch (IOException ioExObj) {
                    System.out.println("Problem Occured While Closing The Object = " + ioExObj.getMessage());
                }
            }
        } else {
            System.out.println("You Have Already Downloaded Shuffleboard Alerts!");
        }
    }
}
