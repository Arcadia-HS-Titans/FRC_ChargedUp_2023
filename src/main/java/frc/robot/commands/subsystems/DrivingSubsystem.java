package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMTalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DrivingSubsystem extends SubsystemBase {
    private MotorController motorControllerBL = new PWMTalonSRX(0);
    private MotorController motorControllerFL = new PWMTalonSRX(1);
    private MotorController motorControllerBR = new PWMTalonSRX(2);
    private MotorController motorControllerFR = new PWMTalonSRX(3);
    private MotorControllerGroup leftGroup = new MotorControllerGroup(motorControllerBL, motorControllerFL);
    private MotorControllerGroup rightGroup = new MotorControllerGroup(motorControllerFR, motorControllerBR);
    private DifferentialDrive drive = new DifferentialDrive(leftGroup, rightGroup);

    public DrivingSubsystem() {}

    public void drive(double xAxis, double yAxis) {
        drive.arcadeDrive(xAxis, yAxis);
    }
}
