package frc.lib.controllers;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.lib.faults.Fault;

public class CommandXBoxWrapper {

    CommandXboxController xbox;
    double joystickDeadband;
    double triggerDeadZone;
    boolean faultsWhenDisconnected;
    String name;

    public CommandXBoxWrapper(String name, int inPort, double joystickDeadband, double triggerDeadZone,
            boolean faultsWhenDisconnected) {
        xbox = new CommandXboxController(inPort);
        this.name = name;
        this.faultsWhenDisconnected = faultsWhenDisconnected;
        this.joystickDeadband = joystickDeadband;
        this.triggerDeadZone = triggerDeadZone;

        Fault.autoUpdating("Controller " + name + " (Port: " + inPort + ") is disconnected.", () -> !isConnected());
    }

    public CommandXBoxWrapper(String name, int inPort) {
        this(name, inPort, .15, .1, true);
    }

    /**
     * Get the underlying GenericHID object.
     *
     * @return the wrapped GenericHID object
     */
    public XboxController getHID() {
        return xbox.getHID();
    }

    /**
     * Constructs an event instance around the left bumper's digital signal.
     *
     * @return an event instance representing the left bumper's digital signal
     *         attached to the {@link
     *         CommandScheduler#getDefaultButtonLoop() default scheduler button
     *         loop}.
     * @see #leftBumper(EventLoop)
     */
    public Trigger leftBumper() {
        return xbox.leftBumper();
    }

    public boolean isConnected() {
        return DriverStation.isJoystickConnected(xbox.getHID().getPort())
                && DriverStation.getJoystickIsXbox(xbox.getHID().getPort());
    }

    /**
     * Constructs an event instance around the right bumper's digital signal.
     *
     * @return an event instance representing the right bumper's digital signal
     *         attached to the {@link
     *         CommandScheduler#getDefaultButtonLoop() default scheduler button
     *         loop}.
     * @see #rightBumper(EventLoop)
     */
    public Trigger rightBumper() {
        return xbox.rightBumper();
    }

    /**
     * Constructs an event instance around the left stick button's digital signal.
     *
     * @return an event instance representing the left stick button's digital signal
     *         attached to the
     *         {@link CommandScheduler#getDefaultButtonLoop() default scheduler
     *         button loop}.
     * @see #leftStick(EventLoop)
     */
    public Trigger leftStick() {
        return xbox.leftStick();
    }

    /**
     * Constructs an event instance around the right stick button's digital signal.
     *
     * @return an event instance representing the right stick button's digital
     *         signal attached to the
     *         {@link CommandScheduler#getDefaultButtonLoop() default scheduler
     *         button loop}.
     * @see #rightStick(EventLoop)
     */
    public Trigger rightStick() {
        return xbox.rightStick();
    }

    /**
     * Constructs an event instance around the A button's digital signal.
     *
     * @return an event instance representing the A button's digital signal attached
     *         to the {@link
     *         CommandScheduler#getDefaultButtonLoop() default scheduler button
     *         loop}.
     * @see #a(EventLoop)
     */
    public Trigger a() {
        return xbox.a();
    }

    /**
     * Constructs an event instance around the B button's digital signal.
     *
     * @return an event instance representing the B button's digital signal attached
     *         to the {@link
     *         CommandScheduler#getDefaultButtonLoop() default scheduler button
     *         loop}.
     * @see #b(EventLoop)
     */
    public Trigger b() {
        return xbox.b();
    }

    /**
     * Constructs an event instance around the X button's digital signal.
     *
     * @return an event instance representing the X button's digital signal attached
     *         to the {@link
     *         CommandScheduler#getDefaultButtonLoop() default scheduler button
     *         loop}.
     * @see #x(EventLoop)
     */
    public Trigger x() {
        return xbox.x();
    }

    /**
     * Constructs an event instance around the Y button's digital signal.
     *
     * @return an event instance representing the Y button's digital signal attached
     *         to the {@link
     *         CommandScheduler#getDefaultButtonLoop() default scheduler button
     *         loop}.
     * @see #y(EventLoop)
     */
    public Trigger y() {
        return xbox.y();
    }

    /**
     * Constructs an event instance around the start button's digital signal.
     *
     * @return an event instance representing the start button's digital signal
     *         attached to the {@link
     *         CommandScheduler#getDefaultButtonLoop() default scheduler button
     *         loop}.
     * @see #start(EventLoop)
     */
    public Trigger start() {
        return xbox.start();
    }

    /**
     * Constructs an event instance around the back button's digital signal.
     *
     * @return an event instance representing the back button's digital signal
     *         attached to the {@link
     *         CommandScheduler#getDefaultButtonLoop() default scheduler button
     *         loop}.
     * @see #back(EventLoop)
     */
    public Trigger back() {
        return xbox.back();
    }

    /**
     * Constructs a Trigger instance around the axis value of the left trigger. The
     * returned trigger
     * will be true when the axis value is greater than 0.5.
     *
     * @return a Trigger instance that is true when the left trigger's axis exceeds
     *         0.5, attached to
     *         the {@link CommandScheduler#getDefaultButtonLoop() default scheduler
     *         button loop}.
     */
    public Trigger leftTrigger() {
        return xbox.leftTrigger(0.25);
        // when shalowest trigger it is 0.35
    }

