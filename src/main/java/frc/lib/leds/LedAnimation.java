package frc.lib.leds;

import java.util.function.Consumer;

import com.ctre.phoenix.led.Animation;

import edu.wpi.first.wpilibj.Timer;

class LedAnimation {
    private Consumer<CANdleWrapper> consumer;

    public void run(CANdleWrapper candle) {
        consumer.accept(candle);
    };

    public LedAnimation(Consumer<CANdleWrapper> consumer) {
        this.consumer = consumer;
    }

    public LedAnimation(Animation animation) {
        this((candle) -> {
            candle.animate(animation);

        });
    }
    // this is the end of the class

    public static LedAnimation namehere() {
        return new LedAnimation((candle) -> {
            candle.setLEDs(1, 100, 50);
        });

    };

    public static LedAnimation off() {
        return new LedAnimation((candle) -> {
            candle.setLEDs(0, 0, 0);
            candle.animate(null);
        });

    };

    public static LedAnimation solid(int r, int b, int g) {
        return new LedAnimation((candle) -> {
            candle.setLEDs(r, g, b);
        });
    };

    // how long would the buttion have to be pressed for a timed anaimation (with
    // delays) to play in full? ASK TIM
    public static LedAnimation progressbar() {
        return new LedAnimation((candle) -> {

            if ((int)((Timer.getFPGATimestamp()*2) % 2) == 0) {
                candle.setLEDs(75, 0, 0);
            } else {
                candle.setLEDs(0, 0, 100);
            }
        });

    };

};