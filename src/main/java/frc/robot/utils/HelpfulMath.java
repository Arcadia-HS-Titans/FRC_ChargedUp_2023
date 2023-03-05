package frc.robot.utils;

public class HelpfulMath {
    public static boolean isInRange(double angle, double range) {
        return Math.abs(angle) < Math.abs(range);
    }
}
