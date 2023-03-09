package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.utils.FileManager;
import frc.robot.commands.subsystems.AccelerometerSubsystem;
import frc.robot.commands.subsystems.DoubleSolonoidSubsystem;
import frc.robot.commands.subsystems.DrivingSubsystem;
import frc.robot.utils.HelpfulMath;
import frc.robot.utils.SimpleCounter;
import frc.robot.utils.Vector3;

import java.io.File;

public class DrivingTeleopCommand extends CommandBase {

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
    private RampPhase currentPhase = RampPhase.NONE;
    private Vector3 recordedAcceleration = null;

    private SimpleCounter firstRampTimer = new SimpleCounter(50, SimpleCounter.Behavior.ONCE);
    private SimpleCounter fallingTimer = new SimpleCounter(20, SimpleCounter.Behavior.INFINITE);
    private double balancingPower;
    public static double currentPower = 0;

    private boolean rampTimerFinished = true;

    private double prevRate;
    private static final double STEP_SIZE = 0.01;
    public BalanceV3 balanceV3 = new BalanceV3();

    public void balanceOnRamp() {
        balanceV3.balanceOnRamp(accelerometerSubsystem, drivingSubsystem);
    }

    private final Joystick joystick;
    public AccelerometerSubsystem accelerometerSubsystem;
    public DoubleSolonoidSubsystem doubleSolonoidSubsystem;
    public DrivingSubsystem drivingSubsystem;
    private boolean loggingData = false;
    boolean pressed = false;
    private int timer = 0;
    private int maxTimer = 5;
    private long startTime = System.currentTimeMillis();
    int iteration = 0;


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
    }

    @Override
    public void execute() {
        // Called every 20 ms
        if(joystick.getRawButton(2)) {
            if(!pressedRamps) {
                pressedRamps = true;
                toggleRamps = !toggleRamps;
            }
        } else pressedRamps = false;
        if(toggleRamps) {
            if(currentPhase == RampPhase.NONE)
                currentPhase = RampPhase.STARTING;
            balanceOnRamp();
        } else {
            drivingSubsystem.arcadeDrive(-joystick.getY(), joystick.getZ());
        }
        recordGyros();
    }



    public void recordGyros() {
        File file;
        if(joystick.getRawButton(1)) {
            if(!pressed) {
                loggingData = !loggingData;
                if(loggingData) {
                    DriverStation.reportWarning("Started recording!", false);
                    startTime = System.currentTimeMillis();
                    iteration++;
                    file = new File("/home/lvuser/GyroData" + iteration + ".csv");
                    FileManager.writeFile(file, "Time,G1Angle,G1Rate,G2Angle,G2Rate,AccelX,AccelY,AccelZ,Phase" +
                            ",RampTimer,Power\n");
                } else DriverStation.reportWarning("Stopped recording!", false);
                pressed = true;
            }
        } else pressed = false;
        file = new File("/home/lvuser/GyroData" + iteration + ".csv");

        if(loggingData) {
            timer++;
            if(timer >= maxTimer) {
                Gyro gyro1 = accelerometerSubsystem.gyroScope;
                Gyro gyro2 = accelerometerSubsystem.gyroScope2;
                Accelerometer accelerometer = accelerometerSubsystem.accelerometer;
                StringBuilder toWrite = new StringBuilder("").append(System.currentTimeMillis() - startTime).append(","); // In Java
                toWrite.append(gyro1.getAngle()).append(",").append(gyro1.getRate()).append(",");
                toWrite.append(gyro2.getAngle()).append(",").append(gyro2.getRate()).append(",");
                toWrite.append(accelerometer.getX()).append(",");
                toWrite.append(accelerometer.getY()).append(",");
                toWrite.append(accelerometer.getZ()).append(",");
                toWrite.append(currentPhase).append(",");
                toWrite.append(rampTimerFinished).append(",");
                toWrite.append(currentPower).append("\n");
            FileManager.appendFile(file, toWrite.toString());
                timer = 0;
            }
        }
    }
}
// https://docs.wpilib.org/en/stable/docs/software/hardware-apis/pneumatics/pneumatics.html