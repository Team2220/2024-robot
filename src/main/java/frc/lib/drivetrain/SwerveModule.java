// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.lib.drivetrain;

import static edu.wpi.first.units.Units.Degrees;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.controls.VelocityDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.devices.PWMEncoder;
import frc.lib.selfCheck.CheckCommand;
import frc.lib.selfCheck.SwerveModuleSelfCheck;
import frc.lib.selfCheck.UnwrappedTalonSpinCheck;
import frc.lib.tunables.TunableDouble;

public class SwerveModule implements ShuffleBoardTabWrapper {
  GenericEntry speed;
  GenericEntry angle;
  GenericEntry drivePositionEntry;
  private final TalonFX driveMotor;
  private final TalonFX turningMotor;

  private final PWMEncoder turningEncoder;

  public static final double DT_WHEEL_DIAMETER = Units.inchesToMeters(4);
  public static final double DT_DRIVE_GEAR_RATIO = (50.0 / 14.0) * (16.0 / 28.0) * (45.0 / 15.0);
  public static final double DT_STEER_GEAR_RATIO = 150.0 / 7.0;
  // Drive gear ratio (that number is the number of revolutions of the motor to
  // get one revolution of the output)
  // public static final double DT_DRIVE_GEAR_RATIO =
  // Constants.SwerveModule.DT_DRIVE_GEAR_RATIO;
  // Drive motor inverted
  // public static final boolean DT_DRIVE_MOTOR_INVERTED = true;

  // Steer gear ratio (that number is the number of revolutions of the steer motor
  // to get one revolution of the output)
  // public static final double DT_STEER_GEAR_RATIO =
  // Constants.SwerveModule.DT_STEER_GEAR_RATIO;
  // Steer motor inverted
  // public static final boolean DT_STEER_MOTOR_INVERTED = false;

  // Steer encoder gear ratio
  // public static final double DT_STEER_ENCODER_GEAR_RATIO = 1;
  // Steer encoder inverted
  // public static final boolean DT_STEER_ENCODER_INVERTED = false;

  private double offset;
  private String name;

  public SwerveModule(
      String name,
      int driveMotorChannel,
      int turningMotorChannel,
      int turningEncoderChannelA,
      double offset) {
    this.offset = offset + 90; // when we zero it's to 90 deg on the unit circle
    this.name = name;
    driveMotor = new TalonFX(driveMotorChannel);
    turningMotor = new TalonFX(turningMotorChannel);
    var driveConfig = makeConfiguration();
    var turningconfig = makeConfiguration();

    turningEncoder = new PWMEncoder(turningEncoderChannelA);
    speed = Shuffleboard.getTab("swerve").add(name + " speed", 0).getEntry();
    angle = Shuffleboard.getTab("swerve").add(name + " angle", 0).getEntry();
    drivePositionEntry = Shuffleboard.getTab("swerve").add(name + " drivePostion", 0).getEntry();
    Shuffleboard.getTab("swerve").addDouble(name + " abs encoder", turningEncoder::getPosition);
    Shuffleboard.getTab("swerve").addDouble(name + " motor encoder", () -> getTurningMotorRotation2d().getDegrees());

    SwerveModule.DT_DRIVE_P.addChangeListener((isInit, value) -> {
      driveConfig.Slot0.kP = value;
      driveMotor.getConfigurator().apply(driveConfig);
    });
    SwerveModule.DT_DRIVE_I.addChangeListener((isInit, value) -> {
      driveConfig.Slot0.kI = value;
      if (!isInit) {
        driveMotor.getConfigurator().apply(driveConfig);
      }
    });
    SwerveModule.DT_DRIVE_D.addChangeListener((isInit, value) -> {
      driveConfig.Slot0.kD = value;
      if (!isInit) {
        driveMotor.getConfigurator().apply(driveConfig);
      }
    });
    SwerveModule.DT_DRIVE_F.addChangeListener((isInit, value) -> {
      driveConfig.Slot0.kV = value;
      if (!isInit) {
        driveMotor.getConfigurator().apply(driveConfig);
      }
    });
    SwerveModule.DT_STEER_P.addChangeListener((isInit, value) -> {
      turningconfig.Slot0.kP = value;
      if (!isInit) {
        turningMotor.getConfigurator().apply(turningconfig);
      }
    });
    SwerveModule.DT_STEER_I.addChangeListener((isInit, value) -> {
      turningconfig.Slot0.kI = value;
      if (!isInit) {
        turningMotor.getConfigurator().apply(turningconfig);

      }
    });

    SwerveModule.DT_STEER_D.addChangeListener((isInit, value) -> {
      turningconfig.Slot0.kD = value;
      if (!isInit) {
        turningMotor.getConfigurator().apply(turningconfig);
      }
    });
    SwerveModule.DT_STEER_F.addChangeListener((isInit, value) -> {
      turningconfig.Slot0.kV = value;
      if (!isInit) {
        turningMotor.getConfigurator().apply(turningconfig);
      }
    });

    zeroTurningMotor();

    // Shuffleboard.getTab("current")
    // .addDouble(name, ()->{
    // return
    // turningMotor.getTorqueCurrent().getValueAsDouble();
    // })
    // .withWidget(BuiltInWidgets.kGraph);

    // if (Constants.isGraphsEnabled) {
    // Shuffleboard.getTab("swerve")
    // .addDouble(name, this::getDriveVelocity)
    // .withWidget(BuiltInWidgets.kGraph).withSize(1, 1);
    // }

    driveMotor.getConfigurator().apply(driveConfig);
    turningMotor.getConfigurator().apply(turningconfig);
  }

