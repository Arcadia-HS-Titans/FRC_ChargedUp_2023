package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.subsystems.*;
import frc.robot.utils.*;

import java.io.File;

public class AutoCommand extends CommandBase {

    public enum AutoPhase {
        GETTING_ON_RAMP,
        APPROACHING_OBJECT,
        GETTING_OBJECT,
        BALANCE_ON_RAMP
    }

    private boolean rampButtonPressed = false;
    private boolean useRamps = false;

    public BalanceV3 balanceV3 = new BalanceV3();

    private  Joystick joystick;
    public AccelerometerSubsystem accelerometerSubsystem;
    public DoubleSolenoidSubsystem doubleSolonoidSubsystem;
    public DrivingSubsystem drivingSubsystem;
    public USSubsystem usSystem;
    private CompressorSubsystem compressorSubsystem;

    public AutoCommand(AccelerometerSubsystem accelerometerSubsystem,
                       DoubleSolenoidSubsystem doubleSolonoidSubsystem,
                       DrivingSubsystem drivingSubsystem,
                       USSubsystem usSystem,
                       CompressorSubsystem compressorSubsystem,
                       Joystick joystick) {
        this.joystick = joystick;
        this.accelerometerSubsystem = accelerometerSubsystem;
        this.doubleSolonoidSubsystem = doubleSolonoidSubsystem;
        this.drivingSubsystem = drivingSubsystem;
        this.usSystem = usSystem;
        this.compressorSubsystem = compressorSubsystem;
        this.addRequirements(accelerometerSubsystem);
        this.addRequirements(doubleSolonoidSubsystem);
        this.addRequirements(drivingSubsystem);
        this.addRequirements(compressorSubsystem);
        if(usSystem != null)
            this.addRequirements(usSystem);
    }

    @Override
    public void initialize() {
        balanceV3 = new BalanceV3();
        accelerometerSubsystem.calibrate();
        calibrator = new SimpleCounter(150, SimpleCounter.Behavior.ONCE);
        pushObject = new SimpleCounter(150, SimpleCounter.Behavior.ONCE);
    }

    private SimpleCounter subPhase = new SimpleCounter(5, SimpleCounter.Behavior.ONCE);
    public void gettingOverRamp() {
        // Ride towards ramp
        if(subPhase.time == 0) {
            // Go forwards until bump
            drivingSubsystem.arcadeDrive(.9, 0);
            if(HelpfulMath.isInRange(5, accelerometerSubsystem.getAngle())) {
                subPhase.tick();
            }
            return;
        }
        if(subPhase.time == 1) {
            // We're now on the ramp, go until we're over
            drivingSubsystem.arcadeDrive(.8, 0); // Go slower just in case
            if(HelpfulMath.isInRange(accelerometerSubsystem.getAngle(), 4)) {
                subPhase.tick();
            }
            return;
        }

        if(subPhase.time == 2) {
            if(HelpfulMath.isInRange(4, accelerometerSubsystem.getAngle())) {
                drivingSubsystem.arcadeDrive(.8, 0);
                return;
            }
            drivingSubsystem.arcadeDrive(.6, 0);
            //if(usSystem.getReading() < 12) {
            drivingSubsystem.arcadeDrive(0, 0);
            subPhase.tick();
            // }
        }

        if(subPhase.time == 3) {
            // TODO: grab then tick and do ramp then finish
        }
    }
    public void gettingObject() {}
    public void balancingOnRamp() {}

    private SimpleCounter calibrator;
    private SimpleCounter pushObject;

    @Override
    public void execute() {
        // Called every 20 ms
        //gettingOverRamp();
        compressorSubsystem.enableCompressor();

        if(!pushObject.tick()) {
            if(pushObject.time < 100)
                doubleSolonoidSubsystem.shortArm.open();
            else
                doubleSolonoidSubsystem.shortArm.close();
        }

        if(!calibrator.tick()) {
            accelerometerSubsystem.tickCalibration();
            return;
        }
        if(accelerometerSubsystem.calibrating) {
            accelerometerSubsystem.assignConst();
        }
        balanceV3.balanceOnRamp(accelerometerSubsystem, drivingSubsystem, false);
        recordGyros();
    }

    private final SimpleCounter recordingTimer = new SimpleCounter(0, SimpleCounter.Behavior.INFINITE);
    private long startTime = System.currentTimeMillis();
    private int iteration = 0;
    private boolean loggingData = false;
    private boolean dataPressed = false;
    public void recordGyros() {
        File file;
        if(!dataPressed) {
                SmartDashboard.putBoolean("Recording Data", true);
                startTime = System.currentTimeMillis();
                iteration++;
                file = new File("/home/lvuser/GyroData" + iteration + ".csv");
                FileManager.writeFile(file, "Time,G1Angle,G1Rate,AccelX,AccelY,AccelZ,Phase" +
                        ",Power,JoystickX,JoystickY\n");
            dataPressed = true;
        }

        if(recordingTimer.tick()) {
            file = new File("/home/lvuser/GyroData" + iteration + ".csv");
            StringBuilder toWrite = new StringBuilder().append(System.currentTimeMillis() - startTime).append(",");
            toWrite.append(accelerometerSubsystem.getAngle()).append(",")
                    .append(accelerometerSubsystem.getCurrentRate()).append(",");
            toWrite.append(accelerometerSubsystem.getX()).append(",");
            toWrite.append(accelerometerSubsystem.getY()).append(",");
            toWrite.append(accelerometerSubsystem.getZ()).append(",");
            toWrite.append(BalanceV3.currentPhase).append(",");
            toWrite.append(drivingSubsystem.currentPower).append(",");
            toWrite.append(joystick.getZ()).append(",");
            toWrite.append(joystick.getY()).append(",").append("\n");
            FileManager.appendFile(file, toWrite.toString());
        }
    }
}