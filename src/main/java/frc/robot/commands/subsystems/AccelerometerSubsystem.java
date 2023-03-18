package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.utils.Vector3;

public class AccelerometerSubsystem extends SubsystemBase {

    private Accelerometer accelerometer;
    public final AnalogGyro gyroScope;
    private double angle = 0,
            dRate = 0, currentRate = 0, prevRate = 0;
    private Vector3 accelerometerVector = new Vector3(0,0,0);
    private double dampener = 0;
    private double totalDampener = 0;
    public boolean calibrating = false;

    public AccelerometerSubsystem() {
        accelerometer = new BuiltInAccelerometer();
        gyroScope = new AnalogGyro(1);
        dampener = 0;
        totalDampener = 0;
    }

    @Override
    public void periodic() {
        currentRate = gyroScope.getRate();
        angle = gyroScope.getAngle() + totalDampener;
        totalDampener += dampener;
        dRate = currentRate - prevRate;
        prevRate = currentRate;
        accelerometerVector = new Vector3(accelerometer);
        SmartDashboard.putString("Angle", String.format("%.2f", angle));
        SmartDashboard.putNumber("Rate", currentRate);
        SmartDashboard.putNumber("dRate", dRate);
        SmartDashboard.putNumber("Dampener", dampener);
        super.periodic();
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
        gyroScope.reset();
        dampener = 0;
        calibrating = true;
        totalDampener = 0;
    }

    private double pAng = 0;
    private double cAng = 0;
    private double iterations = 0;
    private double total = 0;
    private boolean tickedOnce = false;

    public void tickCalibration() {
        if(!tickedOnce) {
            tickedOnce = true;
            pAng = gyroScope.getAngle();
            return;
        }

        cAng = gyroScope.getAngle();
        total += cAng - pAng;
        pAng = cAng;
        iterations++;
    }

    public void assignConst() {
        dampener = -(Math.abs(total/iterations));
        totalDampener = dampener * iterations/2.;
        calibrating = false;
    }
}
