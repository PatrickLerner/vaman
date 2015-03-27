package vaman;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Category extends AGrouping {
	private Map<String, AGrouping> groups;
	//private Map<Integer, List<Integer>> freePointSpending;

	public Category(Map<String, AGrouping> groups) {
		this.groups = groups;
		for (Entry<String, AGrouping> group : groups.entrySet())
			group.getValue().setCategory(this);
		//this.freePointSpending = new HashMap<Integer, List<Integer>>();
	}

	public Map<String, AGrouping> getGroups() {
		return this.groups;
	}

	@Override
	public int getTotalDots() {
		int sum = 0;
		for (Entry<String, AGrouping> group : this.groups.entrySet())
			sum += group.getValue().getTotalDots();
		return sum;
	}

	private Set<List<Integer>> getSpreadPermutations(Set<List<Integer>> permutations, List<Integer> freePoints) {
		Set<List<Integer>> newPermutations = new HashSet<List<Integer>>();
		for (List<Integer> permutation : permutations) {
			for (Integer val : freePoints) {
				if (!permutation.contains(val)) {
					permutation.add(val);
					newPermutations.add(permutation);
				}
			}
		}
		if (newPermutations.size() == 0)
			return permutations;
		else
			return getSpreadPermutations(newPermutations, freePoints);
	}

	private Integer calculatePermutationCost(List<Integer> permutation) {
		int sum = 0;
		int i = 0;
		for (Entry<String, AGrouping> group : this.groups.entrySet()) {
			if (group.getValue().getTotalDots() < permutation.get(i) + group.getValue().getFreeInitialPoints()
					+ group.getValue().getFreeFreebiePoints())
				return null;
			sum += group.getValue().getCheapestXPCostSpread(permutation.get(i));
			i += 1;
		}
		return sum;
	}

	@Override
	public int getCheapestXPCostSpread(Integer additionalFreePoints) {
		this.setFreeListPoints(additionalFreePoints);

		if (this.getFreePointsList() != null && this.getFreePointsList().size() > 0) {
			/*
			if (this.freePointSpending.containsKey(this.getTotalFreePoints())) {
				System.out.print(this.getName() + " ");
				System.out.println(this.freePointSpending.get(this.getTotalFreePoints()));
				return this.calculatePermutationCost(this.freePointSpending.get(this.getTotalFreePoints()));
			}
			*/

			Set<List<Integer>> initialSet = new HashSet<List<Integer>>();
			for (Integer val : this.getFreePointsList()) {
				List<Integer> list = new LinkedList<Integer>();
				list.add(val);
				initialSet.add(list);
			}

			Set<List<Integer>> permutations = this.getSpreadPermutations(initialSet, this.getFreePointsList());

			List<Integer> cheapestPermutation = null;
			int cheapestCost = 0; 
			for (List<Integer> permutation : permutations) {
				Integer currentCost = this.calculatePermutationCost(permutation);
				if ((currentCost != null) && (currentCost < cheapestCost || cheapestPermutation == null)) {
					cheapestCost = currentCost;
					cheapestPermutation = permutation;
				}
			}
			if (cheapestPermutation == null)
				throw new RuntimeException("Could not find any way to spread out the free points to the groups");

			//this.freePointSpending.put(this.getTotalFreePoints(), cheapestPermutation);

			this.calculatePermutationCost(cheapestPermutation);
			
			return cheapestCost;
		} else {
			int sum = 0;
			for (Entry<String, AGrouping> group : this.groups.entrySet())
				sum += group.getValue().getCheapestXPCostSpread();
			return sum;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, AGrouping> group : this.groups.entrySet())
			sb.append(group.getValue().toString());
		return sb.toString();
	}
}
