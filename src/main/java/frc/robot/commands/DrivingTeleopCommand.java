package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.subsystems.*;
import frc.robot.utils.*;

import java.io.File;

public class DrivingTeleopCommand extends CommandBase {

    private boolean rampButtonPressed = false;
    private boolean useRamps = false;

    public BalanceV3 balanceV3 = new BalanceV3();

    private final Joystick joystick;
    private final Joystick gamepad;
    private AccelerometerSubsystem accelerometerSubsystem;
    private DoubleSolenoidSubsystem doubleSolonoidSubsystem;
    private DrivingSubsystem drivingSubsystem;
    private USSubsystem usSystem;

    private CompressorSubsystem compressorSubsystem;
    private WinchSubsystem winchSubsystem;

    public DrivingTeleopCommand(AccelerometerSubsystem accelerometerSubsystem,
                                DoubleSolenoidSubsystem doubleSolonoidSubsystem,
                                DrivingSubsystem drivingSubsystem,
                                USSubsystem usSystem,
                                CompressorSubsystem compressorSubsystem,
                                WinchSubsystem winchSubsystem,
                                Joystick joystick, Joystick gamepad) {
        this.joystick = joystick;
        this.gamepad = gamepad;
        this.accelerometerSubsystem = accelerometerSubsystem;
        this.doubleSolonoidSubsystem = doubleSolonoidSubsystem;
        this.drivingSubsystem = drivingSubsystem;
        this.usSystem = usSystem;
        this.compressorSubsystem = compressorSubsystem;
        this.winchSubsystem = winchSubsystem;
        this.addRequirements(accelerometerSubsystem);
        this.addRequirements(doubleSolonoidSubsystem);
        this.addRequirements(drivingSubsystem);
        this.addRequirements(compressorSubsystem);
        this.addRequirements(winchSubsystem);
        if(usSystem != null)
            this.addRequirements(usSystem);
    }

    private SimpleToggle gripperToggle = new SimpleToggle();
    private SimpleToggle shortArm = new SimpleToggle();
    private SimpleToggle longArm = new SimpleToggle();
    private boolean sent;

    @Override
    public void initialize() {
        balanceV3 = new BalanceV3();
        accelerometerSubsystem.calibrate();
        gripperToggle = new SimpleToggle();
        shortArm = new SimpleToggle();
        longArm = new SimpleToggle();
        doubleSolonoidSubsystem.gripper.close();
        doubleSolonoidSubsystem.shortArm.close();
        doubleSolonoidSubsystem.longArm.close();
        compressorSubsystem.enableCompressor();
        sent = false;
        compressorTimer = new SimpleCounter(50*90, SimpleCounter.Behavior.ONCE);
    }

    public enum ArmPosition {
        LOW,
        MID,
        HIGH
    }

    private ArmPosition currentPos = ArmPosition.LOW;
    private SimpleCounter compressorTimer;

    @Override
    public void execute() {
        // Called every 20 ms
        if(joystick.getRawButton(11)) {
            if(!rampButtonPressed) {
                rampButtonPressed = true;
                useRamps = !useRamps;
            }
        } else rampButtonPressed = false;
        if(useRamps) {
            balanceV3.balanceOnRamp(accelerometerSubsystem, drivingSubsystem, true);
        } else {
            // Sebastion's Toggle system state machine
            if(gamepad.getRawButton(1)) {
                currentPos = ArmPosition.LOW;
            } else if(gamepad.getRawButton(2) || gamepad.getRawButton(3)) {
                currentPos = ArmPosition.MID;
            } else if(gamepad.getRawButton(4)) {
                currentPos = ArmPosition.HIGH;
            }
            if(currentPos == ArmPosition.LOW) {
                doubleSolonoidSubsystem.longArm.close();
                doubleSolonoidSubsystem.shortArm.close();
            } else if(currentPos == ArmPosition.MID) {
                doubleSolonoidSubsystem.shortArm.close();
                doubleSolonoidSubsystem.longArm.open();
            } else if(currentPos == ArmPosition.HIGH) {
                doubleSolonoidSubsystem.longArm.open();
                doubleSolonoidSubsystem.shortArm.open();
            }

/*            if(shortArm.update(gamepad.getRawButton(1))) {
                 doubleSolonoidSubsystem.shortArm.close();
            } else {
                doubleSolonoidSubsystem.shortArm.open();
            }
            if(longArm.update(gamepad.getRawButton(4))) {
                doubleSolonoidSubsystem.longArm.close();
            } else {
                doubleSolonoidSubsystem.longArm.open();
            }*/


            if(gripperToggle.update(gamepad.getRawButton(6))) {
                doubleSolonoidSubsystem.gripper.open();
            } else doubleSolonoidSubsystem.gripper.close();

            winchSubsystem.setMotor(gamepad.getRawAxis(1));
            double[] power = new double[]{-joystick.getY(), joystick.getZ() * .87};
            drivingSubsystem.arcadeDrive(power[0], power[1]);
            SmartDashboard.putNumber("Joystick Y", power[0]);
            SmartDashboard.putNumber("Joystick Z", power[1]);
            if(compressorTimer.tick()) {
                compressorSubsystem.disableCompressor();
            }
        }
        recordGyros();
    }


    private final SimpleCounter recordingTimer = new SimpleCounter(0, SimpleCounter.Behavior.INFINITE);
    private long startTime = System.currentTimeMillis();
    private int iteration = 0;
    private boolean loggingData = false;
    private boolean dataPressed = false;
    public void recordGyros() {
        File file;
        if(joystick.getRawButton(1)) {
            if(!dataPressed) {
                loggingData = !loggingData;
                if(loggingData) {
                    SmartDashboard.putBoolean("Recording Data", true);
                    startTime = System.currentTimeMillis();
                    iteration++;
                    file = new File("/home/lvuser/GyroData" + iteration + ".csv");
                    FileManager.writeFile(file, "Time,G1Angle,G1Rate,AccelX,AccelY,AccelZ,Phase" +
                            ",Power,JoystickX,JoystickY\n");
                } else SmartDashboard.putBoolean("Recording Data", false);
                dataPressed = true;
            }
        } else dataPressed = false;

        if(!loggingData) return;

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