package vaman.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
	public void testValueDependency() {
		HashMap<String, Integer> test = new HashMap<String, Integer>();
		test.put("run", 3);
		test.put("jump", 2);
		PropertyGroup pg = new PropertyGroup(test, 4, 3, 2);
		pg.setName("pg");
		
		HashMap<String, Integer> test2 = new HashMap<String, Integer>();
		test2.put("phy", 4);
		PropertyGroup pg2 = new PropertyGroup(test2, 0, 10, 10);
		
		List<String> list = new LinkedList<String>();
		list.add("jump");
		pg2.setInitialValueDependencyGrouping(pg);
		pg2.setInitialValueDependencies(list);
		pg2.setName("pg2");
		pg.addSlave(pg2);
		
		assertEquals(54, pg.getCheapestXPCost());
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
