package opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import launchpad.TeleopBase;
import launchpad.actions.SleepAction;
import launchpad.geometry.MovementVector;
import launchpad.hardware.Motor;
import launchpad.movement.mecanum.MecanumCoefficientMatrix;
import launchpad.movement.mecanum.MecanumCoefficientSet;
import launchpad.movement.mecanum.MecanumDriver;

    @TeleOp(name="Example Teleop")
    public class ExampleTeleop extends TeleopBase {

        private MecanumDriver driver;

        private DcMotorEx fl, fr, bl, br, in, fwr, fwl;
        //private Motor in;
        //private Motor fwr;
        //private Motor fwl;
        @Override
        public void init() {
            super.init();

            // Initialize motors
            fl = hardwareMap.get(DcMotorEx.class, "fl");
            fr = hardwareMap.get(DcMotorEx.class, "fr");
            bl = hardwareMap.get(DcMotorEx.class, "bl");
            br = hardwareMap.get(DcMotorEx.class, "br");
            in = hardwareMap.get(DcMotorEx.class, "in");
            fwr = hardwareMap.get(DcMotorEx.class, "fwr");
            fwl = hardwareMap.get(DcMotorEx.class, "fwl");


            // encoders
            fl.setDirection(DcMotorEx.Direction.REVERSE);
            fr.setDirection(DcMotorEx.Direction.FORWARD);
            bl.setDirection(DcMotorEx.Direction.REVERSE);
            br.setDirection(DcMotorEx.Direction.FORWARD);
            in.setDirection(DcMotorEx.Direction.FORWARD);
            fwr.setDirection(DcMotorEx.Direction.FORWARD);
            fwl.setDirection(DcMotorEx.Direction.REVERSE);

            fl.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            fr.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            bl.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            br.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            in.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            fwr.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            fwl.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);


        }

        public void driveOmni(double y, double rx, double x) {
            final double MTPS = 4661;
            double maxValue = Math.max(Math.abs(x) * Math.abs(y)+Math.abs(rx), 1);

            // math stuff
            double flwPower = (y + x + rx) / maxValue;
            double blwPower = (y - x + rx) / maxValue;
            double frwPower = (y - x - rx) / maxValue;
            double brwPower = (y + x - rx) / maxValue;

            fl.setPower(flwPower * MTPS);
            fr.setPower(frwPower * MTPS);
            bl.setPower(blwPower * MTPS);
            br.setPower(brwPower * MTPS);
        }

        @Override
        protected void registerSubsystems() {
            // Register a subsystem, such as a claw, a turret, an intake, or a storage mechanism
            // robot.register(new SubsystemName());
        }

        @Override
        public void loop() {

            //determines the inputs of the gamepad
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            //flywheels
            /*if(gamepad1.b) { //start the flywheel
                if(gamepad1.x) { //change power to a smaller value for nectar
                    // in.setPower(0.05);
                    fwl.setPower(0.1);
                    fwr.setPower(0.1);
                } else if(gamepad1.y) {
                    fwl.setPower(1); //change power back (y)
                    fwr.setPower(1);
                } else {
                    fwl.setPower(1); //if x not pressed, keep the power to a higher one
                    fwr.setPower(1);
                }
            } else{
                fwl.setPower(0);
                fwr.setPower(0);
            }
            */
            if(gamepad1.b) {
                fwl.setVelocity(-1400);
                fwr.setPower(1400);
            } else if (gamepad1.x) {
                fwl.setPower(-1400);
                fwr.setPower(1400);
            } else{
                fwl.setPower(0);
                fwr.setPower(0);
            }
            //intake
            if (gamepad1.a) {
                in.setPower(1400);
            } else {
                in.setPower(0);
            }
            driveOmni(y, rx, x);
        }
}