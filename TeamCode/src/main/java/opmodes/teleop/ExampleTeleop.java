package opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

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

        private Motor fl, fr, bl, br;
        private Motor in;
        private Motor fwr;
        private Motor fwl;
        @Override
        public void init() {
            super.init();

            // Initialize motors
            fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"));
            fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"));
            bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"));
            br = new Motor(hardwareMap.get(DcMotorEx.class, "br"));
            in = new Motor(hardwareMap.get(DcMotorEx.class, "in"));
            fwl = new Motor(hardwareMap.get(DcMotorEx.class, "fwl"));
            fwr = new Motor(hardwareMap.get(DcMotorEx.class, "fwr"));


            // Create a mecanum driver
            // change coefficients later (make sure wheels go right way)
            driver = new MecanumDriver(fl, fr, bl, br, new MecanumCoefficientMatrix(new MecanumCoefficientSet(1, 1, 1, 1)));

            // Make a button do something
            mainGamepad.aButton.onPress(() -> {
                // add an action to the action thread, causing it to happen immediately
                runningActions.addAndInitialize(new SleepAction(1000));
            });

            // Hold to run something continuously (intake, shooter, etc.)
            mainGamepad.rightBumper.whileDown(() -> { /* ... */ });

            // Toggle on/off with each press — good for a claw or a latch
            mainGamepad.xButton.onToggle(open -> { /* ... */ });

            // Analog trigger value, 0.0 to 1.0 — variable speed control
            // intake.setPower(mainGamepad.rightTrigger.getValue());

            // Second driver's controller
            secondaryGamepad.dpadUp.onPress(() -> { /* ... */ });
        }

        public void driveOmni(double y, double rx, double x) {
            final double MTPS = 537.7;
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
            // Automatically loops all registered subclasses
            super.loop();

            // Loop all localization classes, etc, here


            // Drive using controller (Note: in the future, add encoder wires to each drive motor, and change this to driver.setRelativeVelocity())
            driver.setRelativePower(new MovementVector(mainGamepad.leftJoystick.getY(), mainGamepad.leftJoystick.getX(), mainGamepad.rightJoystick.getX()));

            //determines the inputs of the gamepad
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // driveOmni(y, rx, x);

            if (gamepad1.a) {
                in.setPower(2);
            } else {
                in.setPower(0);
            }
            if (gamepad1.b){
                fwl.setPower(1);
                fwr.setPower(1);
            } else{
                fwl.setPower(0);
                fwr.setPower(0);
            }

        }
}