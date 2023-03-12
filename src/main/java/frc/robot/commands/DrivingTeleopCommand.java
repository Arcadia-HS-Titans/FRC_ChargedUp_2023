package frc.robot.commands;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.utils.*;
import frc.robot.commands.subsystems.AccelerometerSubsystem;
import frc.robot.commands.subsystems.DoubleSolonoidSubsystem;
import frc.robot.commands.subsystems.DrivingSubsystem;

import java.io.File;

public class DrivingTeleopCommand extends CommandBase {

    private boolean rampButtonPressed = false;
    private boolean useRamps = false;

    public BalanceV3 balanceV3 = new BalanceV3();

    private final Joystick joystick;
    public AccelerometerSubsystem accelerometerSubsystem;
    public DoubleSolonoidSubsystem doubleSolonoidSubsystem;
    public DrivingSubsystem drivingSubsystem;

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
    public void initialize() {
        balanceV3 = new BalanceV3();
        accelerometerSubsystem.calibrate();
    }

    @Override
    public void execute() {
        // Called every 20 ms
        accelerometerSubsystem.update();
        if(joystick.getRawButton(2)) {
            if(!rampButtonPressed) {
                rampButtonPressed = true;
                useRamps = !useRamps;
            }
        } else rampButtonPressed = false;
        if(useRamps) {
            balanceV3.balanceOnRamp(accelerometerSubsystem, drivingSubsystem);
        } else {
            drivingSubsystem.arcadeDrive(-joystick.getY(), joystick.getZ());
            //BalanceV3.currentPhase = BalanceV3.RampPhase.NONE;
        }
        //SmartDashboard.putString("Phase", String.valueOf(BalanceV3.currentPhase));
        recordGyros();
    }


    private final SimpleCounter recordingTimer = new SimpleCounter(5, SimpleCounter.Behavior.INFINITE);
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
                    FileManager.writeFile(file, "Time,G1Angle,G1Rate,G2Angle,G2Rate,AccelX,AccelY,AccelZ,Phase" +
                            ",RampTimer,Power\n");
                } else SmartDashboard.putBoolean("Recording Data", false);
                dataPressed = true;
            }
        } else dataPressed = false;

        if(!loggingData) return;

        if(recordingTimer.tick()) {
            file = new File("/home/lvuser/GyroData" + iteration + ".csv");
            StringBuilder toWrite = new StringBuilder().append(System.currentTimeMillis() - startTime).append(",");
            toWrite.append(accelerometerSubsystem.getAngle()).append(",").append(accelerometerSubsystem.getCurrentRate()).append(",");
            toWrite.append(accelerometerSubsystem.getX()).append(",");
            toWrite.append(accelerometerSubsystem.getY()).append(",");
            toWrite.append(accelerometerSubsystem.getZ()).append(",");
            //toWrite.append(BalanceV3.currentPhase).append(",");
            toWrite.append(drivingSubsystem.currentPower).append("\n");
            FileManager.appendFile(file, toWrite.toString());
        }
    }
}
// https://docs.wpilib.org/en/stable/docs/software/hardware-apis/pneumatics/pneumatics.html