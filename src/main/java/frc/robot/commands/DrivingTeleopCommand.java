package frc.robot.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.AccelerometerSubsystem;
import frc.robot.subsystems.DoubleSolonoidSubsystem;

import java.sql.Driver;

/**
 * https://docs.wpilib.org/en/stable/docs/software/commandbased/commands.html#simple-command-example
 * https://github.com/wpilibsuite/allwpilib/blob/main/wpilibjExamples/src/main/java/edu/wpi/first/wpilibj/examples/hatchbottraditional/commands/DefaultDrive.java
 */
public class DrivingTeleopCommand extends CommandBase {

    private final Joystick joystick;
    public AccelerometerSubsystem accelerometerSubsystem;
    public DoubleSolonoidSubsystem doubleSolonoidSubsystem;

    public DrivingTeleopCommand() {
        this.joystick = new Joystick(0);
        this.accelerometerSubsystem = new AccelerometerSubsystem();
        doubleSolonoidSubsystem = new DoubleSolonoidSubsystem();
    }

    @Override
    public void execute() {
        // Called every 20 ms
/*        DriverStation.reportWarning(String.valueOf(
                //accelerometerSubsystem.accelerometer.getX()) + " " +
                //accelerometerSubsystem.accelerometer.getY() + " " +
                accelerometerSubsystem.accelerometer.getZ()), false);*/
        DriverStation.reportWarning(String.valueOf(accelerometerSubsystem.gyroScope.getRate()), false);
        if(joystick.getRawButton(1)) {
            doubleSolonoidSubsystem.solenoid1.set(DoubleSolenoid.Value.kForward);
            doubleSolonoidSubsystem.solenoid2.set(DoubleSolenoid.Value.kForward);
        }
        if(joystick.getRawButton(2)) {
            doubleSolonoidSubsystem.solenoid1.set(DoubleSolenoid.Value.kReverse);
            doubleSolonoidSubsystem.solenoid2.set(DoubleSolenoid.Value.kReverse);
        }
        if(joystick.getRawButton(3)) {
            doubleSolonoidSubsystem.solenoid1.set(DoubleSolenoid.Value.kOff);
            doubleSolonoidSubsystem.solenoid2.set(DoubleSolenoid.Value.kOff);
        }
        if(joystick.getRawButton(4)) {
            //doubleSolonoidSubsystem.compressor.enable
        }
    }
}
// https://docs.wpilib.org/en/stable/docs/software/hardware-apis/pneumatics/pneumatics.html