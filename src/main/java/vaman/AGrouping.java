package vaman;

import java.util.List;

public abstract class AGrouping {
	private Integer freeInitialPoints;
	private Integer freeFreebiePoints;
	private Integer freeListPoints;
	private List<Integer> freePointsList;
	private Integer xpCostNext;
	private Integer xpCostInitial;
	private Integer initialValue;
	private Category category;
	private Integer freebieCost;
	private Integer maxWithoutFreebie;
	private String name;
	
	public abstract int getCheapestXPCostSpread(Integer additionalFreePoints);
	
	public abstract int getTotalDots();
	
	public int getTotalFreePoints() {
		return this.getFreeInitialPoints() + this.getFreeListPoints() + this.getFreeFreebiePoints();
	}
	
	public int getCheapestXPCostSpread() {
		return this.getCheapestXPCostSpread(0);
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public Integer getMaxWithoutFreebie() {
		if (this.maxWithoutFreebie != null)
			return maxWithoutFreebie;
		if (this.category != null)
			return this.category.getMaxWithoutFreebie();
		return 0;
	}

	public void setMaxWithoutFreebie(Integer maxWithoutFreebie) {
		this.maxWithoutFreebie = maxWithoutFreebie;
	}
	
	public Integer getFreeInitialPoints() {
		if (this.freeInitialPoints != null)
			return this.freeInitialPoints;
		return 0;
	}

	public void setFreeInitialPoints(Integer freePoints) {
		this.freeInitialPoints = freePoints;
	}
	
	public Integer getFreeListPoints() {
		if (this.freeListPoints != null)
			return freeListPoints;
		return 0;
	}

	public void setFreeListPoints(Integer freeListPoints) {
		this.freeListPoints = freeListPoints;
	}

	public List<Integer> getFreePointsList() {
		return this.freePointsList;
	}
	
	public int getFreePointsListLargest() {
		int val = this.freePointsList.get(0);
		for (int i : this.freePointsList)
			if (i > val)
				val = i;
		return val;
	}
	
	public Category getCategory() {
		return category;
	}

	public void setFreePointsList(List<Integer> freePointsList) {
		this.freePointsList = freePointsList;
	}

	public Integer getXpCostNext() {
		if (this.xpCostNext != null)
			return this.xpCostNext;
		if (this.category != null)
			return this.category.getXpCostNext();
		return 1;
	}

	public void setXpCostNext(Integer xpCostNext) {
		this.xpCostNext = xpCostNext;
	}

	public Integer getXpCostInitial() {
		if (this.xpCostInitial != null)
			return this.xpCostInitial;
		if (this.category != null)
			return this.category.getXpCostInitial();
		return 1;
	}

	public void setXpCostInitial(Integer xpCostInitial) {
		this.xpCostInitial = xpCostInitial;
	}

	public Integer getInitialValue() {
		if (this.initialValue != null)
			return this.initialValue;
		if (this.category != null)
			return this.category.getInitialValue();
		return 0;
	}

	public void setInitialValue(Integer initialValue) {
		this.initialValue = initialValue;
	}

	public Integer getFreebieCost() {
		if (this.freebieCost != null)
			return this.freebieCost;
		if (this.category != null)
			return this.category.getFreebieCost();
		return -1;
	}

	public void setFreebieCost(Integer freebieCost) {
		this.freebieCost = freebieCost;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getFreeFreebiePoints() {
		if (this.freeFreebiePoints != null)
			return this.freeFreebiePoints;
		return 0;
	}

	public void setFreeFreebiePoints(Integer freeFreebiePoints) {
		this.freeFreebiePoints = freeFreebiePoints;
	}
}
