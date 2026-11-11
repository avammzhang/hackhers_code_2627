package Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import launchpad.movement.mecanum.MecanumCoefficientMatrix;
import launchpad.movement.mecanum.MecanumCoefficientSet;
import launchpad.movement.mecanum.MecanumDriver;
import launchpad.hardware.Motor;

@TeleOp
public class BasicTeleop extends OpMode{

    // Drive
    private Motor fl, fr, bl, br;
    // Accessories
    //private Motor intake, sabine, sabine2;
    //sabines are the flywheels

    //private Servo servo1;
    //pusher to push artifact into flywheel
    double countsPerRev = 260.0; //can be changed as per rotation
    double countsPerDegree = countsPerRev / 360.0;



    private DcMotorEx storage;

    //  Encoder constants (tested value-360 degrees= 286(encoder pos))
    private static final double COUNTS_PER_MOTOR_REV = 286.0;
    private static final double COUNTS_PER_DEGREE = COUNTS_PER_MOTOR_REV / 360.0;

    // Preset angles (degrees)
    private static final double[] PRESET_DEGREES = {
            0,
            60,
            120,
            180,
            240,
            300
    };

    private int presetIndex = 0;
    private boolean prevLB = false;

    //safety
    private static final double SERVO_SAFE = 0.0;
    private static final double SERVO_PUSH = 1.0;





    private boolean prevRB = false;
    private boolean prevA  = false;
    private boolean prevB  = false;
    private boolean prevX  = false;
    private boolean prevY  = false;

    // Toggles
    private boolean intakeForward = false;

    private boolean sabineForward = false;
    private boolean sabineReverse = false;
    private boolean servosForward = false;
    private boolean sabineTriggerOn = false;
    private boolean prevRT = false;



    // Mecanum
    private MecanumDriver driver;

    @Override
    public void init() {

        // Drive motors
        fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"));
        fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"));
        bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"));
        br = new Motor(hardwareMap.get(DcMotorEx.class, "br"));




        // Accessories
        //intake  = new Motor(hardwareMap.get(DcMotorEx.class, "intake"));
        //sabine  = new Motor(hardwareMap.get(DcMotorEx.class, "sabine"));
        //sabine2 = new Motor(hardwareMap.get(DcMotorEx.class, "sabine2"));


        //servo setup
        //servo1 = hardwareMap.get(Servo.class, "servo1");
        //servo1.setPosition(0);

        //mecanum setup
        driver = new MecanumDriver(
                fl, fr, bl, br,
                new MecanumCoefficientMatrix(
                        new MecanumCoefficientSet(1, 1, 1, 1),
                        1
                ),
                1
        );
        /*storage = hardwareMap.get(DcMotorEx.class, "storage");


        storage.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        storage.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        */


        // ===== PIDF for RUN_TO_POSITION =====
        // Only P (for now?)
        // PIDFCoefficients positionPIDF =
        //        new PIDFCoefficients(10.0, 0.0, 0.0, 0.0);
//
        //storage.setPIDFCoefficients(
        //        DcMotor.RunMode.RUN_TO_POSITION,
        //        positionPIDF
        //);

        // first preset
        //int startTarget = degreesToTicks(PRESET_DEGREES[presetIndex]);
        // storage.setTargetPosition(startTarget);
        // storage.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // storage.setPower(1.0);




    }

    @Override
    public void loop() {

        // ================= DRIVE =================
        double forward = gamepad1.right_stick_y;   // forward/back
        double strafe  =  -gamepad1.right_stick_x;  // strafe left/right
        double rotate  = -gamepad1.left_stick_x;    // turn

        double flPower = forward + strafe + rotate;
        double frPower = forward - strafe + rotate;
        double blPower = forward - strafe - rotate;
        double brPower = forward + strafe - rotate;

        double max = Math.max(1.0,
                Math.max(Math.abs(flPower),
                        Math.max(Math.abs(frPower),
                                Math.max(Math.abs(blPower), Math.abs(brPower)))));

        double driveVelocity = 10000;
        boolean rtPressed = gamepad1.right_trigger > 0.1;

        if (rtPressed && !prevRT) {
            sabineTriggerOn = !sabineTriggerOn;
        }

        prevRT = rtPressed;
        fl.setVelocity(((flPower / max) * driveVelocity));
        bl.setVelocity(((blPower / max) * driveVelocity));

        fr.setVelocity((frPower / max) * driveVelocity);
        br.setVelocity((brPower / max) * driveVelocity);


        // ================= INTAKE =================
        double intakeVelocity = 900;

        if (gamepad1.a && !prevA) {
            intakeForward = !intakeForward;

        }



        //if (intakeForward) intake.setVelocity(intakeVelocity);
        //else intake.setVelocity(0);


        // ================= SABINE =================
        double sabineVelocity = 1000;
        double sabineVelocityb = 2500;
        /*if (gamepad1.b && !prevB) {
            sabineForward = !sabineForward;
            sabineReverse = false;
        }

        if (gamepad1.x && !prevX) {
            sabineReverse = !sabineReverse;
            sabineForward = false;
        */

        /*
        if (sabineForward) {
            sabine.setVelocity(sabineVelocity);
            sabine2.setVelocity(sabineVelocity);
        } else if (sabineReverse) {
            sabine.setVelocity(-sabineVelocityb);
            sabine2.setVelocity(-sabineVelocityb);
        }
        else if ( sabineTriggerOn) {
            sabine.setVelocity(-2700);
            sabine2.setVelocity(-2700);
        }else {
            sabine.setVelocity(0);
            sabine2.setVelocity(0);
        }
        /*
         */

// ================= STORAGE MANUAL HOLD =================
        double storageVelocity = 500;

        if (gamepad1.y) {
            storage.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            storage.setVelocity(storageVelocity);

        } else {
            storage.setVelocity(0);
        }



        // ======= SERVO =============
        // ======= SERVO (INSTANT PULSE) =======
        /*if (gamepad1.right_bumper && !prevRB) {
            servo1.setPosition(SERVO_PUSH);
        }

        if (!gamepad1.right_bumper && prevRB) {
            servo1.setPosition(SERVO_SAFE);
        }
        */
        prevRB = gamepad1.right_bumper;

        // ============ BUTTON STATE UPDATES =================

        prevRB = gamepad1.right_bumper;
        prevA  = gamepad1.a;
        prevB  = gamepad1.b;
        prevX  = gamepad1.x;
        prevY  = gamepad1.y;
        if (gamepad1.left_bumper && !prevLB) {
            presetIndex = (presetIndex + 1) % PRESET_DEGREES.length;

            int targetTicks = degreesToTicks(PRESET_DEGREES[presetIndex]);

            storage.setTargetPosition(targetTicks);
            storage.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            storage.setPower(1.0);
        }

        prevLB = gamepad1.left_bumper;

        // ===== Telemetry =====
        telemetry.addData("Preset Index", presetIndex);
        telemetry.addData("Target Degrees", PRESET_DEGREES[presetIndex]);
        telemetry.addData("Target Ticks", storage.getTargetPosition());
        telemetry.addData("Current Ticks", storage.getCurrentPosition());
        telemetry.addData("Motor Busy", storage.isBusy());
        telemetry.addData("Sabine Target Vel", sabineVelocity);
        //telemetry.addData("Sabine Actual Vel", sabine.getVelocity());
        //telemetry.addData("Sabine2 Actual Vel", sabine2.getVelocity());

        telemetry.update();
    }
    private int degreesToTicks(double degrees) {
        return (int) Math.round(degrees * COUNTS_PER_DEGREE);
    }

}
