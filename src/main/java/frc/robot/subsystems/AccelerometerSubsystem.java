package frc.robot.subsystems;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class AccelerometerSubsystem extends SubsystemBase {
    public Accelerometer accelerometer;

    public AccelerometerSubsystem() {
        accelerometer = new BuiltInAccelerometer();
    }

}
