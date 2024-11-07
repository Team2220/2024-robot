#include <FastLED.h>

#define LED_PIN 7
#define NUM_LEDS 100

CRGB leds[NUM_LEDS];
byte value = -1;
const unsigned long interval = 150;  // 1 second interval in milliseconds
unsigned long previousMillis = 0;
bool toggleState = false;  // Track which command is active



void setup() {
  Serial.begin(9600);
  FastLED.addLeds<WS2812, LED_PIN, GRB>(leds, NUM_LEDS);
}

void command1() {
  fill_solid(leds, NUM_LEDS, CRGB(255, 0, 0));
  FastLED.show();
}

void command2() {
  fill_solid(leds, NUM_LEDS, CRGB(0, 0, 255));
  FastLED.show();
}


void loop() {
 //If the byte is 0x12 (i.e. 18 in decimal)
  unsigned long currentMillis = millis();

  if (Serial.available()) {
    //Read a byte from the input buffer
    value = Serial.read();
  }



  switch (value)
  {
    case 0x12:
    {
      fill_solid(leds, NUM_LEDS, CRGB(255, 0, 0));
    }break;
    case 0x13:
    {
      fill_solid(leds, NUM_LEDS, CRGB(255, 0, 0));
    }break;
    case 0x14:
    { 
      fill_solid(leds, NUM_LEDS, CRGB(255, 0, 0));
    }break;
    case 0x15:
    {
      fill_solid(leds, NUM_LEDS, CRGB(0, 0, 0));
    }break;
    case 0x16:
    {
      fill_solid(leds, NUM_LEDS, CRGB(0, 0, 0));
    }break;
    case 0x17:
    {
      fill_solid(leds, NUM_LEDS, CRGB(0, 0, 0));
    }break;
    case 0x18:
    {
      fill_solid(leds, NUM_LEDS, CRGB(0, 0, 0));
    }break;
    case 0x19:
    {
      fill_solid(leds, NUM_LEDS, CRGB(0, 0, 0));
    }break;
    case 0x20:
    {
          if (currentMillis - previousMillis >= interval) {
      previousMillis = currentMillis;
      toggleState = !toggleState;

      if (toggleState) {
        command1();
      } else {
        command2();
      }
    }
  }
  FastLED.show();
}
}