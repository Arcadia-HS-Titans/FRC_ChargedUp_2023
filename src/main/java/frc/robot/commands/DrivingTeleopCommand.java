package frc.robot.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.FileManager;
import frc.robot.commands.subsystems.AccelerometerSubsystem;
import frc.robot.commands.subsystems.DoubleSolonoidSubsystem;
import frc.robot.commands.subsystems.DrivingSubsystem;

import java.io.File;
import java.util.Random;

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

    public boolean isInRange(double angle, double range) {
        return Math.abs(angle) < Math.abs(range);
    }
    public boolean isOutOfRage(double angle, double range) {
        return Math.abs(angle) > Math.abs(range);
    }

    public boolean isInRange(double lower, double angle, double higher) {
        higher = Math.abs(higher);
        lower = Math.abs(lower);
        angle = Math.abs(angle);
        return (angle > lower && angle < higher);
    }
    public boolean isOutRange(double lower, double angle, double higher) {
        higher = Math.abs(higher);
        lower = Math.abs(lower);
        angle = Math.abs(angle);
        return (angle < lower || angle > higher);
    }
    private RampBalancingDir currentMode = RampBalancingDir.NONE;
    private double startingPower = .8;
    private boolean pressedRamps = false;
    private boolean toggleRamps = false;

    public void balanceOnRamp() {
        // TODO: Have the accelerometer auto-adjust the bot to face ramp, maybe error correcting later
        double angle = accelerometerSubsystem.gyroScope.getAngle();
        double rate = accelerometerSubsystem.gyroScope.getRate();

        // TODO: Check absolute part
/*        if((angle > 10 || angle < 10) && currentMode == RampBalancingDir.NONE) {
            currentMode = RampBalancingDir.FORWARDS;
            startingPower = .6;
        }*/
        if(currentMode == RampBalancingDir.NONE) {
            startingPower = .91;
            currentMode = RampBalancingDir.FORWARDS;
        }

        if(currentMode == RampBalancingDir.FORWARDS) {
            // Do this...
            DriverStation.reportWarning("AAA", false);
            if (isInRange(10., angle, 16.)) {
                // We're climbing up the first ramp
                drivingSubsystem.arcadeDrive(0, -startingPower);
            } else if(angle <= 5 && rate < 15 ) {
                drivingSubsystem.arcadeDrive(0, -startingPower/1.5); // Can we turn to lock ourselves in place?
            } else if(isOutOfRage(angle, 15)) {
                // We're on the main ramp, we can go slower now
                drivingSubsystem.arcadeDrive(0, -startingPower);
            } else {
                // We're level
                if(startingPower > .5) startingPower -= 0.005;
                drivingSubsystem.arcadeDrive(0, -startingPower/1.5); // Can we turn to lock ourselves in place?
            }
            // At the end of the phase, if we're falling, change mode to backwards
/*            if(rate >= 50 || rate <= -50) {
                startingPower = -.7;
            };*/
        } else if(currentMode == RampBalancingDir.BACKWARDS) {
            // Same as above
            startingPower *= -1;
            currentMode = RampBalancingDir.FORWARDS;
        }
    }

    private final Joystick joystick;
    public AccelerometerSubsystem accelerometerSubsystem;
    public DoubleSolonoidSubsystem doubleSolonoidSubsystem;
    public DrivingSubsystem drivingSubsystem;
    private boolean loggingData = false;
    boolean pressed = false;
    private int timer = 0;
    private int maxTimer = 5;
    private long startTime = System.currentTimeMillis();
    int iteration = 0;


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
        recordGyros();
        if(joystick.getRawButton(2)) {
            if(!pressedRamps) {
                pressedRamps = true;
                toggleRamps = !toggleRamps;
            }
        } else pressedRamps = false;
        if(toggleRamps) {
            balanceOnRamp();
        } else {
            drivingSubsystem.arcadeDrive(joystick.getZ(), joystick.getY());
        }
    }

    public void recordGyros() {
        File file = null;
        if(joystick.getRawButton(1)) {
            if(!pressed) {
                loggingData = !loggingData;
                if(loggingData) {
                    DriverStation.reportWarning("Started recording!", false);
                    startTime = System.currentTimeMillis();
                    iteration++;
                    file = new File("/home/lvuser/GyroData" + iteration + ".csv");
                    FileManager.writeFile(file, "Time,G1Angle,G1Rate,G2Angle,G2Rate\n");
                }
                pressed = true;
            }
        } else pressed = false;
        file = new File("/home/lvuser/GyroData" + iteration + ".csv");

        if(loggingData) {
            timer++;
            if(timer >= maxTimer) {
                StringBuilder toWrite = new StringBuilder("").append(System.currentTimeMillis() - startTime).append(","); // In Java
                toWrite.append(accelerometerSubsystem.gyroScope.getAngle()).append(",").append(accelerometerSubsystem.gyroScope.getRate()).append(",");
                toWrite.append(accelerometerSubsystem.gyroScope2.getAngle()).append(",").append(accelerometerSubsystem.gyroScope2.getRate()).append("\n");
                FileManager.appendFile(file, toWrite.toString());
                timer = 0;
            }
        }
    }
}
// https://docs.wpilib.org/en/stable/docs/software/hardware-apis/pneumatics/pneumatics.html