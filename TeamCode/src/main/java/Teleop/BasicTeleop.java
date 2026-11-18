
package Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import launchpad.Constants;
import launchpad.hardware.MotorConfig;
import launchpad.hardware.Motor;

@TeleOp(name = "Test Front Right")
public class BasicTeleop extends OpMode {

    private Motor fr;

    @Override
    public void init() {

        fr = new Motor(
                hardwareMap.get(DcMotorEx.class, "fr"),
                new MotorConfig(
                        Constants.MotorConstants.GOBILDA_312RPM_5203_2402_0019_TICKS_PER_ROTATION,
                        4
                )
        );
    }

    @Override
    public void loop() {

        // Set front-right motor velocity
        fr.setVelocity(1000);
        // fr.setPower(1.0);

        telemetry.addData("Front Right Velocity", 1000);
        telemetry.update();
    }
}
