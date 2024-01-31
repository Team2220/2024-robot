package frc.lib;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class RobotSelfCheckCommand extends SequentialCommandGroup{
    public RobotSelfCheckCommand(CheckableSubsystem[] subsystems){
    for (CheckableSubsystem subsystem : subsystems) {
        var commands = subsystem.getCheckCommands();
        for (CheckCommand command : commands){
            addCommands(command.finallyDo((interrupted)->{
            if (interrupted) {
                anyFailed = true;
            }
            }));
        }
    }
    }
    public boolean anyFailed = false;
}
