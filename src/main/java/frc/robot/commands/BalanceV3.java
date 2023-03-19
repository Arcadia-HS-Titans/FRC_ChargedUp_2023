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
    }

    private SimpleCounter approachRamp; // 500 ms
    public static RampPhase currentPhase;
    private Vector3 recordedAcceleration;
    private double balancingPower;

    private SimpleCounter firstRampDelay; //2400 ms

    public void balanceOnRamp(AccelerometerSubsystem accelerometerSubsystem, DrivingSubsystem drivingSubsystem,
                              boolean direction) { // true = forwards, false = backwards
        double angle = accelerometerSubsystem.getAngle();
        double currentRate = accelerometerSubsystem.getCurrentRate();
        DriverStation.reportWarning("Phase " + currentPhase, false);
        if(currentPhase == RampPhase.STARTING) {
            Vector3 accelerometerAxis = (accelerometerSubsystem.getAcceleration());
            // Start going up the platform
            drivingSubsystem.arcadeDrive(.75, 0., direction); // Drive forwards
            // Wait .5 seconds to get the current acceleration (if we get it when we're still, it'll see us level)
            if(!approachRamp.tick()) return;

            // It's been 500 ms, let's get the current acceleration
            if(recordedAcceleration == null)
                recordedAcceleration = accelerometerAxis;
            if(recordedAcceleration.y > accelerometerAxis.y+.1) {
                // We've hit a jump, we're now climbing on something
                currentPhase = RampPhase.GETTING_ON_FIRST_RAMP;
            }
        } else if(currentPhase == RampPhase.GETTING_ON_FIRST_RAMP) {
            if (!firstRampDelay.tick()) {
                drivingSubsystem.arcadeDrive(.75, 0, direction);
                return;
            }

            drivingSubsystem.arcadeDrive(.75, 0, direction);
            currentPhase = RampPhase.ON_FIRST_RAMP;
        } else if (currentPhase == RampPhase.ON_FIRST_RAMP) {
            // We check rate so that we don't go overboard
            DriverStation.reportWarning("Reached bump", false);
            if(HelpfulMath.isInRange(7.5, currentRate)) {
                // We're tipping over, freeze for now
                drivingSubsystem.arcadeDrive(0, 0);
                return;
            }
            // We're not tipping over
            if(angle > 6) {
                drivingSubsystem.arcadeDrive(balancingPower, 0);
                return;
            } else if(angle < -5) {
                drivingSubsystem.arcadeDrive(-balancingPower, 0);
                return;
            }

            if (angle > -5 && angle < -2) {
                drivingSubsystem.arcadeDrive(-(balancingPower - .04), 0);
                return;
            } else if(angle < 5 && angle > 2) {
                drivingSubsystem.arcadeDrive((balancingPower - .04), 0);
                return;
            }

            drivingSubsystem.arcadeDrive(0, 0);
            // TODO: Counter system to stop the gyro drift from not balancing anymore (rate < 2 for 2 secs.)
        }
    }

    public BalanceV3() {
        DriverStation.reportWarning("Reset Ramp Balancing stuff", false);
        approachRamp = new SimpleCounter(25, SimpleCounter.Behavior.ONCE); // 500 ms
        firstRampDelay = new SimpleCounter(120, SimpleCounter.Behavior.ONCE); // was 120 on phoebe
        recordedAcceleration = null;
        balancingPower = .67;// .63 on phoebe
        currentPhase = RampPhase.STARTING;
    }
}
