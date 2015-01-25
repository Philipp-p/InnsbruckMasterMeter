package org.apptirol.innsbruckmastermeter.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class ManagerTest {

	@Test
	public void testNearest() {
		Manager test = new Manager();
		Point[] meters= test.getNearestMasterMeters(new Point(11.398386, 47.262954));
		Double delta = 0.00000001;
		/*
		for (Points tmp : meters){
			System.out.println(tmp.getX() + " " + tmp.getY());
		} */
		assertEquals(11.3980766980001, meters[0].getX(), delta);
		assertEquals(47.2625801080001, meters[0].getY(), delta);
		assertEquals(11.4034137400001, meters[1].getX(), delta);
		assertEquals(47.2654660420001, meters[1].getY(), delta);
		assertEquals(11.4001522170001, meters[2].getX(), delta);
		assertEquals(47.2570234890001, meters[2].getY(), delta);
	}

}
