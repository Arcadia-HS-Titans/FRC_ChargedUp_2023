package frc.robot.commands;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.subsystems.*;
import frc.robot.utils.FileManager;
import frc.robot.utils.SimpleCounter;

import java.io.File;

public class AutoCommunityCommand extends CommandBase {

    public DrivingSubsystem drivingSubsystem;
    public Joystick joystick;

    public AutoCommunityCommand(DrivingSubsystem drivingSubsystem, Joystick joystick) {
        this.drivingSubsystem = drivingSubsystem;
        this.addRequirements(drivingSubsystem);
        this.joystick = joystick;
    }

    private SimpleCounter counter = new SimpleCounter(175, SimpleCounter.Behavior.ONCE);
    @Override
    public void execute() {
        if(!counter.tick()) {
            drivingSubsystem.arcadeDrive(.65, 0);
        } else drivingSubsystem.arcadeDrive(0,0);
        recordGyros();
    }

    @Override
    public void initialize() {
        counter = new SimpleCounter(100, SimpleCounter.Behavior.ONCE);
        super.initialize();
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
            toWrite.append(joystick.getX()).append(",");
            toWrite.append(joystick.getY()).append(",").append("\n");
            FileManager.appendFile(file, toWrite.toString());
        }
    }
}