  public void zeroTurningMotor() {
    double degrees = getAngle().getDegrees();
    turningMotor.setPosition(-angleToEncoderTicks(degrees));
    System.out.println("zero turingin motor " + name + ": " + degrees);
  }

  private static TalonFXConfiguration makeConfiguration() {
    var config = new TalonFXConfiguration();
    config.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    config.CurrentLimits.StatorCurrentLimit = 60;
    config.CurrentLimits.StatorCurrentLimitEnable = true;
    config.CurrentLimits.SupplyCurrentLimit = 60;
    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.Voltage.PeakForwardVoltage = 10;
    config.Voltage.PeakReverseVoltage = -10;
    config.Audio.BeepOnBoot = false;
    config.Audio.BeepOnConfig = false;
    config.Audio.AllowMusicDurDisable = true;
    return config;

  }

  public SwerveModuleState getState() {
    return new SwerveModuleState(
        getDriveVelocity(), getAngle());
  }

  private double getDriveVelocity() {
    double ticks = driveMotor.getVelocity().getValueAsDouble();
    double revolutionsMotorToRevolutionsWheel = 1.0 / DT_DRIVE_GEAR_RATIO // Reduction from motor to output
        * (1.0 / (SwerveModule.DT_WHEEL_DIAMETER * Math.PI));

    return ticks * revolutionsMotorToRevolutionsWheel;
  }

  public CheckCommand[] getCheckCommands() {
    return new CheckCommand[] {
        new UnwrappedTalonSpinCheck("backLeftDrive", getDriveMotor(), true),
        new UnwrappedTalonSpinCheck("backLeftDrive", getDriveMotor(), false),
        new UnwrappedTalonSpinCheck("backLeftTurn", getTurningMotor(), true),
        new UnwrappedTalonSpinCheck("backLeftTurn", getTurningMotor(), false),
        new SwerveModuleSelfCheck(this, Degrees.of(90), Degrees.of(5)),
    };
  }

