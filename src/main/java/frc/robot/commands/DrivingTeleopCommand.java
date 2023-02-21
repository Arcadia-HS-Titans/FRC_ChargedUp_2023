package frc.robot.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.subsystems.AccelerometerSubsystem;
import frc.robot.commands.subsystems.DoubleSolonoidSubsystem;
import frc.robot.commands.subsystems.DrivingSubsystem;

/**
 * https://docs.wpilib.org/en/stable/docs/software/commandbased/commands.html#simple-command-example
 * https://github.com/wpilibsuite/allwpilib/blob/main/wpilibjExamples/src/main/java/edu/wpi/first/wpilibj/examples/hatchbottraditional/commands/DefaultDrive.java
 */
public class DrivingTeleopCommand extends CommandBase {

    public enum RampBalancingDir {
        NONE,
        FORWARDS,
        BACKWARDS
    }
    private RampBalancingDir currentMode;
    private double startingPower = .8;

    public void balanceOnRamp() {
        // TODO: Have the accellerometer autoadjust the bot to face ramp, maybe error correcting later
        double angle = accelerometerSubsystem.gyroScope.getAngle();
        double rate = accelerometerSubsystem.gyroScope.getRate();

        // TODO: Check absolute part
        if(angle > 10 && currentMode == RampBalancingDir.NONE) {
            currentMode = RampBalancingDir.FORWARDS;
            startingPower = .7;
        }
        if(angle < 10 && currentMode == RampBalancingDir.NONE) {
            currentMode = RampBalancingDir.BACKWARDS;
            startingPower = .7;
        }

        if(currentMode == RampBalancingDir.FORWARDS) {
            // Do this...
            if (angle > 10 && angle < 14.5) {
                // We're climbing up the first ramp
                drivingSubsystem.drive(0, startingPower);
            } else if(angle <= 10 && rate < 15 ) {
                startingPower = 0; // Can we turn to lock ourselves in place?
            } else if(angle >= 14.5) {
                // We're on the main ramp, we can go slower now
                drivingSubsystem.drive(0,startingPower);
            }
            // At the end of the phase, if we're falling, change mode to backwards
            currentMode = RampBalancingDir.BACKWARDS;
        } else if(currentMode == RampBalancingDir.BACKWARDS) {
            // Same as above
            drivingSubsystem.drive(0, -1);
            currentMode = RampBalancingDir.FORWARDS;
        }
        startingPower -= 0.001;
    }

    private final Joystick joystick;
    public AccelerometerSubsystem accelerometerSubsystem;
    public DoubleSolonoidSubsystem doubleSolonoidSubsystem;
    public DrivingSubsystem drivingSubsystem;
    private boolean enabled = false;

    public DrivingTeleopCommand() {
        this.joystick = new Joystick(0);
        this.accelerometerSubsystem = new AccelerometerSubsystem();
        doubleSolonoidSubsystem = new DoubleSolonoidSubsystem();
        drivingSubsystem = new DrivingSubsystem();
        this.addRequirements(accelerometerSubsystem);
        this.addRequirements(doubleSolonoidSubsystem);
        this.addRequirements(drivingSubsystem);
    }

    @Override
    public void execute() {
        // Called every 20 ms
/*        DriverStation.reportWarning(String.valueOf(
                //accelerometerSubsystem.accelerometer.getX()) + " " +
                //accelerometerSubsystem.accelerometer.getY() + " " +
                accelerometerSubsystem.accelerometer.getZ()), false);*/
        if(!enabled) {
            enabled = true;
            accelerometerSubsystem.gyroScope.calibrate();
            accelerometerSubsystem.gyroScope2.calibrate();
        }

        DriverStation.reportWarning("Gyro1: " + accelerometerSubsystem.gyroScope.getAngle(), false);
        DriverStation.reportWarning("Gyro2: " + accelerometerSubsystem.gyroScope2.getAngle(), false);
        //DriverStation.reportWarning(String.valueOf(accelerometerSubsystem.accelerometer.getZ()), false);
        boolean solenoidControl = false; // Just for easily testing multiple stuff and things
        if(solenoidControl) {
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
        }
    }
}
// https://docs.wpilib.org/en/stable/docs/software/hardware-apis/pneumatics/pneumatics.html