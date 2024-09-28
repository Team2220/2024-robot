package frc.lib.devices;

import com.ctre.phoenix6.controls.DutyCycleOut;
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

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.Current;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Time;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.Velocity;
import edu.wpi.first.units.Voltage;
import frc.lib.ShuffleBoardTabWrapper;
import frc.lib.eventLoops.EventLoops;
import frc.lib.faults.Fault;
import frc.lib.tunables.TunableDebouncer;
import frc.lib.tunables.TunableDouble;
import frc.lib.tunables.TunableEnum;
import frc.lib.tunables.TunableMeasure;
import frc.lib.units.UnitsUtil;

public class TalonFXWrapper implements ShuffleBoardTabWrapper {
    private TalonFX talon;
    private TalonFX followerFx;
    private String name;
    private TalonFXConfiguration talonFXConfigs;
    private TunableDebouncer tunableDebouncer;
    // private static Fault fault = new Fault("TalonFX device disconnected");
    // private StatusSignal<Integer> firmwareVersionSignal;
    private Fault softLimitOverrideFault;
    private TunableMeasure<Current> stallCurrentLimit;
    private TunableMeasure<Velocity<Angle>> stallRotationLimit;
    //private Measure<Velocity<Angle>> velocitySetPoint = Units.RPM.of(0);

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
            FollowerConfig followerConfig,
            Measure<Time> debounceTime,
            Measure<Current> stallCurrentThreshold,
            Measure<Velocity<Angle>> stallRotationThreshold) {
        talon = new TalonFX(id);
        this.name = name;
        // firmwareVersionSignal = talon.getVersion();
        // TalonFXLogPowerFaults.setupChecks(this);
        softLimitOverrideFault = new Fault(getName() + " Device ID: " + id + " Soft Limit Overrided");

        talonFXConfigs = new TalonFXConfiguration();

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

        tunableDebouncer = new TunableDebouncer("Stall Debounce Time", getName(), debounceTime,
                Debouncer.DebounceType.kBoth);

        new TunableDouble("P", P, getName(), (isInit, value) -> {
            talonFXConfigs.Slot0.kP = value;
            if (!isInit) {
                talon.getConfigurator().apply(talonFXConfigs);
            }

        });

        new TunableEnum<NeutralModeValue>("breakCoast", neutralMode, NeutralModeValue.class, getName(),
                (isInit, value) -> {
                    talonFXConfigs.MotorOutput.NeutralMode = value;
                    if (!isInit) {
                        talon.getConfigurator().apply(talonFXConfigs);
                    }
                });

        new TunableDouble("I", I, getName(), (isInit, value) -> {
            talonFXConfigs.Slot0.kI = value;
            if (!isInit) {
                talon.getConfigurator().apply(talonFXConfigs);
            }

        });

        new TunableDouble("D", D, getName(), (isInit, value) -> {
            talonFXConfigs.Slot0.kD = value;
            if (!isInit) {
                talon.getConfigurator().apply(talonFXConfigs);
            }
        });

        addGraph("Curent", () -> getTorqueCurrent(), Units.Amps);
        addGraph("velocity", () -> getVelocity(), Units.RPM);
        // new TunableDouble("G", G, getName(), value -> {
        // talonFXConfigs.Slot0.kG = value;
        // talon.getConfigurator().apply(talonFXConfigs);
        // });

        new TunableMeasure<>("Acceleration", Acceleration, getName(), (isInit, value) -> {
            talonFXConfigs.MotionMagic.MotionMagicAcceleration = value.in(RotationsPerSecond.per(Seconds));
            if (!isInit) {
                talon.getConfigurator().apply(talonFXConfigs);
            }
        });

        new TunableMeasure<>("CruiseVelocity", CruiseVelocity, getName(), (isInit, value) -> {
            talonFXConfigs.MotionMagic.MotionMagicCruiseVelocity = value.in(RotationsPerSecond);
            if (!isInit) {
                talon.getConfigurator().apply(talonFXConfigs);
            }
        });

        new TunableMeasure<>("Jerk", Jerk, getName(), (isInit, value) -> {
            talonFXConfigs.MotionMagic.MotionMagicJerk = value
                    .in(RotationsPerSecond.per(Seconds).per(Seconds));
            if (!isInit) {
                talon.getConfigurator().apply(talonFXConfigs);
            }
        });

        this.stallCurrentLimit = new TunableMeasure<>("Stall Current Threshold", stallCurrentThreshold, getName());
        this.stallRotationLimit = new TunableMeasure<>("Stall Rotation Threshold", stallRotationThreshold, getName());
        // DriverStationTriggers.isDisabled().debounce(15).onTrue(
        // Commands.runOnce(() -> {
        // this.setVoltageOut(Units.Volts.of(0));
        // setNeutralMode(NeutralModeValue.Coast);
        // }).ignoringDisable(true));

        // DriverStationTriggers.isDisabled().debounce(15).onFalse(Commands.runOnce(()
        // ->
        // {
        // setNeutralMode(NeutralModeValue.Brake);
        // }).ignoringDisable(true));

        Fault.autoUpdating(getName() + " Stalled", EventLoops.everyLoop, this::isStalled);

        talon.getConfigurator().apply(talonFXConfigs);

        if (followerFx != null) {

            followerFx.getConfigurator().apply(talonFXConfigs);

        }
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
                null,
                Units.Seconds.of(1),
                Units.Amps.of(75),
                Units.RotationsPerSecond.of(1));
    }

    public void setSoftLimitsEnabled(boolean enabled) {
        talonFXConfigs.SoftwareLimitSwitch.ForwardSoftLimitEnable = enabled;
        talonFXConfigs.SoftwareLimitSwitch.ReverseSoftLimitEnable = enabled;
        configureTalons();

        softLimitOverrideFault.setIsActive(enabled);
    }

    private void configureTalons() {
        talon.getConfigurator().apply(talonFXConfigs, 0.000001);
        if (followerFx != null) {
            followerFx.getConfigurator().apply(talonFXConfigs, 0.000001);
        }
    }

    public void setNeutralMode(NeutralModeValue value) {
        if (talonFXConfigs.MotorOutput.NeutralMode != value) {
            talonFXConfigs.MotorOutput.NeutralMode = value;
            configureTalons();
        }
    }

    public NeutralModeValue getNeutralMode() {
        return talonFXConfigs.MotorOutput.NeutralMode;
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

    public void setPosition(double newPosition) {
        talon.setPosition(newPosition, 0.00000000000001);
        if (followerFx != null) {
            followerFx.setPosition(newPosition, 0.00000000000001);
        }
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

    public Measure<Current> getTorqueCurrent() {
        return Units.Amps.of(talon.getTorqueCurrent().getValueAsDouble());
    }

    // From: https://www.chiefdelphi.com/t/falcon-500-detecting-motor-stalls/428106
    private boolean isStalledInternal() {
        if (getTorqueCurrent().gte(stallCurrentLimit.getValue())) {
            return getVelocity().lte(stallRotationLimit.getValue());
        } else {
            return false;
        }
    }

    public boolean isStalled() {
        return tunableDebouncer.calculate(isStalledInternal());
    }
}