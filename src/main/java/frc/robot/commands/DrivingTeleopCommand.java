package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.AccelerometerSubsystem;

import java.sql.Driver;

/**
 * https://docs.wpilib.org/en/stable/docs/software/commandbased/commands.html#simple-command-example
 * https://github.com/wpilibsuite/allwpilib/blob/main/wpilibjExamples/src/main/java/edu/wpi/first/wpilibj/examples/hatchbottraditional/commands/DefaultDrive.java
 */
public class DrivingTeleopCommand extends CommandBase {

    private final Joystick joystick;
    public AccelerometerSubsystem accelerometerSubsystem;

    public DrivingTeleopCommand() {
        this.joystick = new Joystick(0);
        this.accelerometerSubsystem = new AccelerometerSubsystem();
    }

    @Override
    public void execute() {
        // Called every 20 ms
        DriverStation.reportWarning(String.valueOf(accelerometerSubsystem.accelerometer.getX()), false);
    }
}
