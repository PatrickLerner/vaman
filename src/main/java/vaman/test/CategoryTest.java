package vaman.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

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
		
		HashMap<String, Integer> soc = new HashMap<String, Integer>();
		soc.put("talk", 3);
		soc.put("yell", 3);
		PropertyGroup soc_pg = new PropertyGroup(soc, 3);
		
		Map<String, AGrouping> pgs = new HashMap<String, AGrouping>();
		pgs.put("phy", phy_pg);
		pgs.put("soc", soc_pg);
		
		Category att = new Category(pgs);
		att.setXpCostInitial(3);
		att.setXpCostNext(2);
		
		assertEquals(20, att.getCheapestXPCostSpread());
	}

}
