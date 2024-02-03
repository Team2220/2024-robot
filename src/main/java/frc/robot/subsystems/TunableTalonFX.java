package frc.robot.subsystems;

import java.util.function.DoubleConsumer;

import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.Slot1Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import frc.lib.tunables.TunableDouble;

public class TunableTalonFX {


  public static void addTunableTalonFX(TalonFX talonFX, double P, double I, double D, double G) {

    Slot0Configs talonFXConfigs = new Slot0Configs();

    new TunableDouble("P", P, "P", value -> {
      talonFXConfigs.kP = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

    new TunableDouble("I", I, "I", value -> {
      talonFXConfigs.kI = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

    new TunableDouble("D", D, "D", value -> {
      talonFXConfigs.kD = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

    new TunableDouble("G", G, "G", value -> {
      talonFXConfigs.kG = value;
      talonFX.getConfigurator().apply(talonFXConfigs);
    });

  }

}

    