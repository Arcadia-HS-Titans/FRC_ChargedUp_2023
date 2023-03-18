package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.commands.subsystems.AccelerometerSubsystem;
import frc.robot.commands.subsystems.DrivingSubsystem;
import frc.robot.utils.HelpfulMath;
import frc.robot.utils.SimpleCounter;
import frc.robot.utils.Vector3;

public class BalanceV3 {

    public enum RampPhase {
        NONE,
        STARTING,
        GETTING_ON_FIRST_RAMP,
        ON_FIRST_RAMP,
        BALANCING,
        FALLING
    }

    private boolean reached = false;

    private SimpleCounter approachRamp; // 500 ms
    public static RampPhase currentPhase;
    private Vector3 recordedAcceleration;
    private double balancingPower;
    private double reachedBump;

    private SimpleCounter firstRampDelay; //2400 ms

    private boolean forwards = true; // TODO: Finish implementing

    public void balanceOnRamp(AccelerometerSubsystem accelerometerSubsystem, DrivingSubsystem drivingSubsystem) {
/*        if(reached) {
            drivingSubsystem.arcadeDrive(0, 0);
            return;
        }*/
        double angle = accelerometerSubsystem.getAngle();
        double currentRate = accelerometerSubsystem.getCurrentRate();
        DriverStation.reportWarning("Phase " + currentPhase, false);
        if(currentPhase == RampPhase.STARTING) {
            Vector3 accelerometerAxis = (accelerometerSubsystem.getAcceleration());
            // Start going up the platform
            drivingSubsystem.arcadeDrive(.75, 0.); // Drive forwards
            // Wait .5 seconds to get the current acceleration (if we get it when we're still, it'll see us level)
            // TODO later, how can we make this not dependent on time?
            if(!approachRamp.tick()) return;

            // It's been 500 ms, let's get the current acceleration
            if(recordedAcceleration == null)
                recordedAcceleration = accelerometerAxis;
            DriverStation.reportWarning("Acceleration: " + accelerometerAxis, false);
            // TODO: Make sure it's the Y axis we're measuring
            if(recordedAcceleration.y > accelerometerAxis.y+.1) {
                // We've hit a jump, we're now climbing on something
                currentPhase = RampPhase.GETTING_ON_FIRST_RAMP;
            }
        } else if(currentPhase == RampPhase.GETTING_ON_FIRST_RAMP) {
            // We hit the ramp, we're either at an angle of 0-26, we can never know, what can we do to fix this?
            // We can use a timer to see how long we've been on the position, maybe 2/3 sec. to balance the above?
            // Also check the angle in this range to determine what we need to do

            if (!firstRampDelay.tick()) {
                drivingSubsystem.arcadeDrive(.75, 0);
            } else {
                drivingSubsystem.arcadeDrive(.75, 0);
                currentPhase = RampPhase.ON_FIRST_RAMP;
            }
        } else if (currentPhase == RampPhase.ON_FIRST_RAMP) {
            // Let's iterate until we get the bump in the wood
            // TODO: I don't like this, let's get rid of it for now, it existed on Phoebe though we shouldn't need it
/*            if(reachedBump < 1) {
                drivingSubsystem.arcadeDrive(.6, 0);
                // Check if our dR is greater than 4, if it is we've reached the first bump
                if(HelpfulMath.isInRange(4, dRate))
                    reachedBump++;
                DriverStation.reportWarning("Not reached bump", false);
                return;
            }*/
            // We can now do our balancing, why do we set a timer? Why can't we continually check?
            // We check rate so that we don't go overboard
            DriverStation.reportWarning("Reached bump", false);
            if(HelpfulMath.isInRange(7.5, currentRate)) {
                // We're tipping over, freeze for now
                drivingSubsystem.arcadeDrive(0, 0);
            } else {
                // We're not tipping over
                if(angle > 5) {
                    drivingSubsystem.arcadeDrive(balancingPower, 0);

                } else if(angle < -5) {
                    drivingSubsystem.arcadeDrive(-balancingPower, 0);
                } else {
                    if (angle > -5 && angle < -2) {
                        drivingSubsystem.arcadeDrive(-(balancingPower - .04), 0);
                    } else if(angle < 5 && angle > 2) {
                        drivingSubsystem.arcadeDrive((balancingPower - .04), 0);
                    } else {
                        drivingSubsystem.arcadeDrive(0, 0);
                        reached = true;
                    }
                }
            }
        }
    }

    public BalanceV3() {
        DriverStation.reportWarning("Reset Ramp Balancing stuff", false);
        approachRamp = new SimpleCounter(25, SimpleCounter.Behavior.ONCE); // 500 ms
        firstRampDelay = new SimpleCounter(60, SimpleCounter.Behavior.ONCE); // was 120 on phoebe
        recordedAcceleration = null;
        balancingPower = .65;// .63 on phoebe
        currentPhase = RampPhase.STARTING;
        reachedBump = 0;
    }
}
