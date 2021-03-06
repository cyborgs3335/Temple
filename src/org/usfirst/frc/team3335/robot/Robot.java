package org.usfirst.frc.team3335.robot;

/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*                                                                            */
/*                    Written by The Lost King and his 25 slaves              */
/*----------------------------------------------------------------------------*/

//import com.ni.vision.NIVision;
//import com.ni.vision.NIVision.Image;
//import com.ni.vision.NIVision.ImageType;
//import edu.java.ArtificialIntelligence;
//import edu.hell.SoulsOfTheDamned;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.Joystick.RumbleType;
//import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private static final double maxSpeed = 0.6;
	//private static final double ultraInchesPerVolt = 106.1391; // Circuit board
																// = 0 in.

	// Ultrasonic range is 7 in. to 76 in.

	private double autoTimeStart;
	private Compressor compressor;
	private Relay relay;
	private Solenoid solenoidOut;
	private Solenoid solenoidIn;
	private Joystick controller;
	private RobotDrive myRobot;
	private SpeedController mcBackLeft, mcFrontLeft, mcBackRight, mcFrontRight,
			mcLift1, mcLift2;
	//private BuiltInAccelerometer accel;
	//private Gyro gyro;
	// private CameraServer server;
	//private Image image;
	//private int niSession;

	private DigitalInput limit, limit2;
	//private AnalogInput ultrasonic;
	//private AnalogInput gyroTest;
	//private AnalogInput pressure;

	private boolean hasExtended;
	//private boolean useGyro;

	double Kp = 0.03;

	public void robotInit() {
		joystickInit();
		startCompressor();
		sensorInit();
		speedControllerInit();
		robotDriveInit();
		// cameraInit();
		//accel = new BuiltInAccelerometer();
		autoTimeStart = 0;
	}

	public void joystickInit() {
		controller = new Joystick(0);
	}

	public void sensorInit() {
		limit = new DigitalInput(0);
		limit2 = new DigitalInput(1);
		//ultrasonic = new AnalogInput(2);
		//gyro = new Gyro(new AnalogInput(0));
		//gyroTest = new AnalogInput(1);
		//pressure = new AnalogInput(3);

		//useGyro = true; // Manually set this variable to true to use gyro.
	}

	public void speedControllerInit() {
		mcBackRight = new Victor(0);
		mcFrontRight = new Victor(1);
		mcBackLeft = new Victor(2);
		mcFrontLeft = new Victor(3);
		mcLift1 = new Talon(4);
		mcLift2 = new Talon(5);
	}

	public void robotDriveInit() {
		myRobot = new RobotDrive(mcBackLeft, mcFrontLeft, mcBackRight,
				mcFrontRight);
	}

	public void startCompressor() {
		solenoidIn = new Solenoid(0);
		solenoidOut = new Solenoid(1); // Pneumatics
		relay = new Relay(0);
		relay.set(Relay.Value.kOff);
		compressor = new Compressor(2);
	}

	 /*public void cameraInit() {
		 server = CameraServer.getInstance();
	server.setQuality(50);
	 image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
	 niSession = NIVision.IMAQdxOpenCamera("cam0",
	 NIVision.IMAQdxCameraControlMode.CameraControlModeController);
	 NIVision.IMAQdxConfigureGrab(niSession);
	 }*/
	public void autonomousInit() {
		autoTimeStart = Timer.getFPGATimestamp();
		//gyro.reset();
	}

	public void autonomousPeriodic() {
		//double angle = gyro.getAngle();
		double autoTimeCurr = Timer.getFPGATimestamp() - autoTimeStart;
		if (autoTimeCurr <= 0.5) {
			solenoidIn.set(true);
		} else if (autoTimeCurr <= 2) {
			mcLift1.set(0.7);
		} else if (autoTimeCurr <= 4) {
			//myRobot.drive(-1.0, -angle * Kp);
		}

		SmartDashboard.putString("DB/String 1",
				String.format("%.3f", autoTimeCurr));

		// SUPER DUPER AUTONOMOUS
	}

	public void teleopInit() {
		 //NIVision.IMAQdxStartAcquisition(niSession);
		//gyro.reset();
	}

	public void teleopPeriodic() {
		if (controller.getRawButton(9)) {
			controller.setRumble(RumbleType.kLeftRumble, 1);
			controller.setRumble(RumbleType.kRightRumble, 1);
			//gyro.reset();
		} else {
			controller.setRumble(RumbleType.kLeftRumble, 0);
			controller.setRumble(RumbleType.kRightRumble, 0);
		}
		// NIVision.IMAQdxGrab(niSession, image, 1);
		// server.setImage(image);

		double x = getDeadZone(controller.getAxis(Joystick.AxisType.kX), 0.15);
		double y = getDeadZone(controller.getAxis(Joystick.AxisType.kY), 0.15);
		double r = getDeadZone(controller.getRawAxis(4) * 1, 0.15);

		 mcFrontLeft.set(normalize(x - y + r) * maxSpeed);
		 mcBackRight.set(normalize(-x + y + r) * maxSpeed);
		 mcBackLeft.set(normalize(-x - y + r) * maxSpeed);
		 mcFrontRight.set(normalize(x + y + r) * maxSpeed);

		//double joystickAngle = Math.toDegrees(-Math.atan2(-y, x)) + 90;
		//joystickAngle = (joystickAngle % 360 + 360) % 360;

		//if (useGyro) {
			//mcFrontLeft.set(-normalize(Math.hypot(-y, x)
				//	* joystickFunction(joystickAngle - gyro.getAngle()) + r)
					//* maxSpeed);
			//mcFrontRight.set(-normalize(Math.hypot(-y, x)
					//* joystickFunction(-joystickAngle + gyro.getAngle()) - r)
					//* maxSpeed);
			//mcBackLeft.set(normalize(Math.hypot(-y, x)
					//* joystickFunction(-joystickAngle + gyro.getAngle()) + r)
					//* maxSpeed);
			//mcBackRight.set(-normalize(Math.hypot(-y, x)
					//* joystickFunction(joystickAngle - gyro.getAngle()) - r)
					//* maxSpeed);
		//} else {
			//mcFrontLeft.set(-normalize(Math.hypot(-y, x)
				//* joystickFunction(joystickAngle) + r)
					//* maxSpeed);
			//mcFrontRight.set(-normalize(Math.hypot(-y, x)
					//* joystickFunction(-joystickAngle) - r)
				//	* maxSpeed);
			//mcBackLeft.set(normalize(Math.hypot(-y, x)
					//* joystickFunction(-joystickAngle) + r)
				//	* maxSpeed);
			//mcBackRight.set(-normalize(Math.hypot(-y, x)
				//	* joystickFunction(joystickAngle) - r)
			//		* maxSpeed);
		//}

		//if (pressure.getVoltage() < 3) {
			//relay.set(Relay.Value.kReverse);
		//} else {
			//relay.set(Relay.Value.kOff);
		//}

		if (controller.getRawButton(8)) {
			relay.set(Relay.Value.kReverse);
		} else if (controller.getRawButton(7)) {
			relay.set(Relay.Value.kOff);
		}

		// Pneumatics:
		if (controller.getRawButton(2) && hasExtended) {
			solenoidOut.set(true);
			solenoidIn.set(false);

			hasExtended = false;
		} else if (controller.getRawButton(1) && !hasExtended) {
			solenoidOut.set(false);
			solenoidIn.set(true);

			hasExtended = true;
		} else {
			solenoidOut.set(false);
			solenoidIn.set(false);
		}

		if (controller.getRawButton(5) && controller.getRawButton(6)) {
			mcLift1.set(0);
			mcLift2.set(0);
		} else if (controller.getRawButton(6) && limit.get() == false) {
			relay.set(Relay.Value.kOff);
			mcLift1.set(0.7);
			mcLift2.set(0.7);
		} else if (controller.getRawButton(5) && limit2.get() == false) {
			relay.set(Relay.Value.kOff);
			mcLift1.set(-0.4);
			mcLift2.set(-0.4);
		} else {
			if (controller.getRawButton(8)) {
				relay.set(Relay.Value.kReverse);
			} else if (controller.getRawButton(7)) {
				relay.set(Relay.Value.kOff);
			}
			mcLift1.set(0);
			mcLift2.set(0);
			
		}
		//SmartDashboard
			//	.putString(
				//		"DB/String 0",
					//	String.format("Ultrasonic Volt: %.4f",
				//				ultrasonic.getVoltage()));
		SmartDashboard.putString("DB/String 1",
				String.format("D-Pad: %d", controller.getPOV()));
		SmartDashboard.putString("DB/String 2",
				String.format("Axis X: %.4f", controller.getAxis(AxisType.kX)));
		SmartDashboard.putString("DB/String 3",
				String.format("Axis Y: %.4f", controller.getAxis(AxisType.kY)));
		SmartDashboard.putString("DB/String 4",
				String.format("Turn Axis: %.4f", controller.getRawAxis(4)));
		//SmartDashboard.putString(
			//	"DB/String 5",
				//String.format("Distance: %.4f", ultrasonic.getVoltage()
					//	* ultraInchesPerVolt));
//		SmartDashboard.putString("DB/String 6",
	//			String.format("Gyro: %.4f", gyro.getAngle()));
		//SmartDashboard.putString("DB/String 7",
			//	String.format("Gyro Rate: %.4f", gyro.getRate()));
	//	SmartDashboard.putString("DB/String 8",
		//		String.format("Gyro Test: %.4f", gyroTest.getVoltage()));
		//SmartDashboard.putString("DB/String 9", 
			//	String.format("Pressure: %.4f", pressure.getVoltage()));
	//SmartDashboard.putString("DB/String 8",
		//		String.format("%.3f", joystickAngle));
	}

	//
	public void disabledInit() {
		// NIVision.IMAQdxStopAcquisition(niSession);
		SmartDashboard.putString("DB/String 0", "");
		SmartDashboard.putString("DB/String 1", "");
		SmartDashboard.putString("DB/String 2", "");
		SmartDashboard.putString("DB/String 3", "");
		SmartDashboard.putString("DB/String 4", "");
		SmartDashboard.putString("DB/String 5", "");
		SmartDashboard.putString("DB/String 6", "");
		SmartDashboard.putString("DB/String 7", "");
		SmartDashboard.putString("DB/String 8", "");
		SmartDashboard.putString("DB/String 9", "");
		solenoidIn.set(false);
		solenoidOut.set(false);
	}

	public void testPeriodic() {

		SmartDashboard.putString("DB/String 2",
				String.format("Axis X: %.4f", controller.getAxis(AxisType.kX)));
		SmartDashboard.putString("DB/String 3",
				String.format("Axis Y: %.4f", controller.getAxis(AxisType.kY)));
		SmartDashboard.putString("DB/String 4",
				String.format("Turn Axis: %.4f", controller.getRawAxis(4)));

		double x = getDeadZone(controller.getAxis(Joystick.AxisType.kX), 0.15);
		double y = getDeadZone(controller.getAxis(Joystick.AxisType.kY), 0.15);

		double joystickAngle = Math.toDegrees(-Math.atan2(-y, x)) + 90;
		SmartDashboard.putString("DB/String 8",
				String.format("%.3f", joystickAngle));
	}

	public double getDeadZone(double axis, double zone) {
		return Math.abs(axis) > zone ? axis : 0;
	}

	public double joystickFunction(double angle) {
		angle = (angle % 360 + 360) % 360;
		if (0 <= angle && angle < 90)
			return 1;
		else if (90 <= angle && angle < 180)
			return -Math.cos(Math.toRadians(2 * angle));
		else if (180 <= angle && angle < 270)
			return -1;
		else
			return Math.cos(Math.toRadians(2 * angle));
	}

	public double normalize(double value) {
		return Math.min(1, Math.max(-1, value));
	}
}
