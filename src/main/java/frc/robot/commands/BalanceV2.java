package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.commands.subsystems.AccelerometerSubsystem;
import frc.robot.commands.subsystems.DrivingSubsystem;
import frc.robot.utils.HelpfulMath;
import frc.robot.utils.SimpleCounter;
import frc.robot.utils.Vector3;

public class BalanceV2 {

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
    private SimpleCounter approachRamp = new SimpleCounter(25, SimpleCounter.Behavior.ONCE); // 500 ms
    private DrivingTeleopCommand.RampPhase currentPhase = DrivingTeleopCommand.RampPhase.NONE;
    private Vector3 recordedAcceleration = null;

    private SimpleCounter firstRampTimer = new SimpleCounter(50, SimpleCounter.Behavior.ONCE);
    private SimpleCounter fallingTimer = new SimpleCounter(20, SimpleCounter.Behavior.INFINITE);
    private double balancingPower;
    public static double currentPower = 0;

    private boolean rampTimerFinished = true;

    private double prevRate;
    private static final double STEP_SIZE = 0.01;

    public void balanceOnRamp(AccelerometerSubsystem accelerometerSubsystem, DrivingSubsystem drivingSubsystem) {
        double angle = accelerometerSubsystem.gyroScope.getAngle();
        double currentRate = accelerometerSubsystem.gyroScope.getRate();
        double dRate = Math.abs(currentRate) - Math.abs(prevRate);
        prevRate = currentRate;
        DriverStation.reportWarning("Phase " + currentPhase, false);
        if(currentPhase == DrivingTeleopCommand.RampPhase.STARTING) {
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
                currentPhase = DrivingTeleopCommand.RampPhase.GETTING_ON_FIRST_RAMP;
            }
        } else if(currentPhase == DrivingTeleopCommand.RampPhase.GETTING_ON_FIRST_RAMP) {
            // We hit the ramp, we're either at an angle of 0-26, we can never know, what can we do to fix this?
            // We can use a timer to see how long we've been on the position, maybe 2/3 sec. to balance the above?
            // Also check the angle in this range to determine what we need to do

            if (HelpfulMath.isInRange(angle, 5)) {
                drivingSubsystem.arcadeDrive(.75, 0);
            } else {
                drivingSubsystem.arcadeDrive(.75, 0);
                currentPhase = DrivingTeleopCommand.RampPhase.ON_FIRST_RAMP;
            }
        } else if (currentPhase == DrivingTeleopCommand.RampPhase.ON_FIRST_RAMP) {
            if (!firstRampTimer.tick()) {
                drivingSubsystem.arcadeDrive(0.65, 0);
                return;
            }
            if (HelpfulMath.isInRange(4, dRate)) {
                if(rampTimerFinished) {
                    rampTimerFinished = false;
                    balancingPower = (angle > 0) ? Math.abs(balancingPower) : -Math.abs(balancingPower);
                    DriverStation.reportWarning("Started timer", false);
                }
            }
            if (!rampTimerFinished) {
                boolean finishTimerTick = fallingTimer.tick();
                if (!finishTimerTick) {
                    drivingSubsystem.arcadeDrive(0, 0);
                    return;
                } else {
/*                    rampTimerFinished = true;
                    balancingPower = (angle > 0) ? Math.abs(balancingPower)-STEP_SIZE : -Math.abs(balancingPower)-STEP_SIZE;
                    if(HelpfulMath.isInRange(.5, balancingPower)) balancingPower = .5;
                    drivingSubsystem.arcadeDrive(0, 0);
                    //return;*/
                }
            }
            if (angle < -7) {
                drivingSubsystem.arcadeDrive(balancingPower*1.2, 0);
            } else if(angle > 7) {
                drivingSubsystem.arcadeDrive(-balancingPower*1.2, 0);
            } else if(HelpfulMath.isInRange(angle, 4)) {
                drivingSubsystem.arcadeDrive(0, 0);
            }

        }
    }

    public BalanceV2() {
        DriverStation.reportWarning("Reset info", false);
        pressedRamps = false;
        toggleRamps = false;
        approachRamp = new SimpleCounter(25, SimpleCounter.Behavior.ONCE); // 500 ms
        recordedAcceleration = null;
        firstRampTimer = new SimpleCounter(50, SimpleCounter.Behavior.ONCE);
        fallingTimer = new SimpleCounter(30, SimpleCounter.Behavior.INFINITE);
        balancingPower = .59;
        currentPower = 0;
        rampTimerFinished = true;
        currentPhase = DrivingTeleopCommand.RampPhase.NONE;
    }
}
