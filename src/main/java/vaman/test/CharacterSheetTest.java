package vaman.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import vaman.CharacterSheet;

public class CharacterSheetTest {
	@Test
	public void testStina() {
		CharacterSheet cs = new CharacterSheet("src/test/resources/stina.json");
		assertEquals(0, cs.getCheapestXPCost());
	}

	@Test
	public void testStinaFourDot() {
		CharacterSheet cs = new CharacterSheet("src/test/resources/stina_4dot.json");
		assertEquals(6, cs.getCheapestXPCost());
	}
	
	@Test
	public void testStinaVirtueIncreased() {
		CharacterSheet cs = new CharacterSheet("src/test/resources/stinaMoreCouragous.json");
		System.out.println(cs.toString());
		assertEquals(9, cs.getCheapestXPCost());
	}
}
