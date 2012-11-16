package inrs.goniophotometer.motion;

import org.junit.Test;

import inrs.goniophotometer.motion.XliControlled.XliControlledMotionEngine;
import junit.framework.TestCase;

public class TestXliControlledMotionengine extends TestCase {
	
	public static void waitTestTime(int milli_, String following_stage){
		try {
			Thread.sleep(milli_);
		} catch (InterruptedException _e) {
			System.out.println("Cannot wait");
		}
		System.out.println("Suite : " + following_stage);
	}
	
	@Test	
	public void testMotion(){
		
		XliControlledMotionEngine _engine = new XliControlledMotionEngine("COM1", 4000, 500);
		
		waitTestTime(100, "Setting low max speed");
		_engine.setAngularMaxVelocity(0.5f);
		
		waitTestTime(1000, "launching forward motion");
		_engine.processRelativeMove(10.0f);
		
		waitTestTime(200, "waiting end of forward motion");
		_engine.waitForEndOfMotion();
		
		waitTestTime(500, "Setting high max speed");
		_engine.setAngularMaxVelocity(2f);
		
		waitTestTime(500, "launching backward motion");
		_engine.processRelativeMove(-10f);
		
		waitTestTime(100,  "waiting end of backward motion");
		_engine.waitForEndOfMotion();
		
		System.out.println("End");
		System.exit(0);
	}


	
}
