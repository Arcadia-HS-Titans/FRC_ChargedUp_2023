package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class AccelerometerSubsystem extends SubsystemBase {
    /**
     *
     */
    public Accelerometer accelerometer;
    /**
     *
     */
    public final AnalogGyro gyroScope;
    public final AnalogGyro gyroScope2;

    public AccelerometerSubsystem() {
        accelerometer = new BuiltInAccelerometer();
        gyroScope = new AnalogGyro(0);
        gyroScope2 = new AnalogGyro(1);
    }

    /**
     * Get absoulte angle (we don't need to worry about the degrees for now
     * @return
     */
    public double getAbsAngle() {
        double angle = gyroScope.getAngle();
        return (angle < 0) ? -angle : angle;
    }

}
