package frc.robot.utils;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;

public class Vector3Sendable implements Sendable {

    private double[] vector3 = new double[3];
    public Vector3Sendable(double[] vector3) {
        this.vector3 = vector3;
    }
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Working Accelerometer");
        builder.addDoubleProperty("X", () -> vector3[0], value -> {});
        builder.addDoubleProperty("Y", () -> vector3[1], value -> {});
        builder.addDoubleProperty("Z", () -> vector3[2], value -> {});
        builder.update();
    }
}
