package frc.lib.eventLoops;

public class EventLoops {
    public static final IsolatedEventLoop oncePerSec = new IsolatedEventLoop();
    public static final IsolatedEventLoop oncePerMin = new IsolatedEventLoop();
    public static final IsolatedEventLoop everyLoop = new IsolatedEventLoop();
}
