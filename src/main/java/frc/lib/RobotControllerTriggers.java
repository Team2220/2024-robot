package frc.lib;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public final class RobotControllerTriggers {

  private RobotControllerTriggers() {
    throw new UnsupportedOperationException("This is a utility class!");
  }

  public static Trigger isSysActive() {
    return new Trigger(RobotController::isSysActive);
  }
}