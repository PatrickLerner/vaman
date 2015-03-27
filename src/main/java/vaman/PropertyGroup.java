package vaman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/*
 * A property group is a collection of properties (e.g. one of the ability or attribute groups) and their
 * respective ratings.
 */
public class PropertyGroup extends AGrouping {
	private Map<String, Integer> properties;
	private Map<Integer, Map<String, Integer>> freePointSpending;

	public PropertyGroup(Map<String, Integer> properties, Integer freePoints, Integer xpCostInitial,
			Integer xpCostNext, Integer initialValue) {
		this.properties = properties;
		this.setFreeInitialPoints(freePoints);
		this.setXpCostInitial(xpCostInitial);
		this.setXpCostNext(xpCostNext);
		this.setInitialValue(initialValue);
		this.freePointSpending = new HashMap<Integer, Map<String, Integer>>();
	}

	public PropertyGroup(Map<String, Integer> properties, Integer freePoints, Integer xpCostInitial, Integer xpCostNext) {
		this(properties, freePoints, xpCostInitial, xpCostNext, null);
	}

	public PropertyGroup(Map<String, Integer> properties, Integer freePoints) {
		this(properties, freePoints, null, null, null);
	}

	public PropertyGroup(Map<String, Integer> properties) {
		this(properties, null, null, null, null);
	}

	@Override
	public int getTotalDots() {
		int sum = 0;
		for (String property : this.properties.keySet()) {
			sum += this.properties.get(property);
			sum -= this.getInitialValue();
		}
		return sum;
	}

	private int calculateRemainingXPCost(Map<String, Integer> permutation) {
		int cost = 0;
		for (String property : permutation.keySet()) {
			for (int i = permutation.get(property); i < this.properties.get(property); i++) {
				int price = 0;
				if (i == 0)
					price = this.getXpCostInitial();
				else
					price = this.getXpCostNext() * i;
				cost += price;
				if (cost < 0)
					throw new RuntimeException("Property " + this.getName() + " cannot be raised by experience points.");
			}
		}
		return cost;
	}

	@Override
	public int getCheapestXPCostSpread(Integer additionalFreePoints) {
		this.setFreeListPoints(additionalFreePoints);

		//if (this.freePointSpending.containsKey(this.getTotalFreePoints()))
		//	return this.calculateRemainingXPCost(this.freePointSpending.get(this.getTotalFreePoints()));

		Map<String, Integer> cheapestPermutation = null;
		int cheapestCost = 0;
		for (Map<String, Integer> permutation : this.getFreePointPermutations()) {
			int currentCost = this.calculateRemainingXPCost(permutation);
			if (currentCost < cheapestCost || cheapestPermutation == null) {
				cheapestCost = currentCost;
				cheapestPermutation = permutation;
			}
		}

		this.freePointSpending.put(this.getTotalFreePoints(), cheapestPermutation);
		
		return cheapestCost;
	}

	public Map<String, Integer> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Integer> properties) {
		this.properties = properties;
	}

	private Set<Map<String, Integer>> getFreePointPermutations() {
		// create a base permutation, which is a map with all properties
		// initialized to their
		// start value (usually 0)
		HashMap<String, Integer> basePermutation = new HashMap<String, Integer>();
		for (String property : this.properties.keySet())
			basePermutation.put(property, this.getInitialValue());

		// Create a list of initial permutations, which is just the base one
		// initially
		Set<Map<String, Integer>> basePermutationList = new HashSet<Map<String, Integer>>();
		basePermutationList.add(basePermutation);

		// return all the possible ways to spend the free dots
		Set<Map<String, Integer>> poss = this.spendFreePoints(basePermutationList, this.getTotalFreePoints());
		
		// now filter out those that spend free initial points instead of freebie points
		// above the limit if it exists
		if (this.getMaxWithoutFreebie() > 0) {
			Set<Map<String, Integer>> illegalPoss = new HashSet<Map<String, Integer>>();
			
			// find illegal possibilities
			for (Map<String, Integer> p : poss) {
				int sumAboveLimit = 0;
				// count number of free dots assigned above limit
				for (Integer i : p.values())
					if (i > this.getMaxWithoutFreebie())
						sumAboveLimit += i - this.getMaxWithoutFreebie();
				// if too many are assigned above limit it's an illegal permutation
				if (sumAboveLimit > this.getFreeFreebiePoints())
					illegalPoss.add(p);
			}
			
			// remove illegal possibilities
			for (Map<String, Integer> ip : illegalPoss)
				poss.remove(ip);
		}
		
		return poss;
	}

	private Set<Map<String, Integer>> spendFreePoints(Set<Map<String, Integer>> permutations, int freePoints) {
		// if no free points are left to spend, we are done here
		if (freePoints <= 0)
			return permutations;

		// create new permutations based of the old ones
		Set<Map<String, Integer>> newPermutations = new HashSet<Map<String, Integer>>();

		// for each existing permutation
		for (Map<String, Integer> permutation : permutations) {
			// find any property that can still be increased
			for (String property : permutation.keySet()) {
				if (permutation.get(property) < this.properties.get(property)) {
					// and add it being increased as a new permutation
					Map<String, Integer> newPermutation = new HashMap<String, Integer>(permutation);
					newPermutation.put(property, permutation.get(property) + 1);
					newPermutations.add(newPermutation);
				}
			}
		}

		// if we have no new permutations, this must mean no property could be
		// increased, which means
		// we have too many free dots or rather too few dots assigned in the
		// character sheet for a category
		if (newPermutations.size() == 0)
			throw new RuntimeException("Free dot in " + this.getName()
					+ " could not be assigned to anything as nothing was left.");

		return spendFreePoints(newPermutations, freePoints - 1);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		int totalXP = 0;
		for (String property : this.properties.keySet()) {
			for (int i = 1; i <= this.properties.get(property); i++) {
				if (this.freePointSpending.containsKey(this.getTotalFreePoints())
						&& this.freePointSpending.get(this.getTotalFreePoints()).get(property) < i) {
					sb.append("\t");
					sb.append(StringUtils.rightPad(property + " " + i, 20));
					sb.append(" ");
					int cost = 0;
					if (i == 1)
						cost = this.getXpCostInitial();
					else
						cost = this.getXpCostNext() * (i - 1);
					totalXP += cost;
					sb.append(cost);
					sb.append(" xp\n");
				}
			}
		}
		if (sb.length() > 0)
			sb.insert(0, this.getName() + " (" + totalXP + " xp)\n");
		return sb.toString();
	}
}
