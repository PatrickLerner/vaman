package vaman;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AGrouping {
	private Integer freeInitialPoints;
	private Integer freeFreebiePoints;
	private Integer freeListPoints;
	private List<Integer> freePointsList;
	private Integer xpCostNext;
	private Integer xpCostInitial;
	private Integer initialValue;
	private Integer freebieCost;
	private Integer maxWithoutFreebie;
	private String name;
	private AGrouping master;
	private Map<String, AGrouping> slaves;
	private int cheapestXPCost;
	private boolean recalculationNecessary = true;
	
	protected abstract void recalculateXPCost();
	
	public abstract int getTotalDots();
	
	public int getTotalFreePoints() {
		return this.getFreeInitialPoints() + this.getFreeListPoints() + this.getFreeFreebiePoints();
	}
	
	public void setMaster(AGrouping master) {
		this.master = master;
	}
	
	public AGrouping getMaster() {
		return this.master;
	}
	
	public Collection<AGrouping> getSlaves() {
		if (this.slaves != null)
			return this.slaves.values();
		else
			return new LinkedList<AGrouping>();
	}
	
	public AGrouping getSlave(String group) {
		if (this.slaves == null)
			return null;
		return this.slaves.get(group);
	}
	
	public void addSlave(AGrouping slave) {
		if (this.slaves == null)
			this.slaves = new HashMap<String, AGrouping>();
		this.slaves.put(slave.getName(), slave);
		slave.setMaster(this);
	}
	
	public void addSlaves(List<AGrouping> slaves) {
		for (AGrouping slave : slaves)
			this.addSlave(slave);
	}
	
	public Integer getMaxWithoutFreebie() {
		if (this.maxWithoutFreebie != null)
			return maxWithoutFreebie;
		if (this.master != null)
			return this.master.getMaxWithoutFreebie();
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

	public void setFreeInitialPoints(Integer freeInitialPoints) {
		if (this.freeInitialPoints == freeInitialPoints)
			return;
		this.freeInitialPoints = freeInitialPoints;
		this.setRecalculationNecessary(true);
	}
	
	public Integer getFreeListPoints() {
		if (this.freeListPoints != null)
			return freeListPoints;
		return 0;
	}

	public void setFreeListPoints(Integer freeListPoints) {
		if (this.freeListPoints == freeListPoints)
			return;
		this.freeListPoints = freeListPoints;
		this.setRecalculationNecessary(true);
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

	public void setFreePointsList(List<Integer> freePointsList) {
		this.freePointsList = freePointsList;
	}

	public Integer getXpCostNext() {
		if (this.xpCostNext != null)
			return this.xpCostNext;
		if (this.master != null)
			return this.master.getXpCostNext();
		return 1;
	}

	public void setXpCostNext(Integer xpCostNext) {
		this.xpCostNext = xpCostNext;
	}

	public Integer getXpCostInitial() {
		if (this.xpCostInitial != null)
			return this.xpCostInitial;
		if (this.master != null)
			return this.master.getXpCostInitial();
		return 1;
	}

	public void setXpCostInitial(Integer xpCostInitial) {
		this.xpCostInitial = xpCostInitial;
	}

	public Integer getInitialValue() {
		if (this.initialValue != null)
			return this.initialValue;
		if (this.master != null)
			return this.master.getInitialValue();
		return 0;
	}

	public void setInitialValue(Integer initialValue) {
		if (this.initialValue == initialValue)
			return;
		this.initialValue = initialValue;
		this.setRecalculationNecessary(true);
	}

	public Integer getFreebieCost() {
		if (this.freebieCost != null)
			return this.freebieCost;
		if (this.master != null)
			return this.master.getFreebieCost();
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
		if (this.freeFreebiePoints == freeFreebiePoints)
			return;
		this.freeFreebiePoints = freeFreebiePoints;
		this.setRecalculationNecessary(true);
	}

	public int getCheapestXPCost() {
		if (this.isRecalculationNecessary()) {
			this.recalculateXPCost();
			this.setRecalculationNecessary(false);
		}
		return cheapestXPCost;
	}

	protected void setCheapestXPCost(int cheapestXPCost) {
		this.cheapestXPCost = cheapestXPCost;
	}

	protected boolean isRecalculationNecessary() {
		return recalculationNecessary;
	}
	
	protected void setRecalculationNecessary(boolean recalculationNecessary) {
		this.recalculationNecessary = recalculationNecessary;
		if (this.master != null)
			this.master.setRecalculationNecessary(true);
	}
}
