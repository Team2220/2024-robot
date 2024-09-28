package frc.lib.music;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix6.controls.MusicTone;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.Command;

public class MusicToneCommand extends Command {
    public final List<TalonFX> talonFXs = new ArrayList<>();
    public final double frequency;

    public MusicToneCommand(Note note, TalonFXSubsystem... subsystems) {
        this(note.getFrequency(), subsystems);
    }

    public MusicToneCommand(double frequency, TalonFXSubsystem... subsystems) {
        this.frequency = frequency;
        addRequirements(subsystems);
        for (TalonFXSubsystem subsystem : subsystems) {

            for (TalonFX talon : subsystem.getTalonFXs()) {
                talonFXs.add(talon);
            }
        }
    }

    private void setAllTalonsTo(double frequency) {
        var tone = new MusicTone(frequency);
        for (TalonFX i : talonFXs) {
            i.setControl(tone);
        }
    }

    @Override
    public void initialize() {
        setAllTalonsTo(frequency);
    }

    @Override
    public void end(boolean interrupted) {
        setAllTalonsTo(0);
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }

}