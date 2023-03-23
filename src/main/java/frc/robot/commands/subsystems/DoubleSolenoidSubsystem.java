package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DoubleSolenoidSubsystem extends SubsystemBase {

    public static final class SolenoidStatus {
        DoubleSolenoid part;
        public boolean status; // False = closed, true = open
        public SolenoidStatus(int ch1, int ch2) {
            status = false;
            part = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, ch1, ch2);
        }
        public void close() {
            status = false;
            part.set(DoubleSolenoid.Value.kReverse);
        }
        public void open() {
            status = true;
            part.set(DoubleSolenoid.Value.kForward);
        }
        public void turnOff() {
            status=  false;
            part.set(DoubleSolenoid.Value.kOff);
        }
    }

    public SolenoidStatus shortArm;
    public SolenoidStatus longArm;
    public SolenoidStatus gripper;

    public DoubleSolenoidSubsystem() {
        longArm = new SolenoidStatus(5,4);
        shortArm = new SolenoidStatus(7,6);
        gripper = new SolenoidStatus(2,3);
        longArm.turnOff();
        shortArm.turnOff();
        gripper.turnOff();
    }

    @Override
    public void periodic() {
        SmartDashboard.putBoolean("Arm Mid", shortArm.status);
        SmartDashboard.putBoolean("Arm High", longArm.status);
        SmartDashboard.putBoolean("Gripper Sol.", gripper.status);
        super.periodic();
    }
}
