package vaman.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import org.junit.Test;

import vaman.PropertyGroup;

public class PropertyGroupTest {
	@Test
	public void testSimple() {
		HashMap<String, Integer> test = new HashMap<String, Integer>();
		test.put("run", 4);
		test.put("jump", 2);
		PropertyGroup pg = new PropertyGroup(test, 3, 3, 2);
		assertEquals(11, pg.getCheapestXPCost());
	}
	
	@Test
	public void testSimpleWithHighInitalValue() {
		HashMap<String, Integer> test = new HashMap<String, Integer>();
		test.put("run", 4);
		test.put("jump", 2);
		PropertyGroup pg = new PropertyGroup(test, 3, 10, 2);
		assertEquals(12, pg.getCheapestXPCost());
	}

	@Test
	public void testSimpleFreeInitalDot() {
		HashMap<String, Integer> test = new HashMap<String, Integer>();
		test.put("run", 4);
		test.put("jump", 2);
		PropertyGroup pg = new PropertyGroup(test, 3, 10, 2, 1);
		assertEquals(2, pg.getCheapestXPCost());
	}
	
	@Test
	public void testSimpleFourthDot() {
		HashMap<String, Integer> test = new HashMap<String, Integer>();
		test.put("run", 4);
		test.put("jump", 2);
		PropertyGroup pg = new PropertyGroup(test, 4, 3, 2);
		pg.setMaxWithoutFreebie(3);
		assertEquals(8, pg.getCheapestXPCost());
	}
	
	@Test
	public void testComplexFourthDot() {
		HashMap<String, Integer> test = new HashMap<String, Integer>();
		test.put("academics", 4);
		test.put("investigation", 4);
		test.put("medicine", 1);
		test.put("occult", 1);
		test.put("politics", 1);
		PropertyGroup pg = new PropertyGroup(test, 9, 3, 2);
		pg.setMaxWithoutFreebie(3);
		pg.setFreeFreebiePoints(1);
		assertEquals(6, pg.getCheapestXPCost());
	}
}
