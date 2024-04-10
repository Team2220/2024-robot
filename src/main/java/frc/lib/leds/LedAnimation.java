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

    // timed example
    // change 3 for number of varibles
    // change 2 for speed
    public static LedAnimation e() {
        return new LedAnimation((candle) -> {

            int i = (int) ((Timer.getFPGATimestamp() * 2) % 3);
            if (i == 0) {
                candle.setLEDs(75, 0, 0);
            }
            if (i == 1) {
                candle.setLEDs(0, 75, 0);
            }
            if (i == 2) {
                candle.setLEDs(0, 0, 75);
            }
        });

    };

    public static LedAnimation progressbar() {
        return new LedAnimation((candle) -> {

            for (int i = 0; i < candle.getNumLed(); i++) {
                if ((int) (i % 2) == 0) {
                    candle.setLEDs(20, 0, 0, i, 1);

                } else {
                    candle.setLEDs(0, 20, 0, i, 1);
                }

            }
        });
    };

};