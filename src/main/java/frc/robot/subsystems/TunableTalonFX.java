package frc.robot.subsystems;

import java.util.function.DoubleConsumer;

import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.Slot1Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import frc.lib.TalonFXWrapper;
import frc.lib.tunables.TunableDouble;

public class TunableTalonFX {


  public static void addTunableTalonFX(
    TalonFXWrapper talonFXWrapper,
    double P, 
    double I, 
    double D, 
    double G,
    double Acceleration,
    double CruiseVelocity,
    double Jerk
   ) {
    TalonFX talonFX = talonFXWrapper.getTalon();
    TalonFXConfiguration talonFXConfigs = new TalonFXConfiguration();

    new TunableDouble("P", P, talonFXWrapper.getName(), value -> {
      talonFXConfigs.Slot0.kP = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

    new TunableDouble("I", I, talonFXWrapper.getName(), value -> {
      talonFXConfigs.Slot0.kI = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

    new TunableDouble("D", D, talonFXWrapper.getName(), value -> {
      talonFXConfigs.Slot0.kD = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

    new TunableDouble("G", G, talonFXWrapper.getName(), value -> {
      talonFXConfigs.Slot0.kG = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

    new TunableDouble("Acceleration", Acceleration, talonFXWrapper.getName(), value -> {
      talonFXConfigs.MotionMagic.MotionMagicAcceleration = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

    new TunableDouble("CruiseVelocity", CruiseVelocity, talonFXWrapper.getName(), value -> {
      talonFXConfigs.MotionMagic.MotionMagicCruiseVelocity = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

    new TunableDouble("Jerk", Jerk, talonFXWrapper.getName(), value -> {
      talonFXConfigs.MotionMagic.MotionMagicJerk = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

  }
  

}

    