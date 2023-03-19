package frc.robot.utils;

public class SimpleToggle {
    private boolean pressed;
    private boolean toggled;

    public SimpleToggle() {
        pressed = false;
        toggled = false;
    }

    public boolean update(boolean status) {
        if(status) {
            if(!pressed) {
                pressed = true;
                toggled = !toggled;
            }
        } else pressed = false;
        return toggled;
    }
}