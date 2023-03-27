// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.*;
import frc.robot.commands.subsystems.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    // The robot's subsystems
    private final AccelerometerSubsystem accelerometerSubsystem;
    private final DoubleSolenoidSubsystem doubleSolenoidSubsystem;
    private final DrivingSubsystem drivingSubsystem;
    private USSubsystem usSubsystem;
    private final CompressorSubsystem compressorSubsystem;
    private final WinchSubsystem winchSubsystem;

    // Devices
    private final Joystick joystick = new Joystick(0);
    private final Joystick gamepad = new Joystick(1);

    // The main commands of the robot
    private final Command teleopCommand;
    private final Command autoCommand;
    private final Command autoCommunityCommand;


    // A chooser for autonomous commands
    SendableChooser<Command> m_chooser = new SendableChooser<>();

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        try {
            CameraServer.startAutomaticCapture();
            //CameraServer.startAutomaticCapture(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Shuffleboard.addEventMarker("A", "a", EventImportance.kNormal);

        this.accelerometerSubsystem = new AccelerometerSubsystem();
        this.doubleSolenoidSubsystem = new DoubleSolenoidSubsystem();
        this.drivingSubsystem = new DrivingSubsystem();
        this.usSubsystem = null;
        this.compressorSubsystem = new CompressorSubsystem();
        this.winchSubsystem = new WinchSubsystem();

        this.teleopCommand = new DrivingTeleopCommand(accelerometerSubsystem, doubleSolenoidSubsystem,
                drivingSubsystem, usSubsystem, compressorSubsystem, winchSubsystem, joystick, gamepad);
        this.autoCommand = new AutoCommand(accelerometerSubsystem, doubleSolenoidSubsystem,
                drivingSubsystem, usSubsystem, compressorSubsystem, joystick);
        this.autoCommunityCommand = new AutoCommunityCommand(drivingSubsystem, joystick, doubleSolenoidSubsystem,
                compressorSubsystem);

        // Add commands to the autonomous command chooser
        //m_chooser.setDefaultOption("No Ramp Auto", teleopCommand); // TODO: Actual option
        m_chooser.setDefaultOption("Ramp Auto", autoCommand);
        m_chooser.addOption("Taxi Auto", autoCommunityCommand);

        // Put the chooser on the dashboard
        Shuffleboard.getTab("Autonomous").add(m_chooser);
        //Shuffleboard.getTab("TeleOperated").add(m_chooser);
    }

    public Command getTeleopCommand() {
        return teleopCommand;
    }

    public Command getAutoCommand() {
        return m_chooser.getSelected();
    }
}