package frc.lib.faults;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BooleanSupplier;

import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import frc.lib.Alert;
import frc.lib.eventLoops.EventLoops;
import frc.lib.eventLoops.IsolatedEventLoop;

public final class Fault {
    private boolean isActive = false;
    private boolean wasActive = false;
    private Alert errorAlert;
    private Alert warningAlert;
    private BooleanLogEntry booleanLog;

    public Fault(String description) {
        this.errorAlert = new Alert("[Active] " + description, Alert.AlertType.ERROR);
        this.warningAlert = new Alert("[Recent] " + description, Alert.AlertType.WARNING);
        this.booleanLog = new BooleanLogEntry(DataLogManager.getLog(), description);
        FaultRegistry.register(this);
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
        booleanLog.append(isActive);

        if (isActive) {
            wasActive = true;
            errorAlert.set(true);
        } else {
            errorAlert.set(false);
            if (wasActive) {
                warningAlert.set(true);
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean wasActive() {
        return wasActive;
    }

    public static void autoUpdating(String description, IsolatedEventLoop oncepersec, BooleanSupplier booleanSupplier) {
        Fault fault = new Fault(description);
        oncepersec.bind(() -> {
            fault.setIsActive(booleanSupplier.getAsBoolean());
        });
    }

    public static void autoUpdating(String description, BooleanSupplier booleanSupplier) {
        autoUpdating(description, EventLoops.oncePerSec, booleanSupplier);
    }

    public static void setupDefaultFaults() {
        // autoUpdating("canBusUtilization", () -> {
        // return RobotController.getCANStatus().percentBusUtilization > 0.8;
        // });

        autoUpdating("No USB connected into RoboRio", () -> {
            if (RobotBase.isSimulation()) {
                return false;
            } else {
                try {
                    // prefer a mounted USB drive if one is accessible
                    Path usbDir = Paths.get("/u").toRealPath();
                    if (Files.isWritable(usbDir)) {
                        return false;
                    } else {
                        return true;
                    }
                } catch (IOException ex) {
                    return true;
                }
            }
        });
// Different stages: https://docs.wpilib.org/en/stable/docs/software/roborio-info/roborio-brownouts.html
        autoUpdating("RoboRio browned out", EventLoops.everyLoop, RobotController::isBrownedOut);
        autoUpdating("RoboRio 3.3V browned out (Stage 2)", EventLoops.everyLoop, () -> !RobotController.getEnabled3V3());
        autoUpdating("RoboRio 5V browned out (Stage 2)", EventLoops.everyLoop, () -> !RobotController.getEnabled5V());
        autoUpdating("RoboRio 6V browned out (Stage 1)", EventLoops.everyLoop, () -> !RobotController.getEnabled6V());

    }

}
