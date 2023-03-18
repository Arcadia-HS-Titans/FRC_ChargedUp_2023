package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WinchSubsystem extends SubsystemBase {
    public PWMSparkMax motor;

    public WinchSubsystem() {
        this.motor = new PWMSparkMax(4);
    }

    public void setMotor(double power) {
        motor.set(power);
    }
}
