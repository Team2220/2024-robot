package frc.lib.leds;

import java.util.function.Consumer;

import com.ctre.phoenix.led.Animation;

public class LedSegment {
    Consumer<LedAnimation> setAnimation;
    boolean hasBeenSet = false;

    public LedSegment(Consumer<LedAnimation> setAnimation) {
        this.setAnimation = setAnimation;
    }

    public LedSegment(CANdleWrapper candle) {
        this((animation) -> {
            animation.run(candle);

        });
    }

    public boolean reset() {
        return hasBeenSet = false;
    }

    public void setAnimationIfAble(LedAnimation animation) {
        if (!hasBeenSet) {
            setAnimation.accept(animation);
            hasBeenSet = true;
        }
    }
}
