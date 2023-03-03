package frc.robot.utils;

public class HelpfulMath {
    public static boolean isInRange(double angle, double range) {
        return Math.abs(angle) < Math.abs(range);
    }
    public static boolean isOutOfRage(double angle, double range) {
        return Math.abs(angle) > Math.abs(range);
    }

    public static boolean isInRange(double lower, double angle, double higher) {
        higher = Math.abs(higher);
        lower = Math.abs(lower);
        angle = Math.abs(angle);
        return (angle > lower && angle < higher);
    }
    public static boolean isOutRange(double lower, double angle, double higher) {
        higher = Math.abs(higher);
        lower = Math.abs(lower);
        angle = Math.abs(angle);
        return (angle < lower || angle > higher);
    }
}
