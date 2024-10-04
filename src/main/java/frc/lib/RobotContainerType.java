package frc.lib;

import edu.wpi.first.wpilibj2.command.Command;

public interface RobotContainerType {
    public Command getAutonomousCommand();
    public Command getTestCommand();
}
