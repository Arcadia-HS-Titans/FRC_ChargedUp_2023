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
    private DoubleSolenoidSubsystem doubleSolenoidSubsystem;
    private CompressorSubsystem compressorSubsystem;

    public AutoCommunityCommand(DrivingSubsystem drivingSubsystem, Joystick joystick,
                                DoubleSolenoidSubsystem doubleSolenoidSubsystem, CompressorSubsystem compressorSubsystem) {
        this.drivingSubsystem = drivingSubsystem;
        this.doubleSolenoidSubsystem = doubleSolenoidSubsystem;
        this.compressorSubsystem = compressorSubsystem;
        this.addRequirements(drivingSubsystem);
        this.addRequirements(doubleSolenoidSubsystem);
        this.addRequirements(compressorSubsystem);
        this.joystick = joystick;
    }

    private SimpleCounter counter;
    private SimpleCounter scoreOnce;
    @Override
    public void execute() {
        compressorSubsystem.enableCompressor();
        if(!scoreOnce.tick()) {
            if(scoreOnce.time < 100) {
                doubleSolenoidSubsystem.shortArm.open();
            } else {
                doubleSolenoidSubsystem.shortArm.close();
            }
            drivingSubsystem.arcadeDrive(0, 0);
            return;
        }

        if(!counter.tick()) {
            drivingSubsystem.arcadeDrive(-.65, 0);
        } else drivingSubsystem.arcadeDrive(0,0);
    }

    @Override
    public void initialize() {
        counter = new SimpleCounter(150, SimpleCounter.Behavior.ONCE);
        scoreOnce = new SimpleCounter(150, SimpleCounter.Behavior.ONCE);
        super.initialize();
    }
}