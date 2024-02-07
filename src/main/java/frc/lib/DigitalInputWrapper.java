package frc.lib;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardComponent;

public class DigitalInputWrapper {
    int channel;
    private String name;
    private boolean inverted;
    DigitalInput dInput;

    public DigitalInputWrapper(int channel, String name, boolean inverted) {
        channel = this.channel;
        name = this.name;
        inverted = this.inverted;
        dInput = new DigitalInput(channel);
    }

    public boolean get() {
        if (inverted) {
            return !dInput.get();
        } else {
            return dInput.get();
        }
    }

    // GenericEntry digInputStatus = Shuffleboard.getTab("sensors").addBoolean(name,
    // dInput.get());
}
