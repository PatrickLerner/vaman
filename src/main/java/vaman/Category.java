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

	private Set<List<Integer>> getSpreadPermutations(List<Integer> freePoints) {
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		for (Integer i : freePoints) {
			List<Integer> list = new LinkedList<Integer>();
			list.add(i);
			result.add(list);
		}
		for (int c = 1; c < freePoints.size(); c++) {
			Set<List<Integer>> newResult = new HashSet<List<Integer>>();
			for (Integer i : freePoints) {
				for (List<Integer> list : result) {
					if (!list.contains(i)) {
						List<Integer> newList = new LinkedList<Integer>(list);
						newList.add(i);
						newResult.add(newList);
					}
				}
			}
			result = newResult;
		}
		return result;
	}

	private Integer calculatePermutationCost(List<Integer> permutation) {
		int sum = 0;
		int i = 0;
		for (AGrouping group : this.getSlaves()) {
			if (group.getTotalDots() < permutation.get(i) + group.getFreeInitialPoints() + group.getFreeFreebiePoints())
				return null;
			group.setFreeListPoints(permutation.get(i));
			sum += group.getCheapestXPCost();
			i += 1;
		}
		return sum;
	}

	protected void recalculateXPCost() {
		if (this.getFreePointsList() != null && this.getFreePointsList().size() > 0) {
			Set<List<Integer>> permutations = this.getSpreadPermutations(this.getFreePointsList());

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
				throw new RuntimeException("Could not find any way to spread out the free points to the groups in "
						+ this.getName());

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

		this.setRecalculationNecessary(false);
	}

	public String toString() {
		if (this.isRecalculationNecessary()) {
			this.recalculateXPCost();
			this.setRecalculationNecessary(false);
		}

		StringBuilder sb = new StringBuilder();
		for (AGrouping group : this.getSlaves())
			sb.append(group.toString());
		return sb.toString();
	}
}
