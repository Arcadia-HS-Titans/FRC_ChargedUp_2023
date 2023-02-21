package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DoubleSolonoidSubsystem extends SubsystemBase {
    public DoubleSolenoid solenoid1 = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);
    public DoubleSolenoid solenoid2 = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);
    //public Compressor compressor = new Compressor(4, PneumaticsModuleType.CTREPCM);
}
