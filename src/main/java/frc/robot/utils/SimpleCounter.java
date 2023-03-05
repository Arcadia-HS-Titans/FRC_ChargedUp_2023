package frc.robot.utils;

public class SimpleCounter {

    public enum Behavior {
        ONCE,
        INFINITE
    }

    public int maxTime;
    public int time;
    private final Behavior behavior;

    public SimpleCounter(int maxTime, Behavior behavior) {
        this(maxTime, maxTime, behavior);
    }

    public SimpleCounter(int maxTime, int time, Behavior behavior) {
        this.maxTime = maxTime;
        this.time = time;
        this.behavior = behavior;
    }

    public boolean tick() {
        time--;
        if(time <= 0) {
            if(behavior == Behavior.INFINITE)
                time = maxTime;
            return true;
        }
        return false;
    }
}
