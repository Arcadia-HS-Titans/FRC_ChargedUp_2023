package frc.robot.subsystems;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class AccelerometerSubsystem extends SubsystemBase {
    public Accelerometer accelerometer;
    public final AnalogGyro gyroScope;

    public AccelerometerSubsystem() {
        accelerometer = new BuiltInAccelerometer();
        gyroScope = new AnalogGyro(I2C.Port.kMXP.value);
    }

}
