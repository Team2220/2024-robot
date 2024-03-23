package frc.lib.selfCheck;

import edu.wpi.first.wpilibj2.command.Command;

public abstract class CheckCommand extends Command {
    public abstract double getTimeoutSeconds();

    public abstract String getDescription();
}
