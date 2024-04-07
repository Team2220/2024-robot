package frc.lib.leds;

import java.util.function.Consumer;

import com.ctre.phoenix.led.Animation;

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

    public static LedAnimation solid(int r, int b, int g) {
        return new LedAnimation((candle) -> {
            candle.setLEDs(r, g, b);
        });
    };

    public static LedAnimation progressbar() {
        return new LedAnimation((candle) -> {
            candle.setLEDs(1, 100, 50, 15, 7);
        });

    };

};