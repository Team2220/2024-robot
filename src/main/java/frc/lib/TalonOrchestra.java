package frc.lib;

import com.ctre.phoenix6.Orchestra;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;



public class TalonOrchestra {
    static Orchestra orchestra = new Orchestra();

    public static Command playMusicCommand() {
        return Commands.runOnce(() -> {
            orchestra.clearInstruments();
            for (TalonFX talon : TalonFXRegistry.getTalonFXs()) {
                orchestra.addInstrument(talon);
            }
            orchestra.loadMusic("song1.chrp");

        }).andThen(Commands.waitSeconds(0.1)).andThen(Commands.runOnce(()->{orchestra.play();}));
    }
}
