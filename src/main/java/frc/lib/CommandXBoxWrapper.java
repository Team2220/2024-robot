package frc.lib;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.lib.faults.Fault;

public class CommandXBoxWrapper {

    CommandXboxController xbox;
    double joystickDeadband;
    double triggerDeadZone;

    public CommandXBoxWrapper(int inPort, double joystickDeadband, double triggerDeadZone) {
        xbox = new CommandXboxController(inPort);
        this.joystickDeadband = joystickDeadband;
        this.triggerDeadZone = triggerDeadZone;

        Fault.autoUpdating("Controller " + inPort + " is disconnected.", this::isConnected);
    }

    public CommandXBoxWrapper(int inPort) {
        this(inPort, .15, .1);
    }

    public boolean getrawLeftBumper() {

        return xbox.getHID().getLeftBumperPressed();

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
        return !DriverStation.isJoystickConnected(xbox.getHID().getPort())
                || !DriverStation.getJoystickIsXbox(xbox.getHID().getPort());
    }

    /**
     * Constructs an event instance around the left bumper's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the right bumper's digital signal
     *         attached to the given
     *         loop.
     */
    public Trigger leftBumper(EventLoop loop) {
        return xbox.leftBumper(loop);
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
     * Constructs an event instance around the right bumper's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the left bumper's digital signal
     *         attached to the given
     *         loop.
     */
    public Trigger rightBumper(EventLoop loop) {
        return xbox.rightBumper(loop);
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
     * Constructs an event instance around the left stick button's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the left stick button's digital signal
     *         attached to the
     *         given loop.
     */
    public Trigger leftStick(EventLoop loop) {
        return xbox.leftStick(loop);
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
     * Constructs an event instance around the right stick button's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the right stick button's digital
     *         signal attached to the
     *         given loop.
     */
    public Trigger rightStick(EventLoop loop) {
        return xbox.rightStick(loop);
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
     * Constructs an event instance around the A button's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the A button's digital signal attached
     *         to the given
     *         loop.
     */
    public Trigger a(EventLoop loop) {
        return xbox.a(loop);
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
     * Constructs an event instance around the B button's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the B button's digital signal attached
     *         to the given
     *         loop.
     */
    public Trigger b(EventLoop loop) {
        return xbox.b(loop);
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
     * Constructs an event instance around the X button's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the X button's digital signal attached
     *         to the given
     *         loop.
     */
    public Trigger x(EventLoop loop) {
        return xbox.x(loop);
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
     * Constructs an event instance around the Y button's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the Y button's digital signal attached
     *         to the given
     *         loop.
     */
    public Trigger y(EventLoop loop) {
        return xbox.y(loop);
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
     * Constructs an event instance around the start button's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the start button's digital signal
     *         attached to the given
     *         loop.
     */
    public Trigger start(EventLoop loop) {
        return xbox.start(loop);
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
     * Constructs an event instance around the back button's digital signal.
     *
     * @param loop the event loop instance to attach the event to.
     * @return an event instance representing the back button's digital signal
     *         attached to the given
     *         loop.
     */
    public Trigger back(EventLoop loop) {
        return xbox.back(loop);
    }

    /**
     * Constructs a Trigger instance around the axis value of the left trigger. The
     * returned trigger
     * will be true when the axis value is greater than {@code threshold}.
     *
     * @param threshold the minimum axis value for the returned {@link Trigger} to
     *                  be true. This value
     *                  should be in the range [0, 1] where 0 is the unpressed state
     *                  of the axis.
     * @param loop      the event loop instance to attach the Trigger to.
     * @return a Trigger instance that is true when the left trigger's axis exceeds
     *         the provided
     *         threshold, attached to the given event loop
     */
    public Trigger leftTrigger(double threshold, EventLoop loop) {
        return xbox.leftTrigger(threshold, loop);
    }

    /**
     * Constructs a Trigger instance around the axis value of the left trigger. The
     * returned trigger
     * will be true when the axis value is greater than {@code threshold}.
     *
     * @param threshold the minimum axis value for the returned {@link Trigger} to
     *                  be true. This value
     *                  should be in the range [0, 1] where 0 is the unpressed state
     *                  of the axis.
     * @return a Trigger instance that is true when the left trigger's axis exceeds
     *         the provided
     *         threshold, attached to the
     *         {@link CommandScheduler#getDefaultButtonLoop() default scheduler
     *         button loop}.
     */
    public Trigger leftTrigger(double threshold) {
        return xbox.leftTrigger(threshold);
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
        return xbox.leftTrigger();
    }

    /**
     * Constructs a Trigger instance around the axis value of the right trigger. The
     * returned trigger
     * will be true when the axis value is greater than {@code threshold}.
     *
     * @param threshold the minimum axis value for the returned {@link Trigger} to
     *                  be true. This value
     *                  should be in the range [0, 1] where 0 is the unpressed state
     *                  of the axis.
     * @param loop      the event loop instance to attach the Trigger to.
     * @return a Trigger instance that is true when the right trigger's axis exceeds
     *         the provided
     *         threshold, attached to the given event loop
     */
    public Trigger rightTrigger(double threshold, EventLoop loop) {
        return xbox.rightTrigger(threshold, loop);
    }

    /**
     * Constructs a Trigger instance around the axis value of the right trigger. The
     * returned trigger
     * will be true when the axis value is greater than {@code threshold}.
     *
     * @param threshold the minimum axis value for the returned {@link Trigger} to
     *                  be true. This value
     *                  should be in the range [0, 1] where 0 is the unpressed state
     *                  of the axis.
     * @return a Trigger instance that is true when the right trigger's axis exceeds
     *         the provided
     *         threshold, attached to the
     *         {@link CommandScheduler#getDefaultButtonLoop() default scheduler
     *         button loop}.
     */
    public Trigger rightTrigger(double threshold) {
        return xbox.rightTrigger(threshold);
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
        return xbox.rightTrigger();
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
            return getLeftX() > 0
                    || getLeftY() > 0
                    || getRightX() > 0
                    || getRightY() > 0;
        });
    }

    public Trigger leftYTrigger() {
        return new Trigger(() -> {
            return getLeftY() > 0;
        });
    }
}
