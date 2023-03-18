package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class USSubsystem extends SubsystemBase {
    private Ultrasonic ultrasonic;
    private double reading;

    public USSubsystem() {
        this.ultrasonic = new Ultrasonic(new DigitalOutput(1), new DigitalInput(0));
        Ultrasonic.setAutomaticMode(true);
    }

    public double getReading() {
        return reading;
    }

    @Override
    public void periodic() {
        reading = ultrasonic.getRangeInches();
        SmartDashboard.putNumber("US Inches", reading);
        super.periodic();
    }
}
