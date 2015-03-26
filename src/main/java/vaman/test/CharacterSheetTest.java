package vaman.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import vaman.CharacterSheet;

public class CharacterSheetTest {
	@Test
	public void testSimple() {
		CharacterSheet cs = new CharacterSheet("src/test/resources/stina.json");
		int total = cs.getXPTotal();
		System.out.println(cs.toString());
		assertEquals(0, total);	
	}
}
