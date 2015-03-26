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
		assertEquals(11, pg.getCheapestXPCostSpread());
	}
	
	@Test
	public void testSimpleWithHighInitalValue() {
		HashMap<String, Integer> test = new HashMap<String, Integer>();
		test.put("run", 4);
		test.put("jump", 2);
		PropertyGroup pg = new PropertyGroup(test, 3, 10, 2);
		assertEquals(12, pg.getCheapestXPCostSpread());
	}

	@Test
	public void testSimpleFreeInitalDot() {
		HashMap<String, Integer> test = new HashMap<String, Integer>();
		test.put("run", 4);
		test.put("jump", 2);
		PropertyGroup pg = new PropertyGroup(test, 3, 10, 2, 1);
		assertEquals(2, pg.getCheapestXPCostSpread());
	}
}
