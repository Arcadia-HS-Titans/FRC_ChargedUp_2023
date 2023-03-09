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

    private SimpleCounter approachRamp; // 500 ms
    private RampPhase currentPhase;
    private Vector3 recordedAcceleration;

    private double balancingPower;
    public static double currentPower = 0;

    private double prevRate;
    private static final double STEP_SIZE = 0.01;
    private double reachedBump;

    public void balanceOnRamp(AccelerometerSubsystem accelerometerSubsystem, DrivingSubsystem drivingSubsystem) {
        double angle = accelerometerSubsystem.gyroScope.getAngle();
        double currentRate = accelerometerSubsystem.gyroScope.getRate();
        double dRate = Math.abs(currentRate) - Math.abs(prevRate);
        prevRate = currentRate;
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
            if(recordedAcceleration.y < accelerometerAxis.y) {
                // We've hit a jump, we're now climbing on something
                currentPhase = RampPhase.GETTING_ON_FIRST_RAMP;
            }
        } else if(currentPhase == RampPhase.GETTING_ON_FIRST_RAMP) {
            // We hit the ramp, we're either at an angle of 0-26, we can never know, what can we do to fix this?
            // We can use a timer to see how long we've been on the position, maybe 2/3 sec. to balance the above?
            // Also check the angle in this range to determine what we need to do

            if (HelpfulMath.isInRange(angle, 5)) {
                drivingSubsystem.arcadeDrive(.75, 0);
            } else {
                drivingSubsystem.arcadeDrive(.75, 0);
                currentPhase = RampPhase.ON_FIRST_RAMP;
            }
        } else if (currentPhase == RampPhase.ON_FIRST_RAMP) {
            // Let's iterate until we get the bump in the wood
            if(reachedBump < 1) {
                drivingSubsystem.arcadeDrive(.6, 0);
                // Check if our dR is greater than 4, if it is we've reached the first bump
                if(HelpfulMath.isInRange(4, dRate))
                    reachedBump++;
                DriverStation.reportWarning("Not reached bump", false);
                return;
            }
            // We can now do our balancing, why do we set a timer? Why can't we continually check?
            // We check rate so that we don't go overboard
            DriverStation.reportWarning("Reached bump", false);
            if(HelpfulMath.isInRange(7, currentRate)) {
                // We're tipping over, freeze for now
                drivingSubsystem.arcadeDrive(0, 0);
            } else {
                // We're not tipping over
                if(angle > 7)
                    drivingSubsystem.arcadeDrive(-balancingPower, 0);
                else if(angle < -7)
                    drivingSubsystem.arcadeDrive(balancingPower, 0);
                else
                    drivingSubsystem.arcadeDrive(0, 0);
            }
        }
    }

    public BalanceV3() {
        DriverStation.reportWarning("Reset Ramp Balancing stuff", false);
        approachRamp = new SimpleCounter(25, SimpleCounter.Behavior.ONCE); // 500 ms
        recordedAcceleration = null;
        balancingPower = .59;
        currentPower = 0;
        currentPhase = RampPhase.STARTING;
        reachedBump = 0;
    }
}
