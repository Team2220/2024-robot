package frc.lib.leds;

import java.util.function.Consumer;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;

public class LedSegment {
    Consumer<Animation> setAnimation;
    boolean hasBeenSet = false;

    public LedSegment(Consumer<Animation> setAnimation) {
        this.setAnimation = setAnimation;
    }

    public LedSegment(CANdle candle) {
        this(candle::animate);
    }

    public boolean reset() {
        return hasBeenSet = false;
    }

    public void setAnimationIfAble(Animation animation) {
        if (!hasBeenSet) {
            setAnimation.accept(animation);
            hasBeenSet = true;
        }
    }
}
