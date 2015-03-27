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
		
		assertEquals(20, att.getCheapestXPCostSpread());
	}

}
