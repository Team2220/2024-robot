package frc.lib.controllers;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.lib.faults.Fault;

public class CommandJoystickWrapper {

    private final CommandJoystick joystick;
    private final double joystickDeadband;
    private final double throttleDeadZone;

    public CommandJoystickWrapper(String name, int inPort, double joystickDeadband, double throttleDeadZone,
            boolean faultsWhenDisconnected) {
        joystick = new CommandJoystick(inPort);
        this.joystickDeadband = joystickDeadband;
        this.throttleDeadZone = throttleDeadZone;

        if (faultsWhenDisconnected) {
            Fault.autoUpdating("Controller " + name + " (Port: " + inPort + ") is disconnected.", () -> !isConnected());
        }
    }

    public CommandJoystickWrapper(String name, int inPort, boolean faultsWhenDisconnected) {
        this(name, inPort, .15, .05, faultsWhenDisconnected);
    }

    public boolean isConnected() {
        return DriverStation.isJoystickConnected(joystick.getHID().getPort());
    }

    private Trigger lazyTrigger(Trigger trigger) {
        return new Trigger(() -> {
            if (isConnected()) {
                return trigger.getAsBoolean();
            } else {
                return false;
            }
        });
    }

    private double lazyDouble(DoubleSupplier doubleSup) {
        if (isConnected()) {
            return doubleSup.getAsDouble();
        } else {
            return 0.0;
        }
    }

    public Joystick getHID() {
        return joystick.getHID();
    }

    public Trigger button(int id) {
        return lazyTrigger(joystick.button(id));
    }

    public Trigger trigger() {
        return lazyTrigger(joystick.trigger());
    }

    public double getX() {
        return lazyDouble(() -> MathUtil.applyDeadband(joystick.getX(), joystickDeadband));
    }

    public double getY() {
        // flip y axis so that up is positive
        return lazyDouble(() -> -MathUtil.applyDeadband(joystick.getY(), joystickDeadband));
    }

    public double getZ() {
        return lazyDouble(() -> MathUtil.applyDeadband(joystick.getZ(), joystickDeadband));
    }

    // did math to make it a precentage 0 - 1
    public double getThrottle() {
        return lazyDouble(() -> {
            double throttle = (joystick.getThrottle() + 1.0) / 2.0;
            return MathUtil.applyDeadband(throttle, throttleDeadZone);
        });
    }

    public Trigger joysticksTrigger() {
        return new Trigger(() -> {
            return Math.abs(getX()) > 0
                    || Math.abs(getY()) > 0
                    || Math.abs(getZ()) > 0;
        });
    }

    public Trigger xTrigger() {
        return new Trigger(() -> {
            return Math.abs(getX()) > 0;
        });
    }

    public Trigger yTrigger() {
        return new Trigger(() -> {
            return Math.abs(getY()) > 0;
        });
    }

    public Trigger zTrigger() {
        return new Trigger(() -> {
            return Math.abs(getZ()) > 0;
        });
    }

    private Trigger pov(int angle) {
        return lazyTrigger(joystick.pov(angle));
    }

    /**
     * Constructs a Trigger instance based around the 0 degree angle (up) of the
     * default (index 0) POV
     * on the HID, attached to {@link CommandScheduler#getDefaultButtonLoop() the
     * default command
     * scheduler button loop}.
     *
     * @return a Trigger instance based around the 0 degree angle of a POV on the
     *         HID.
     */
    public Trigger povUp() {
        return pov(0);
    }

    /**
     * Constructs a Trigger instance based around the 45 degree angle (right up) of
     * the default (index
     * 0) POV on the HID, attached to {@link CommandScheduler#getDefaultButtonLoop()
     * the default
     * command scheduler button loop}.
     *
     * @return a Trigger instance based around the 45 degree angle of a POV on the
     *         HID.
     */
    public Trigger povUpRight() {
        return pov(45);
    }

    /**
     * Constructs a Trigger instance based around the 90 degree angle (right) of the
     * default (index 0)
     * POV on the HID, attached to {@link CommandScheduler#getDefaultButtonLoop()
     * the default command
     * scheduler button loop}.
     *
     * @return a Trigger instance based around the 90 degree angle of a POV on the
     *         HID.
     */
    public Trigger povRight() {
        return pov(90);
    }

    /**
     * Constructs a Trigger instance based around the 135 degree angle (right down)
     * of the default
     * (index 0) POV on the HID, attached to
     * {@link CommandScheduler#getDefaultButtonLoop() the
     * default command scheduler button loop}.
     *
     * @return a Trigger instance based around the 135 degree angle of a POV on the
     *         HID.
     */
    public Trigger povDownRight() {
        return pov(135);
    }

    /**
     * Constructs a Trigger instance based around the 180 degree angle (down) of the
     * default (index 0)
     * POV on the HID, attached to {@link CommandScheduler#getDefaultButtonLoop()
     * the default command
     * scheduler button loop}.
     *
     * @return a Trigger instance based around the 180 degree angle of a POV on the
     *         HID.
     */
    public Trigger povDown() {
        return pov(180);
    }

    /**
     * Constructs a Trigger instance based around the 225 degree angle (down left)
     * of the default
     * (index 0) POV on the HID, attached to
     * {@link CommandScheduler#getDefaultButtonLoop() the
     * default command scheduler button loop}.
     *
     * @return a Trigger instance based around the 225 degree angle of a POV on the
     *         HID.
     */
    public Trigger povDownLeft() {
        return pov(225);
    }

    /**
     * Constructs a Trigger instance based around the 270 degree angle (left) of the
     * default (index 0)
     * POV on the HID, attached to {@link CommandScheduler#getDefaultButtonLoop()
     * the default command
     * scheduler button loop}.
     *
     * @return a Trigger instance based around the 270 degree angle of a POV on the
     *         HID.
     */
    public Trigger povLeft() {
        return pov(270);
    }

    /**
     * Constructs a Trigger instance based around the 315 degree angle (left up) of
     * the default (index
     * 0) POV on the HID, attached to {@link CommandScheduler#getDefaultButtonLoop()
     * the default
     * command scheduler button loop}.
     *
     * @return a Trigger instance based around the 315 degree angle of a POV on the
     *         HID.
     */
    public Trigger povUpLeft() {
        return pov(315);
    }

    /**
     * Constructs a Trigger instance based around the center (not pressed) position
     * of the default
     * (index 0) POV on the HID, attached to
     * {@link CommandScheduler#getDefaultButtonLoop() the
     * default command scheduler button loop}.
     *
     * @return a Trigger instance based around the center position of a POV on the
     *         HID.
     */
    public Trigger povCenter() {
        return pov(-1);
    }
}
