package frc.lib;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class RobotSelfCheckCommand extends SequentialCommandGroup{
    public RobotSelfCheckCommand(CheckableSubsystem[] subsystems){

    }
    public boolean anyFailed = false;
}
