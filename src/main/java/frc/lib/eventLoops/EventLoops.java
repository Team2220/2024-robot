package frc.lib.eventloops;

public class EventLoops {
    public static final IsolatedEventLoop oncePerSec = new IsolatedEventLoop();
    public static final IsolatedEventLoop oncePerMin = new IsolatedEventLoop();
}
