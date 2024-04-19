package frc.lib.leds;

import java.sql.Blob;
import java.util.function.BooleanSupplier;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.FireAnimation;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.SingleFadeAnimation;
import com.ctre.phoenix.led.StrobeAnimation;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import frc.lib.faults.FaultRegistry;
import frc.lib.tunables.TunableDouble;

public class LedSignal {
    String name;
    BooleanSupplier isActive;
    LedAnimation animation;
    double debounce;
    Debouncer debouncer;
    private LedSegment[] segments;
    BooleanLogEntry logEntry;

    public LedSignal(String name, BooleanSupplier isActive, LedAnimation animation, double debounce,
            DebounceType debounceType, LedSegment[] segments) {
        this.name = name;
        this.isActive = isActive;
        this.animation = animation;
        this.debounce = debounce;
        this.debouncer = new Debouncer(debounce);
        this.segments = segments;
        this.logEntry = new BooleanLogEntry(DataLogManager.getLog(), "Led Signal " + name);
    }

    public LedSignal(String name, BooleanSupplier isActive, LedAnimation animation, double debounce) {
        this(name, isActive, animation, debounce, DebounceType.kFalling, new LedSegment[] {});
    }

    public LedSignal(String name, BooleanSupplier isActive, LedAnimation animation, double debounce,
            DebounceType debounceType) {
        this(name, isActive, animation, debounce, debounceType, new LedSegment[] {});
    }

    // $#####################//#endregion
    public LedSignal(String name, BooleanSupplier isActive, Animation animation, double debounce,
            DebounceType debounceType, LedSegment[] segments) {
        this(name, isActive, new LedAnimation(animation), debounce, debounceType, segments);
    }

    public LedSignal(String name, BooleanSupplier isActive, Animation animation, double debounce) {
        this(name, isActive, new LedAnimation(animation), debounce);
    }

    public LedSignal(String name, BooleanSupplier isActive, Animation animation, double debounce,
            DebounceType debounceType) {
        this(name, isActive, new LedAnimation(animation), debounce, debounceType);
    }

    public void update(LedSegment[] allSegments) {

        boolean asBoolean = isActive.getAsBoolean();
        logEntry.append(asBoolean);
        if (debouncer.calculate(asBoolean)) {
            if (segments.length == 0) {
                for (LedSegment segment : allSegments) {
                    segment.setAnimationIfAble(animation);
                }
            } else {
                for (LedSegment segment : segments) {
                    segment.setAnimationIfAble(animation);
                }
            }
        }
    }

    // static LedSignal[] leds = new LedSignal[]{isDSConnected(),
    // previouslyHadFault(), isBrownedOut(), shooterAtSetPoint(isActive),
    // hasActiveFault(), isEndGame(), getLowBatteryLedSignal(),
    // hasgamepiceBottomLedSignal(isActive), hasgamepiceTopLedSignal(isActive)};

    // starter signals
    public static LedSignal isDSConnected() {
        // fade blue
        SingleFadeAnimation singleFadeAnimation = new SingleFadeAnimation(0, 0, 100, 0, .5, 164);

        return new LedSignal("isDSConnected", () -> {
            return !DriverStation.isDSAttached();
        }, singleFadeAnimation, 0);
    }

    public static LedSignal previouslyHadFault() {
        // fade orange
        SingleFadeAnimation singleFadeAnimation = new SingleFadeAnimation(255, 165, 0, 0, .5, 164);
        return new LedSignal("previouslyHadFault", () -> {
            if (DriverStation.isDisabled()) {
                return FaultRegistry.hasAnyPreviouslyActive();
            } else {
                return false;
            }
        }, singleFadeAnimation, 0);
    }

    public static LedSignal isBrownedOut() {
        // blink red
        StrobeAnimation strobeAnimation = new StrobeAnimation(64, 0, 0, 0, 0.1, 164);
        return new LedSignal("isBrownedOut", RobotController::isBrownedOut, strobeAnimation, 3);
    }

    public static LedSignal shooterAtSetPoint(BooleanSupplier supplier) {
        // blink black-9

        StrobeAnimation strobeAnimation = new StrobeAnimation(0, 0, 20, 0, 0.1, 164);
        return new LedSignal("shooterAtSetPoint", supplier, strobeAnimation, 0);
    }

