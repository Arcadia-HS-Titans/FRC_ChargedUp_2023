package frc.robot.utils;

import edu.wpi.first.wpilibj.interfaces.Accelerometer;

import java.util.Vector;

public class Vector3 {
    public double x;
    public double y;
    public double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Accelerometer accelerometer) {
        this.x = accelerometer.getX();
        this.y = accelerometer.getY();
        this.z = accelerometer.getZ();
    }
}
