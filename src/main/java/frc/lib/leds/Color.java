package frc.lib.leds;

public class Color {
    private int r;
    private int g;
    private int b;

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color BLACK = new Color(0,0,0);

    public int getR() {
        return r;
    }
    public int getG() {
        return g;
    }
    public int getB() {
        return b;
    }

    public Color brightness(double brightness) {
        return new Color((int)(r*brightness), (int)(g*brightness), (int)(b*brightness));
    }
}
