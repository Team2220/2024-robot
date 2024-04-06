package frc.lib.describingCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class NamedTrigger {
    String name;
    Trigger trigger;

    public NamedTrigger(String name, Trigger trigger) {
        this.name = name;
        this.trigger = trigger;
    }

    public NamedTrigger onTrue(String description, Command command) {
        trigger.onTrue(command);
        return this;
    }

    public NamedTrigger onFalse(String description, Command command) {
        trigger.onFalse(command);
        return this;
    }

    public NamedTrigger whileTrue(String description, Command command) {
        trigger.whileTrue(command);
        return this;
    }

    public NamedTrigger whileFalse(String description, Command command) {
        trigger.whileFalse(command);
        return this;
    }

    public NamedTrigger toggleOnTrue(String description, Command command) {
        trigger.toggleOnTrue(command);
       return this;
    }

    public NamedTrigger toggleOnFalse(String description, Command command) {
        trigger.toggleOnFalse(command);
        return this;
    }

}
