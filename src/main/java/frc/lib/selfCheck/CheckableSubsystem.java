package frc.lib.selfCheck;

import edu.wpi.first.wpilibj2.command.Subsystem;

public interface CheckableSubsystem extends Subsystem {
     CheckCommand[] getCheckCommands();

}
