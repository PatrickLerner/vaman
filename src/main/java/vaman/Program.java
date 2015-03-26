package vaman;

public class Program {
	public static void main(String[] args) {
		for (String arg : args) {
			CharacterSheet cs = new CharacterSheet(arg);
			System.out.println(cs.toString());
		}
	}
}
