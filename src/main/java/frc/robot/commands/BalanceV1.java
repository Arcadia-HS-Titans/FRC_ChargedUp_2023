
package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.utils.FileManager;
import frc.robot.commands.subsystems.AccelerometerSubsystem;
import frc.robot.commands.subsystems.DoubleSolonoidSubsystem;
import frc.robot.commands.subsystems.DrivingSubsystem;
import frc.robot.utils.HelpfulMath;
import frc.robot.utils.SimpleCounter;
import frc.robot.utils.Vector3;

import java.io.File;

// This is Friday's code which worked by constantly going down and back up the ramp
public class BalanceV1 {

    public enum RampPhase {
        NONE,
        STARTING,
        GETTING_ON_FIRST_RAMP,
        ON_FIRST_RAMP,
        BALANCING,
        FALLING
    }

    private SimpleCounter approachRamp = new SimpleCounter(25, SimpleCounter.Behavior.ONCE); // 500 ms
    private RampPhase currentPhase = RampPhase.NONE;
    private Vector3 recordedAcceleration = null;

    private SimpleCounter firstRampTimer = new SimpleCounter(50, SimpleCounter.Behavior.ONCE);

    public void balanceOnRamp(AccelerometerSubsystem accelerometerSubsystem, DrivingSubsystem drivingSubsystem) {
        double angle = accelerometerSubsystem.gyroScope.getAngle();
        double rate = accelerometerSubsystem.gyroScope.getRate();
        DriverStation.reportWarning("Phase " + currentPhase, false);
        if(currentPhase == RampPhase.STARTING) {
            Vector3 accelerometerAxis = new Vector3(accelerometerSubsystem.accelerometer);
            // Start going up the platform
            drivingSubsystem.arcadeDrive(.75, 0.); // Drive forwards
            // Wait .5 seconds to get the current acceleration (if we get it when we're still, it'll see us level)
            // TODO later, how can we make this not dependent on time?
            if(!approachRamp.tick()) return;

            // It's been 500 ms, let's get the current acceleration
            if(recordedAcceleration == null)
                recordedAcceleration = accelerometerAxis;

            // TODO: Make sure it's the Y axis we're measuring
            if(recordedAcceleration.z < accelerometerAxis.z) {
                // We've hit a jump, we're now climbing on something
                currentPhase = RampPhase.GETTING_ON_FIRST_RAMP;
            }
        } else if(currentPhase == RampPhase.GETTING_ON_FIRST_RAMP) {
            // We hit the ramp, we're either at an angle of 0-26, we can never know, what can we do to fix this?
            // We can use a timer to see how long we've been on the position, maybe 2/3 sec. to balance the above?
            // Also check the angle in this range to determine what we need to do

            if (angle < 5 && angle > -5) {
                drivingSubsystem.arcadeDrive(.75, 0);
            } else {
                drivingSubsystem.arcadeDrive(.65, 0);
                currentPhase = RampPhase.ON_FIRST_RAMP;
            }
        } else if (currentPhase == RampPhase.ON_FIRST_RAMP) {
            if (firstRampTimer.tick() == false){
                drivingSubsystem.arcadeDrive(0.6, 0);
                return;
            }
            if (HelpfulMath.isInRange(50, rate)) {
                drivingSubsystem.arcadeDrive(0, 0);
                //TODO: Add timer for it to wait to shift (maybe even make it go backwards a little bit?)
            }
            //HelpfulMath.isInRange(angle, degreeRange);
            if (angle < -7) {
                drivingSubsystem.arcadeDrive(0.6, 0);
            } else if(angle > 7) {
                drivingSubsystem.arcadeDrive(-.6, 0);
            } else if(HelpfulMath.isInRange(angle, 2)) {
                drivingSubsystem.arcadeDrive(0, 0);
            }

        }
    }

    public BalanceV1() {
        approachRamp = new SimpleCounter(25, SimpleCounter.Behavior.ONCE); // 500 ms
        currentPhase = RampPhase.NONE;
        recordedAcceleration = null;
        firstRampTimer = new SimpleCounter(50, SimpleCounter.Behavior.ONCE);
    }
}
// https://docs.wpilib.org/en/stable/docs/software/hardware-apis/pneumatics/pneumatics.html