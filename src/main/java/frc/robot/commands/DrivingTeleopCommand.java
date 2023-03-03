package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.utils.FileManager;
import frc.robot.commands.subsystems.AccelerometerSubsystem;
import frc.robot.commands.subsystems.DoubleSolonoidSubsystem;
import frc.robot.commands.subsystems.DrivingSubsystem;
import frc.robot.utils.SimpleTimer;
import frc.robot.utils.Vector3;

import java.io.File;

/**
 * https://docs.wpilib.org/en/stable/docs/software/commandbased/commands.html#simple-command-example
 * https://github.com/wpilibsuite/allwpilib/blob/main/wpilibjExamples/src/main/java/edu/wpi/first/wpilibj/examples/hatchbottraditional/commands/DefaultDrive.java
 */
public class DrivingTeleopCommand extends CommandBase {

    public enum RampPhase {
        NONE,
        STARTING,
        GETTING_ON_FIRST_RAMP,
        ON_FIRST_RAMP,
        BALANCING,
        FALLING
    }

    private boolean pressedRamps = false;
    private boolean toggleRamps = false;
    private SimpleTimer approachRamp = new SimpleTimer(25, SimpleTimer.Behavior.ONCE); // 500 ms
    private RampPhase currentPhase = RampPhase.NONE;
    private Vector3 recordedAcceleration = null;

    public void balanceOnRamp() {
        double angle = accelerometerSubsystem.gyroScope.getAngle();
        double rate = accelerometerSubsystem.gyroScope.getRate();
        if(currentPhase == RampPhase.STARTING) {
            // TODO: Have the accelerometer auto-adjust the bot to face ramp, maybe error correcting later
            // This will be later and before this
            boolean autoAdjusting = false;
            if(autoAdjusting) {}
            // End auto adjusting and start climbing

            Vector3 accelerometerAxis = new Vector3(accelerometerSubsystem.accelerometer);
            // Start going up the platform
            drivingSubsystem.arcadeDrive(.8, 0.); // Drive forwards
            // Wait .5 seconds to get the current acceleration (if we get it when we're still, it'll see us level)
            // TODO later, how can we make this not dependent on time?
            if(!approachRamp.tick()) return;

            // It's been 500 ms, let's get the current acceleration
            if(recordedAcceleration == null)
                recordedAcceleration = accelerometerAxis;

            // TODO: Make sure it's the Y axis we're measuring
            if(recordedAcceleration.y < accelerometerAxis.y) {
                // We've hit a jump, we're now climbing on something
                currentPhase = RampPhase.GETTING_ON_FIRST_RAMP;
            }
        } else if(currentPhase == RampPhase.GETTING_ON_FIRST_RAMP) {
            // We hit the ramp, we're either at an angle of 0-26, we can never know, what can we do to fix this?
            // We can use a timer to see how long we've been on the position, maybe 2/3 sec. to balance the above?
            // Also check the angle in this range to determine what we need to do

            if (angle < 5) {
                drivingSubsystem.arcadeDrive(.75, 0);
            }
            else {
                drivingSubsystem.arcadeDrive(.65, 0);
                currentPhase = RampPhase.ON_FIRST_RAMP;
            }
        } else if (currentPhase == RampPhase.ON_FIRST_RAMP) {
            if (angle > 9) {

            }
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
            if(currentPhase == RampPhase.NONE)
                currentPhase = RampPhase.STARTING;
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