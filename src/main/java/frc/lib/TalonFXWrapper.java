package frc.lib;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.MusicTone;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.controls.VoltageOut;

import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;
import static frc.lib.units.UnitsUtil.RotationsPerSecCubed;
import static frc.lib.units.UnitsUtil.RotationsPerSecSquared;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.lib.faults.Fault;
import frc.lib.tunables.TunableDouble;
import frc.lib.tunables.TunableMeasure;
import frc.lib.units.UnitsUtil;

public class TalonFXWrapper {
    private TalonFX talon;
    private TalonFX followerFx;
    private String name;
    private TalonFXConfiguration talonFXConfigs;
    // private static Fault fault = new Fault("TalonFX device disconnected");
    // private StatusSignal<Integer> firmwareVersionSignal;
    private Fault softLimitOverrideFault;

    public TalonFXWrapper(
            int id,
            String name,
            boolean isInverted,
            NeutralModeValue neutralMode,
            double gearRatio,
            double P,
            double I,
            double D,
            Measure<Velocity<Velocity<Angle>>> Acceleration,
            Measure<Velocity<Angle>> CruiseVelocity,
            Measure<Velocity<Velocity<Velocity<Angle>>>> Jerk,
            boolean forwardSoftLimitEnable,
            boolean reverseSoftLimitEnable,
            Measure<Angle> forwardSoftLimitTreshold,
            Measure<Angle> reverseSoftLimitThreshold,
            FollowerConfig followerConfig) {
        talon = new TalonFX(id);
        this.name = name;
        // firmwareVersionSignal = talon.getVersion();
        // TalonFXLogPowerFaults.setupChecks(this);
        softLimitOverrideFault = new Fault(getName() + " Device ID: " + id + " Soft Limit Overrided");

        talonFXConfigs = new TalonFXConfiguration();

        talonFXConfigs.MotorOutput.NeutralMode = neutralMode;

        talonFXConfigs.MotorOutput.Inverted = isInverted ? InvertedValue.Clockwise_Positive
                : InvertedValue.CounterClockwise_Positive;

        talonFXConfigs.Audio.BeepOnBoot = false;
        talonFXConfigs.Audio.BeepOnConfig = false;
        talonFXConfigs.Audio.AllowMusicDurDisable = true;

        talonFXConfigs.CurrentLimits.StatorCurrentLimit = 60;
        talonFXConfigs.CurrentLimits.StatorCurrentLimitEnable = true;
        talonFXConfigs.CurrentLimits.SupplyCurrentLimit = 60;
        talonFXConfigs.CurrentLimits.SupplyCurrentLimitEnable = true;

        talonFXConfigs.SoftwareLimitSwitch.ForwardSoftLimitEnable = forwardSoftLimitEnable;
        talonFXConfigs.SoftwareLimitSwitch.ReverseSoftLimitEnable = reverseSoftLimitEnable;
        talonFXConfigs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = forwardSoftLimitTreshold.in(Rotations);
        talonFXConfigs.SoftwareLimitSwitch.ReverseSoftLimitThreshold = reverseSoftLimitThreshold.in(Rotations);

        talonFXConfigs.Feedback.SensorToMechanismRatio = gearRatio;

        talonFXConfigs.Voltage.PeakForwardVoltage = 10;
        talonFXConfigs.Voltage.PeakReverseVoltage = -10;

        talon.getConfigurator().apply(talonFXConfigs);
        if (followerConfig != null) {
            followerFx = new TalonFX(followerConfig.id);
            followerFx.getConfigurator().apply(talonFXConfigs);
            followerFx.setControl(new Follower(id, followerConfig.isInverted));
        }

        new TunableDouble("P", P, getName(), value -> {
            talonFXConfigs.Slot0.kP = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableDouble("I", I, getName(), value -> {
            talonFXConfigs.Slot0.kI = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableDouble("D", D, getName(), value -> {
            talonFXConfigs.Slot0.kD = value;
            talon.getConfigurator().apply(talonFXConfigs);
        });

        // new TunableDouble("G", G, getName(), value -> {
        // talonFXConfigs.Slot0.kG = value;
        // talon.getConfigurator().apply(talonFXConfigs);
        // });

        new TunableMeasure<>("Acceleration", Acceleration, getName(), value -> {
            talonFXConfigs.MotionMagic.MotionMagicAcceleration = value.in(RotationsPerSecond.per(Seconds));
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableMeasure<>("CruiseVelocity", CruiseVelocity, getName(), value -> {
            talonFXConfigs.MotionMagic.MotionMagicCruiseVelocity = value.in(RotationsPerSecond);
            talon.getConfigurator().apply(talonFXConfigs);
        });

        new TunableMeasure<>("Jerk", Jerk, getName(), value -> {
            talonFXConfigs.MotionMagic.MotionMagicJerk = value
                    .in(RotationsPerSecond.per(Seconds).per(Seconds));
            talon.getConfigurator().apply(talonFXConfigs);
        });

        // DriverStationTriggers.isDisabled().debounce(7).onFalse(
        // Commands.runOnce(() -> {
        // this.setVoltageOut(Units.Volts.of(0));
        // talonFXConfigs.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        // talon.getConfigurator().apply(talonFXConfigs, 0);
        // }).ignoringDisable(true));

        // DriverStationTriggers.isDisabled().debounce(7).onTrue(Commands.runOnce(() ->
        // {
        // talonFXConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        // talon.getConfigurator().apply(talonFXConfigs, 0);
        // }).ignoringDisable(true));
    }

    public TalonFXWrapper(int id, String name, boolean isInverted, NeutralModeValue neutralMode) {
        this(
                id,
                name,
                isInverted,
                neutralMode,
                1,
                0,
                0,
                0,
                RotationsPerSecSquared.of(0),
                RotationsPerSecond.of(0),
                RotationsPerSecCubed.of(0),
                false,
                false,
                Rotations.of(0),
                Rotations.of(0),
                null);
    }

    public void setSoftLimitsEnabled(boolean enabled) {
        talonFXConfigs.SoftwareLimitSwitch.ForwardSoftLimitEnable = enabled;
        talonFXConfigs.SoftwareLimitSwitch.ReverseSoftLimitEnable = enabled;
        talon.getConfigurator().apply(talonFXConfigs);

        softLimitOverrideFault.setIsActive(enabled);
    }

    boolean isPositionBeingHeld = false;

    public void holdPosition() {
        if (!isPositionBeingHeld) {
            isPositionBeingHeld = true;
            double position = talon.getPosition().getValueAsDouble();
            talon.setControl(new MotionMagicVoltage(position));
        }
    }

    public String getName() {
        return name + " (TalonFX " + talon.getDeviceID() + ")";
    }

    public TalonFX getTalon() {
        return talon;
    }

    public void checkFault() {
        // if (firmwareVersionSignal.refresh().getError() != StatusCode.OK) {
        // fault.setIsActive(true);
        // }
    }

    public void setPosition(double newPosition) {
        talon.setPosition(newPosition);
    }

    public Measure<Angle> getPosition() {
        return Units.Rotations.of(talon.getPosition().getValueAsDouble());
    }

    public void setVelocity(Measure<Velocity<Angle>> speed) {
        talon.setControl(new VelocityVoltage(speed.in(RotationsPerSecond)));
        isPositionBeingHeld = false;
    }

    public Measure<Velocity<Angle>> getVelocity() {
        return Units.RotationsPerSecond.of(talon.getVelocity().getValueAsDouble());
    }

    public boolean isAtReference(Measure<Velocity<Angle>> speed, Measure<Velocity<Angle>> tolerance) {
        var diff = (getVelocity().minus(speed));
        return UnitsUtil.abs(diff).lte(tolerance);
    }

    // multaplying by 10 to convert duty cycle to voltage
    public void set(double speed) {
        talon.setControl(new VoltageOut(speed * 10));
        isPositionBeingHeld = false;
    }

    public void setMotionMagicVoltage(Measure<Angle> position) {
        talon.setControl(new PositionVoltage(position.in(Rotations)));
        isPositionBeingHeld = false;
    }

    public void setVoltageOut(Measure<Voltage> voltage) {
        talon.setControl(new VoltageOut(voltage.in(Volts)));
        isPositionBeingHeld = false;
    }

    public void setDutyCycleOut(double cycle) {
        talon.setControl(new DutyCycleOut(cycle));
        isPositionBeingHeld = false;
    }

    public void setMusicTone(double frequency) {
        talon.setControl(new MusicTone(frequency));
        isPositionBeingHeld = false;
    }

    public static record FollowerConfig(int id, boolean isInverted) {
    }

    public boolean isAtPositionReference(Measure<Angle> speed, Measure<Angle> tolerance) {
        var diff = (getPosition().minus(speed));
        return UnitsUtil.abs(diff).lte(tolerance);
    }
}
