package frc.lib;

import edu.wpi.first.wpilibj2.command.Command;

public interface RobotContainerInterface {
    Command getAutonomousCommand();
    Command getTestCommand();
}

// what does this do? 