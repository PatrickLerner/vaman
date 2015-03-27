package vaman;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Category extends AGrouping {
	public Category() {
	}

	@Override
	public int getTotalDots() {
		int sum = 0;
		for (AGrouping group : this.getSlaves())
			sum += group.getTotalDots();
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
		for (AGrouping group : this.getSlaves()) {
			if (group.getTotalDots() < permutation.get(i) + group.getFreeInitialPoints()
					+ group.getFreeFreebiePoints())
				return null;
			group.setFreeListPoints(permutation.get(i));
			sum += group.getCheapestXPCost();
			i += 1;
		}
		return sum;
	}
	
	protected void recalculateXPCost() {
		if (this.getFreePointsList() != null && this.getFreePointsList().size() > 0) {
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

			this.calculatePermutationCost(cheapestPermutation);
			
			this.setCheapestXPCost(cheapestCost);
		} else {
			int sum = 0;
			for (AGrouping group : this.getSlaves()) {
				group.setFreeListPoints(0);
				sum += group.getCheapestXPCost();
			}
			
			this.setCheapestXPCost(sum);
		}
	}

	public String toString() {
		this.recalculateXPCost();
		
		StringBuilder sb = new StringBuilder();
		for (AGrouping group : this.getSlaves())
			sb.append(group.toString());
		return sb.toString();
	}
}
