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
    public static LedAnimation timedexample() {
        return new LedAnimation((candle) -> {

            int time = (int) ((Timer.getFPGATimestamp() * 2) % 3);
            if (time == 0) {
                candle.setLEDs(75, 0, 0);
            }
            if (time == 1) {
                candle.setLEDs(0, 75, 0);
            }
            if (time == 2) {
                candle.setLEDs(0, 0, 75);
            }
        });

    };

    public static LedAnimation rainbowOffset() {
        return new LedAnimation((candle) -> {
            int numLeds = candle.getNumLed();
            float hueOffset = (float) Timer.getFPGATimestamp() / 1000.0f; // Adjust speed as needed

            for (int led = 0; led < numLeds; led++) {
                float hue = (led + hueOffset) % 1.0f;
                int red = (int) (Math.sin(hue * Math.PI * 2.0f) * 255);
                int green = (int) (Math.cos(hue * Math.PI) * 255);
                int blue = (int) (-Math.sin(hue * Math.PI * 2.0f + 2.0f * Math.PI / 3.0f) * 255);

                candle.setLEDs(red, green, blue, led, 1); // Set brightness with the last argument
            }
        });
    }

    // ###############################

    public static LedAnimation stagger() {
        return new LedAnimation((candle) -> {

            for (int stagger = 0; stagger < candle.getNumLed(); stagger++) {
                if ((int) (stagger % 3) == 0) {
                    candle.setLEDs(20, 0, 0, stagger, 1);
                }
                if ((int) (stagger % 3) == 0) {
                    candle.setLEDs(20, 20, 0, stagger, 1);
                } else {
                    candle.setLEDs(0, 0, 20, stagger, 1);
                }

            }
        });
    };

    // ###############################

    public static LedAnimation staggerexample() {
        return new LedAnimation((candle) -> {

            for (int stager = 0; stager < candle.getNumLed(); stager++) {
                if ((int) (stager % 2) == 0) {
                    candle.setLEDs(20, 0, 0, stager, 1);

                } else {
                    candle.setLEDs(0, 20, 0, stager, 1);
                }

            }
        });
    };

    public static LedAnimation rainbow() {
        return new LedAnimation((candle) -> {

            int time = (int) ((Timer.getFPGATimestamp() * 15) % 6);
            if (time == 0) {
                candle.setLEDs(255, 0, 0);
            }
            // red
            if (time == 1) {
                candle.setLEDs(255, 128, 0);
            }
            // orange
            if (time == 2) {
                candle.setLEDs(255, 255, 0);
            }
            // yellow
            if (time == 3) {
                candle.setLEDs(0, 255, 75);
            }
            // green
            if (time == 4) {
                candle.setLEDs(0, 0, 255);
            }
            // blue
            if (time == 5) {
                candle.setLEDs(255, 0, 255);
            }
            // pink

        });

    };

};
// dont name all the varibles "i" it breaks it
// *cough* tim *cough*