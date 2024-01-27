package frc.lib;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class CheckableSubsystem extends SubsystemBase {
    abstract CheckCommand[] getCheckCommands();

}