    /**
     * Constructs a Trigger instance around the axis value of the right trigger. The
     * returned trigger
     * will be true when the axis value is greater than 0.5.
     *
     * @return a Trigger instance that is true when the right trigger's axis exceeds
     *         0.5, attached to
     *         the {@link CommandScheduler#getDefaultButtonLoop() default scheduler
     *         button loop}.
     */
    public Trigger rightTrigger() {
        return xbox.rightTrigger(0.25);
        // when shalowest trigger it is 0.35
    }

    /**
     * Get the X axis value of left side of the controller.
     *
     * @return The axis value.
     */
    public double getLeftX() {
        return MathUtil.applyDeadband(xbox.getLeftX(), joystickDeadband);
    }

    /**
     * Get the X axis value of right side of the controller.
     *
     * @return The axis value.
     */
    public double getRightX() {
        return MathUtil.applyDeadband(xbox.getRightX(), joystickDeadband);
    }

    /**
     * Get the Y axis value of left side of the controller.
     *
     * @return The axis value.
     */
    public double getLeftY() {
        // flip y axis so that up is positive
        return MathUtil.applyDeadband(xbox.getLeftY(), joystickDeadband) * -1;
    }

    /**
     * Get the Y axis value of right side of the controller.
     *
     * @return The axis value.
     */
    public double getRightY() {
        // flip y axis so that up is positive
        return MathUtil.applyDeadband(xbox.getRightY(), joystickDeadband) * -1;
    }

    /**
     * Get the left trigger (LT) axis value of the controller. Note that this axis
     * is bound to the
     * range of [0, 1] as opposed to the usual [-1, 1].
     *
     * @return The axis value.
     */
    public double getLeftTriggerAxis() {
        return MathUtil.applyDeadband(xbox.getLeftTriggerAxis(), triggerDeadZone);
    }

    /**
     * Get the right trigger (RT) axis value of the controller. Note that this axis
     * is bound to the
     * range of [0, 1] as opposed to the usual [-1, 1].
     *
     * @return The axis value.
     */
    public double getRightTriggerAxis() {
        return MathUtil.applyDeadband(xbox.getRightTriggerAxis(), triggerDeadZone);
    }

    public Trigger joysticksTrigger() {
        return new Trigger(() -> {
            return Math.abs(getLeftX()) > 0
                    || Math.abs(getLeftY()) > 0
                    || Math.abs(getRightX()) > 0
                    || Math.abs(getRightY()) > 0;
        });
    }

    public Trigger leftYTrigger() {
        return new Trigger(() -> {
            return Math.abs(getLeftY()) > 0;
        });
    }

    public Trigger leftXTrigger() {
        return new Trigger(() -> {
            return Math.abs(getLeftX()) > 0;
        });
    }

    public Trigger rightYTrigger() {
        return new Trigger(() -> {
            return Math.abs(getRightY()) > 0;
        });
    }

    public Trigger rightXTrigger() {
        return new Trigger(() -> {
            return Math.abs(getRightX()) > 0;
        });
    }

    public Trigger joysticksTriggerLeft() {
        return new Trigger(() -> {
            return Math.abs(getLeftX()) > 0
                    || Math.abs(getLeftY()) > 0;
        });
    }

    public Trigger joysticksTriggerRight() {
        return new Trigger(() -> {
            return Math.abs(getRightX()) > 0
                    || Math.abs(getRightY()) > 0;
        });
    }

    /**
     * Constructs a Trigger instance based around this angle of the default (index
     * 0) POV on the HID,
     * attached to {@link CommandScheduler#getDefaultButtonLoop() the default
     * command scheduler button
     * loop}.
     *
     * <p>
     * The POV angles start at 0 in the up direction, and increase clockwise (e.g.
     * right is 90,
     * upper-left is 315).
     *
     * @param angle POV angle in degrees, or -1 for the center / not pressed.
     * @return a Trigger instance based around this angle of a POV on the HID.
     */
    public Trigger pov(int angle) {
        return pov(0, angle, CommandScheduler.getInstance().getDefaultButtonLoop());
    }

    /**
     * Constructs a Trigger instance based around this angle of a POV on the HID.
     *
     * <p>
     * The POV angles start at 0 in the up direction, and increase clockwise (e.g.
     * right is 90,
     * upper-left is 315).
     *
     * @param pov   index of the POV to read (starting at 0). Defaults to 0.
     * @param angle POV angle in degrees, or -1 for the center / not pressed.
     * @param loop  the event loop instance to attach the event to. Defaults to
     *              {@link
     *              CommandScheduler#getDefaultButtonLoop() the default command
     *              scheduler button loop}.
     * @return a Trigger instance based around this angle of a POV on the HID.
     */
    public Trigger pov(int pov, int angle, EventLoop loop) {
        return new Trigger(loop, () -> xbox.getHID().getPOV(pov) == angle);
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

    public void setRumble(double value) {
        xbox.getHID().setRumble(RumbleType.kBothRumble, value);
    }

    public Command rumbleCommand(double value) {
        return Commands.startEnd(
                () -> setRumble(value),
                () -> setRumble(0));
    }
}
