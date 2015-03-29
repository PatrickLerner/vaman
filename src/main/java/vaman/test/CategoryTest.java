package vaman.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import vaman.AGrouping;
import vaman.Category;
import vaman.PropertyGroup;

public class CategoryTest {

	@Test
	public void test() {
		HashMap<String, Integer> phy = new HashMap<String, Integer>();
		phy.put("run", 4);
		phy.put("jump", 2);
		PropertyGroup phy_pg = new PropertyGroup(phy, 3);
		phy_pg.setName("phy");
		
		HashMap<String, Integer> soc = new HashMap<String, Integer>();
		soc.put("talk", 3);
		soc.put("yell", 3);
		PropertyGroup soc_pg = new PropertyGroup(soc, 3);
		soc_pg.setName("soc");
		
		List<AGrouping> pgs = new LinkedList<AGrouping>();
		pgs.add(phy_pg);
		pgs.add(soc_pg);
		
		Category att = new Category();
		att.addSlaves(pgs);
		att.setXpCostInitial(3);
		att.setXpCostNext(2);
		
		assertEquals(20, att.getCheapestXPCost());
	}
	
	@Test
	public void testBigSpread() {
		HashMap<String, Integer> phy = new HashMap<String, Integer>();
		phy.put("strength", 1);
		phy.put("dexterity", 6);
		phy.put("stamina", 3);
		PropertyGroup phy_pg = new PropertyGroup(phy);
		phy_pg.setName("phy");
		
		HashMap<String, Integer> soc = new HashMap<String, Integer>();
		soc.put("charisma", 1);
		soc.put("manipulation", 4);
		soc.put("appearance", 1);
		PropertyGroup soc_pg = new PropertyGroup(soc);
		soc_pg.setName("soc");
	
		HashMap<String, Integer> men = new HashMap<String, Integer>();
		men.put("perception", 5);
		men.put("intelligence", 1);
		men.put("wits", 2);
		PropertyGroup men_pg = new PropertyGroup(men);
		men_pg.setName("men");
		
		List<AGrouping> pgs = new LinkedList<AGrouping>();
		pgs.add(phy_pg);
		pgs.add(soc_pg);
		pgs.add(men_pg);
		
		Category att = new Category();
		att.addSlaves(pgs);
		att.setXpCostInitial(-1);
		att.setXpCostNext(4);
		att.setInitialValue(1);
		att.setName("att");
		List<Integer> freePointsList = new LinkedList<Integer>();
		freePointsList.add(3);
		freePointsList.add(5);
		freePointsList.add(7);
		att.setFreePointsList(freePointsList);
		
		assertEquals(0, att.getCheapestXPCost());
	}
}
