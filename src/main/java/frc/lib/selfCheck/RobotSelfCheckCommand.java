package frc.lib.selfCheck;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class RobotSelfCheckCommand extends SequentialCommandGroup {
    public RobotSelfCheckCommand(Command onPassCommand, Command onFailedCommand, CheckableSubsystem... subsystems) {
        addRequirements(subsystems);
        for (CheckableSubsystem subsystem : subsystems) {
            var commands = subsystem.getCheckCommands();
            for (CheckCommand command : commands) {
                addCommands(
                        Commands.print(command.getDescription())
                                .andThen(command)
                                .finallyDo((interrupted) -> {
                                    if (interrupted) {
                                        anyFailed = true;
                                        System.out.println("[FAIL] " + command.getDescription());
                                    }
                                    // [PASS] description goes here
                                    // [FAIL] description goes here
                                    else {
                                        anyFailed = false;
                                        System.out.println("[PASS] " + command.getDescription());

                                    }
                                })
                                .withTimeout(command.getTimeoutSeconds()));
            }
        }
        addCommands(Commands.runOnce(() -> {
            DataLogManager.log("anyFailed=" + anyFailed);

        }));
        addCommands(Commands.either(onFailedCommand, onPassCommand, () -> anyFailed));
    }

    public boolean anyFailed = false;
}
