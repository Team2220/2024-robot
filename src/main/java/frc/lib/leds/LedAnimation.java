package frc.lib.leds;

import java.util.function.Consumer;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;

class LedAnimation {
    private Consumer<CANdle> run;

    public LedAnimation(Consumer<CANdle> run) {
        this.run = run;
    }

    public LedAnimation(Animation animation) {
        this((candle) -> { 
            candle.animate(animation);

        });
    }
    // this is the end of the class
}
