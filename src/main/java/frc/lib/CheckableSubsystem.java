package frc.lib;

import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public interface CheckableSubsystem extends Subsystem {
     CheckCommand[] getCheckCommands();

}
