package frc.robot.commands;

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

    @Override
    public void initialize() {
        balanceV3 = new BalanceV3();
        accelerometerSubsystem.calibrate();
        highToggle = new SimpleToggle();
        midToggle = new SimpleToggle();
        lowToggle = new SimpleToggle();
        gripperToggle = new SimpleToggle();
        //accelerometerSubsystem.gyroScope.calibrate();
    }

    public static final class SimpleToggle{
        private boolean pressed;
        private boolean toggled;

        public SimpleToggle() {
            pressed = false;
            toggled = false;
        }

        public boolean update(boolean status) {
            if(status) {
                if(!pressed) {
                    pressed = true;
                    toggled = !toggled;
                }
            } else pressed = false;
            return toggled;
        }
    }

    private SimpleToggle lowToggle = new SimpleToggle();
    private SimpleToggle midToggle = new SimpleToggle();
    private SimpleToggle highToggle = new SimpleToggle();
    private SimpleToggle gripperToggle = new SimpleToggle();

    @Override
    public void execute() {
        // Called every 20 ms
        //SmartDashboard.putNumber("US Inches", usSystem.getReading());
        if(joystick.getRawButton(11)) {
            if(!rampButtonPressed) {
                rampButtonPressed = true;
                useRamps = !useRamps;
            }
        } else rampButtonPressed = false;
        if(useRamps) {
            balanceV3.balanceOnRamp(accelerometerSubsystem, drivingSubsystem);
        } else {
            /*if(lowToggle.update(gamepad.getRawButton(1))) {
                doubleSolonoidSubsystem.armHigh.close();
                doubleSolonoidSubsystem.armMid.close();
            }
            if(midToggle.update(gamepad.getRawButton(2)) || midToggle.update(gamepad.getRawButton(3))) {
                doubleSolonoidSubsystem.armHigh.open();
                doubleSolonoidSubsystem.armMid.close();
            }
            if(highToggle.update(gamepad.getRawButton(4))) {
                doubleSolonoidSubsystem.armMid.open();
                doubleSolonoidSubsystem.armHigh.open();
            }*/


            if(highToggle.update(gamepad.getRawButton(1))) {
                doubleSolonoidSubsystem.longArm.open();
            } else doubleSolonoidSubsystem.longArm.close();
            if(lowToggle.update(gamepad.getRawButton(4))) {
                doubleSolonoidSubsystem.shortArm.open();
            } else doubleSolonoidSubsystem.shortArm.close();



            if(gripperToggle.update(gamepad.getRawButton(6))) {
                doubleSolonoidSubsystem.gripper.open();
            } else doubleSolonoidSubsystem.gripper.close();

            winchSubsystem.setMotor(gamepad.getRawAxis(1));

/*            if(joystick.getRawAxis(0) > 0.5) {
                drivingSubsystem.arcadeDrive(-joystick.getY(), joystick.getZ());
            } else {
                drivingSubsystem.arcadeDrive(-joystick.getY() * .98, joystick.getZ() * .87);
            }*/
            drivingSubsystem.arcadeDrive(-joystick.getY() * .98, joystick.getZ() * .87);
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