    public static LedSignal hasActiveFault() {
        // blink orange
        StrobeAnimation strobeAnimation = new StrobeAnimation(255, 165, 0, 0, 0.1,
                164);
        return new LedSignal("hasActiveFault", FaultRegistry::hasAnyActive,
                strobeAnimation, 0);
    }

    private static TunableDouble endgameTimeStart = new TunableDouble("EndgameTimeStart", 15, true, "LEDs");
    private static TunableDouble endgameTimeEnd = new TunableDouble("EndgameTimeEnd", 15, true, "LEDs");

    public static LedSignal isEndGame() {
        // blink yellow
        StrobeAnimation strobeAnimation = new StrobeAnimation(246, 247, 0, 0, 0.1, 164);
        return new LedSignal("isEndGame", () -> {
            // System.out.println(DriverStation.getMatchTime());
            if (DriverStation.isTeleop()) {
                if (DriverStation.getMatchTime() < 0) {
                    return false;
                } else {
                    return DriverStation.getMatchTime() <= endgameTimeStart.getValue()
                            && DriverStation.getMatchTime() >= endgameTimeEnd.getValue();
                }

            } else {
                return false;
            }
        }, strobeAnimation, 0);
    }

    public static LedSignal getLowBatteryLedSignal() {
        // blink red
        SingleFadeAnimation singleFadeAnimation = new SingleFadeAnimation(100, 0, 0, 0, .5, 164);
        return new LedSignal("lowBattery", () -> {
            if (RobotController.getBatteryVoltage() < 12.3) {
                return DriverStation.isDisabled();
            } else {
                return false;
            }
        }, singleFadeAnimation, 0);
    }

    public static LedSignal hasgamepiceBottomLedSignal(BooleanSupplier supplier) {
        StrobeAnimation singleFadeAnimation = new StrobeAnimation(0, 255, 0, 0, 0.5, 164, 0);
        return new LedSignal("HasGamepiceBottom", supplier, singleFadeAnimation, 0);
    }

    public static LedSignal hasgamepiceTopLedSignal(BooleanSupplier supplier) {
        RainbowAnimation rainbowAnimation = new RainbowAnimation();
        return new LedSignal("seanscolors", supplier, rainbowAnimation, 0);
    }

    public static LedSignal erolsPurpleLight(BooleanSupplier supplier) {
        StrobeAnimation strobeAnimation = new StrobeAnimation(70, 0, 165, 0, 2, 164);
        return new LedSignal("erolsPurpleLight", supplier, strobeAnimation, 0);
    }

    public static LedSignal intakeStalled(BooleanSupplier supplier) {
        FireAnimation FireAnimation = new FireAnimation();
        return new LedSignal("intakeStalled", supplier, FireAnimation, 0);
    }

    public static LedSignal shooterspin(BooleanSupplier supplier) {
        SingleFadeAnimation singleFadeAnimation = new SingleFadeAnimation(0, 0, 0, 255, 0.113, 164, 0);
        return new LedSignal("shooterspin", supplier, singleFadeAnimation, 0);
    }

    public static LedSignal limeLight(BooleanSupplier supplier) {
        SingleFadeAnimation singleFadeAnimation = new SingleFadeAnimation(10, 200, 0, 10, 1, 164, 0);
        return new LedSignal("limeLight", supplier, singleFadeAnimation, 0);
    }

    public static LedSignal coastButton(BooleanSupplier supplier) {
        SingleFadeAnimation singleFadeAnimation = new SingleFadeAnimation(10, 100, 40, 10, 1, 164, 0);
        return new LedSignal("coastButton", supplier, singleFadeAnimation, 0);
    }

    public static LedSignal Lime(BooleanSupplier supplier) {
        LedAnimation animation = LedAnimation.solid(Color.GREEN);
        return new LedSignal("Lime", supplier, animation, 0);
    }

    public static LedSignal customrainbow(BooleanSupplier supplier) {
        LedAnimation animation = LedAnimation.rainbow();
        return new LedSignal("Testwrapper", supplier, animation, 0);
    }

    public static LedSignal probalynotgoingtowork(BooleanSupplier supplier) {
        LedAnimation animation = LedAnimation.rainbowOffset();
        return new LedSignal("notgoingtowork", supplier, animation, 0);
    }

}