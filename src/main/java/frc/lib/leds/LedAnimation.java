package frc.lib.leds;

import java.util.function.Consumer;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;

class LedAnimation {
    private Consumer<CANdle> consumer;
    public void run(CANdle candle) {
        consumer.accept(candle);
    };

    public LedAnimation(Consumer<CANdle> consumer) {
        this.consumer = consumer;
    }

    public LedAnimation(Animation animation) {
        this((candle) -> { 
            candle.animate(animation);

        });
    }
    // this is the end of the class
}
