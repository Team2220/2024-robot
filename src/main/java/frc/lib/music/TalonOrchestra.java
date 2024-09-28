package frc.lib.music;

import com.ctre.phoenix6.Orchestra;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.Command;

public class TalonOrchestra extends Command {

    Orchestra orchestra = new Orchestra();
    private String file;

    public TalonOrchestra(String file, TalonFXSubsystem... subsystems) {
        this.file = file;
        addRequirements(subsystems);
        for (TalonFXSubsystem subsystem : subsystems) {

            for (TalonFX talon : subsystem.getTalonFXs()) {
                orchestra.addInstrument(talon);
            }
        }
    }

    @Override
    public void initialize() {

        orchestra.loadMusic(file);
        orchestra.play();
    }

    @Override
    public void end(boolean interrupted) {
        orchestra.stop();
    }

    @Override
    public boolean isFinished() {
        return !orchestra.isPlaying();
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }
}