package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DrivingSubsystem extends SubsystemBase {
    // Left motors
    private final PWMVictorSPX GREEN_MOTOR = new PWMVictorSPX(Constants.GREEN_MOTOR_PORT);
    private final PWMVictorSPX YELLOW_MOTOR = new PWMVictorSPX(Constants.YELLOW_MOTOR_PORT);
    MotorController rightController = new MotorControllerGroup(GREEN_MOTOR, YELLOW_MOTOR);

    //Right motors
    private final PWMVictorSPX RED_MOTOR = new PWMVictorSPX(Constants.RED_MOTOR_PORT);
    private final PWMVictorSPX ORANGE_MOTOR = new PWMVictorSPX(Constants.ORANGE_MOTOR_PORT);
    MotorController leftController = new MotorControllerGroup(RED_MOTOR, ORANGE_MOTOR);
    public double currentPower = 0;

    // Differential drive to control the robot
    private final DifferentialDrive robotDrive = new DifferentialDrive(leftController, rightController);

    public DrivingSubsystem() {}

    public void arcadeDrive(double forward, double rotation) {
        currentPower = forward;
        robotDrive.arcadeDrive(rotation, -forward);
    }

    public void arcadeDrive(double forward, double rotation,  boolean direction) {
        // dir = true = forwards, false = backwards
        currentPower = forward;
        if(direction) {
            robotDrive.arcadeDrive(rotation, -forward);
        } else {
            robotDrive.arcadeDrive(rotation, forward);
        }
    }
}
