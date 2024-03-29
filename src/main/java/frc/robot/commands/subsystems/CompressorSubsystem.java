package frc.robot.commands.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class CompressorSubsystem extends SubsystemBase {

    private Compressor compressor;

    public CompressorSubsystem() {
        this.compressor = new Compressor(PneumaticsModuleType.CTREPCM);
    }

    private boolean compressorEnabled = true;

    public void disableCompressor() {
        compressorEnabled = false;
    }

    public void enableCompressor() {
        compressorEnabled = true;
    }

    @Override
    public void periodic() {
        if(compressor.getPressureSwitchValue()) {
            compressorEnabled = false;
        }
        if(compressorEnabled) {
            SmartDashboard.putBoolean("Compressor", true);
            compressor.enableDigital(); // Only a specific REV motor can use analog w/ min and maxes
        } else {
            SmartDashboard.putBoolean("Compressor", false);
            compressor.disable();
        }
    }
}
