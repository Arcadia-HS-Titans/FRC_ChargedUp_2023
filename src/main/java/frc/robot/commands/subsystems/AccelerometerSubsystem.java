package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.utils.Vector3;

public class AccelerometerSubsystem extends SubsystemBase {

    private Accelerometer accelerometer;
    private final AnalogGyro gyroScope;
    private double angle = 0,
            dRate = 0, currentRate = 0, prevRate = 0;
    private Vector3 accelerometerVector = new Vector3(0,0,0);

    public AccelerometerSubsystem() {
        accelerometer = new BuiltInAccelerometer();
        gyroScope = new AnalogGyro(0);
    }

    public void update() {
        currentRate = gyroScope.getRate();
        angle = gyroScope.getAngle();
        dRate = currentRate - prevRate;
        prevRate = currentRate;
        accelerometerVector = new Vector3(accelerometer);
        SmartDashboard.putString("Angle", String.format("%.2f", angle));
        SmartDashboard.putNumber("Rate", currentRate);
        SmartDashboard.putNumber("dRate", dRate);
    }

    public double getAngle() {
        return angle;
    }

    public double getCurrentRate() {
        return currentRate;
    }

    public double getdRate() {
        return dRate;
    }

    public Vector3 getAcceleration() {
        return accelerometerVector;
    }

    public double getX() {
        return accelerometerVector.x;
    }

    public double getY() {
        return accelerometerVector.y;
    }

    public double getZ() {
        return accelerometerVector.z;
    }

    public void calibrate() {
        gyroScope.calibrate();
    }
}