  public static final TunableDouble DT_DRIVE_P = new TunableDouble("DT_DRIVE_P", 0.025, "swerve").setSpot(0, 0);
  public static final TunableDouble DT_DRIVE_I = new TunableDouble("DT_DRIVE_I", 0, "swerve").setSpot(1, 0);
  public static final TunableDouble DT_DRIVE_D = new TunableDouble("DT_DRIVE_D", 0.00001, "swerve").setSpot(2, 0);
  public static final TunableDouble DT_DRIVE_F = new TunableDouble("DT_DRIVE_F", 0, "swerve").setSpot(3, 0);

  // PID values for the steer motor
  public static final TunableDouble DT_STEER_P = new TunableDouble("DT_STEER_P", 1, "swerve").setSpot(0, 1);
  public static final TunableDouble DT_STEER_I = new TunableDouble("DT_STEER_I", 0, "swerve").setSpot(1, 1);
  public static final TunableDouble DT_STEER_D = new TunableDouble("DT_STEER_D", 0.0001, "swerve").setSpot(2, 1);
  public static final TunableDouble DT_STEER_F = new TunableDouble("DT_STEER_F", 0, "swerve").setSpot(3, 1);

  private double getDrivePosition() {
    double ticks = driveMotor.getRotorPosition().getValueAsDouble();
    double revolutionsMotorToRevolutionsWheel = 1.0 / DT_DRIVE_GEAR_RATIO // Reduction from motor to output
        * (1.0 * (SwerveModule.DT_WHEEL_DIAMETER * Math.PI));

    return ticks * revolutionsMotorToRevolutionsWheel;
  }

  private double mpsToEncoderTicks(double mps) {
    double wheelRevolutions = (DT_WHEEL_DIAMETER * Math.PI);
    double motorRev = mps / wheelRevolutions * DT_DRIVE_GEAR_RATIO;
    double ticks = motorRev;
    return ticks;
  }

  private double angleToEncoderTicks(double angle) {
    double angleToWheelRev = angle / 360.0;
    double motorRev = angleToWheelRev * DT_STEER_GEAR_RATIO;
    return motorRev;
  }

  private double steerEncoderTicksToAngle(double ticks) {
    double motorRotation = ticks;
    double moduleRotation = motorRotation / DT_STEER_GEAR_RATIO;
    double angle = moduleRotation * 360;
    return angle;
  }

  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(
        getDrivePosition(), getAngle());
  }

  private Rotation2d getAngle() {
    var rAngle = Rotation2d.fromDegrees(turningEncoder.getPosition() - offset);
    return Rotation2d.fromDegrees(MathUtil.inputModulus(rAngle.getDegrees(), 0, 360));
  }

  public void setDesiredState(SwerveModuleState desiredState) {
    drivePositionEntry.setDouble(getDrivePosition());

    Rotation2d rotation2d = getTurningMotorRotation2d();
    SwerveModuleState state = SwerveModuleState.optimize(desiredState, rotation2d);
    speed.setDouble(mpsToEncoderTicks(state.speedMetersPerSecond));
    driveMotor.setControl(new VelocityDutyCycle(mpsToEncoderTicks(state.speedMetersPerSecond) * -1));
    turningMotor.setControl(new PositionDutyCycle(
        angleToEncoderTicks(convertAngle(rotation2d.getDegrees(), state.angle.getDegrees()) * -1)));

  }

  private Rotation2d getTurningMotorRotation2d() {
    return Rotation2d
        .fromDegrees(steerEncoderTicksToAngle(-turningMotor.getPosition().getValueAsDouble()));
  }

  public static double convertAngle(double start, double end) {
    int angleRevo = (int) (start / 360);
    start %= 360;
    end %= 360;
    double change = start - end;
    if (change > 270) {
      end += 360;
    } else if (change < -270) {
      end -= 360;
    }
    end += 360 * angleRevo;
    return end;
  }

  public TalonFX getDriveMotor() {
    return driveMotor;
  }

  public TalonFX getTurningMotor() {
    return turningMotor;
  }

  @Override
  public String getName() {
    return name;
  }

  public void toggleCoast() {

  };
}
