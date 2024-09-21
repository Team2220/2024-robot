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
            candle.setLEDs(Color.BLUE);
        });

    };

    public static LedAnimation off() {
        return new LedAnimation((candle) -> {
            candle.setLEDs(Color.BLACK);
            candle.animate(null);
        });

    };

    public static LedAnimation solid(Color color) {
        return new LedAnimation((candle) -> {
            candle.setLEDs(color);
        });
    };

    // timed example
    // change 3 for number of varibles
    // change 2 for speed
    public static LedAnimation timedexample() {
        return new LedAnimation((candle) -> {

            int time = (int) ((Timer.getFPGATimestamp() * 2) % 3);
            if (time == 0) {
                candle.setLEDs(Color.RED);
            }
            if (time == 1) {
                candle.setLEDs(Color.GREEN);
            }
            if (time == 2) {
                candle.setLEDs(Color.BLUE);
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

                candle.setLEDs(new Color(red, green, blue), led, 1); // Set brightness with the last argument
            }
        });
    }

    // ###############################

    public static LedAnimation stagger() {
        return new LedAnimation((candle) -> {

            for (int stagger = 0; stagger < candle.getNumLed(); stagger++) {
                if ((int) (stagger % 3) == 0) {
                    candle.setLEDs(Color.RED, stagger, 1);
                }
                if ((int) (stagger % 3) == 0) {
                    candle.setLEDs(Color.GREEN, stagger, 1);
                } else {
                    candle.setLEDs(Color.BLUE, stagger, 1);
                }

            }
        });
    };

    // ###############################

    public static LedAnimation staggerexample() {
        return new LedAnimation((candle) -> {

            for (int stager = 0; stager < candle.getNumLed(); stager++) {
                if ((int) (stager % 2) == 0) {
                    candle.setLEDs(Color.RED, stager, 1);

                } else {
                    candle.setLEDs(Color.GREEN, stager, 1);
                }

            }
        });
    };

    public static LedAnimation rainbow() {
        return new LedAnimation((candle) -> {

            int time = (int) ((Timer.getFPGATimestamp() * 15) % 6);
            if (time == 0) {
                candle.setLEDs(Color.RED);
            }
            // red
            if (time == 1) {
                candle.setLEDs(Color.RED);
            }
            // orange
            if (time == 2) {
                candle.setLEDs(Color.RED);
            }
            // yellow
            if (time == 3) {
                candle.setLEDs(Color.RED);
            }
            // green
            if (time == 4) {
                candle.setLEDs(Color.RED);
            }
            // blue
            if (time == 5) {
                candle.setLEDs(Color.RED);
            }
            // pink

        });

    };

};
