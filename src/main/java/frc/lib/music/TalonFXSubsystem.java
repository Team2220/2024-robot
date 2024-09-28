package frc.lib.music;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.Subsystem;

public interface TalonFXSubsystem extends Subsystem {
    TalonFX[] getTalonFXs();
}