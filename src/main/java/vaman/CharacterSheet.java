package vaman;

import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CharacterSheet extends AGrouping {
	private Set<AGrouping> freebieBuyable;
	private Integer freebiePoints;
	private Map<AGrouping, Integer> freebiePointsSpending;
	private Map<AGrouping, List<String>> postLoadResolveValues;

	public CharacterSheet(String fileName) {
		this.freebieBuyable = new HashSet<AGrouping>();
		this.postLoadResolveValues = new HashMap<AGrouping, List<String>>();
		this.loadFromFile(fileName);
		
		for (Entry<AGrouping, List<String>> res : this.postLoadResolveValues.entrySet()) {			
			List<String> dep = new LinkedList<String>();
			AGrouping c = null;
			for (String val : res.getValue()) {
				String[] uri = val.split("/");
				c = this.getSlave(uri[0]);
				for (int i = 1; i < uri.length - 1; i++)
					c = c.getSlave(uri[i]);
				dep.add(uri[uri.length - 1]);
				if (res.getKey().getInitialValueDependencyGrouping() != null)
					if (res.getKey().getInitialValueDependencyGrouping() != c)
						throw new RuntimeException("Dependencies on multiple groups are not supported.");
				res.getKey().setInitialValueDependencyGrouping(c);
			}
			res.getKey().setInitialValueDependencies(dep);
			c.addSlave(res.getKey());
			res.getKey().setInitialValue(((PropertyGroup) c).getMinimumSpreadOnDependencies(dep));
		}
	}

	private void parseParameters(JSONObject obj, AGrouping grouping) {
		if (obj.keySet().contains("_initialValue")) {
			if (obj.get("_initialValue") instanceof Long) {
				grouping.setInitialValue(((Long) obj.get("_initialValue")).intValue());
				if (postLoadResolveValues.containsKey(grouping))
					postLoadResolveValues.remove(grouping);
			}
			else if (obj.get("_initialValue") instanceof JSONArray) {
				JSONArray arr = (JSONArray) obj.get("_initialValue");
				List<String> refs = new LinkedList<String>();
				for (Object val : arr.toArray())
					refs.add((String) val);
				
				postLoadResolveValues.put(grouping, refs);
			}
		}
		if (obj.keySet().contains("_xpCostInitial"))
			grouping.setXpCostInitial(((Long) obj.get("_xpCostInitial")).intValue());
		if (obj.keySet().contains("_xpCostNext"))
			grouping.setXpCostNext(((Long) obj.get("_xpCostNext")).intValue());
		if (obj.keySet().contains("_maxWithoutFreebie"))
			grouping.setMaxWithoutFreebie(((Long) obj.get("_maxWithoutFreebie")).intValue());
		if (obj.keySet().contains("_freebieCost")) {
			grouping.setFreebieCost(((Long) obj.get("_freebieCost")).intValue());
			if (grouping.getFreebieCost() > 0)
				this.freebieBuyable.add(grouping);
		}
		if (obj.keySet().contains("_freeDots")) {
			if (obj.get("_freeDots") instanceof Long)
				grouping.setFreeInitialPoints(((Long) obj.get("_freeDots")).intValue());
			else if (obj.get("_freeDots") instanceof JSONArray) {
				List<Integer> freePointsList = new LinkedList<Integer>();
				JSONArray arr = (JSONArray) obj.get("_freeDots");
				for (Object val : arr.toArray())
					freePointsList.add(((Long) val).intValue());
				grouping.setFreePointsList(freePointsList);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadFromFile(String fileName) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject cha_obj = (JSONObject) parser.parse(new FileReader(fileName));

			if (cha_obj.containsKey("_base")) {
				File file = new File(fileName);
				String absolutePath = file.getAbsolutePath();
				String filePath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
				this.loadFromFile(filePath + File.separator + cha_obj.get("_base") + ".json");
			}
			
			if (cha_obj.containsKey("_freebiePoints"))
				this.freebiePoints = ((Long) cha_obj.get("_freebiePoints")).intValue();

			Set<String> catNames = (Set<String>) cha_obj.keySet();

			for (String cat : catNames) {
				Category cur_cat;
				if (this.getSlave(cat) != null)
					cur_cat = (Category) this.getSlave(cat);
				else {
					cur_cat = new Category();
					cur_cat.setName(cat);
					this.addSlave(cur_cat);
				}
				cur_cat.setName(cat);
				
				if (cat.startsWith("_"))
					continue;
				
				JSONObject cat_obj = (JSONObject) cha_obj.get(cat);
				Set<String> grpNames = (Set<String>) cat_obj.keySet();

				for (String grp : grpNames) {
					if (grp.startsWith("_"))
						continue;

					Map<String, Integer> properties = new HashMap<String, Integer>();

					JSONObject grp_obj = (JSONObject) cat_obj.get(grp);
					Set<String> proNames = (Set<String>) grp_obj.keySet();
					for (String pro : proNames) {
						if (pro.startsWith("_"))
							continue;

						properties.put(pro, ((Long) grp_obj.get(pro)).intValue());
					}

					PropertyGroup cur_grp;
					if (cur_cat.getSlave(grp) != null) {
						cur_grp = (PropertyGroup) cur_cat.getSlave(grp);
						cur_grp.getProperties().putAll(properties);
					}
					else {
						cur_grp = new PropertyGroup(properties);
						cur_grp.setName(grp);
						cur_cat.addSlave(cur_grp);
						for (AGrouping group : cur_cat.getSlaves())
							group.setMaster(cur_cat);
					}
					this.parseParameters(grp_obj, cur_grp);
					cur_cat.addSlave(cur_grp);
				}
				
				this.parseParameters(cat_obj, cur_cat);
				this.addSlave(cur_cat);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Set<Map<AGrouping, Integer>> getPossibleFreebiePointSpreads(Set<Map<AGrouping, Integer>> possibilities,
			Set<AGrouping> remainingGroupings, int remainingPoints) {
		if (remainingGroupings.size() == 0)
			return possibilities;

		Set<AGrouping> newRemainingGroupings = new HashSet<AGrouping>(remainingGroupings);
		AGrouping group = remainingGroupings.iterator().next();
		newRemainingGroupings.remove(group);

		int maxValue = group.getTotalDots() - group.getFreeInitialPoints();
		
		/*if (group.getInitialValueDependencies() != null) {
			PropertyGroup pg = (PropertyGroup) group.getMaster();
			maxValue = group.getTotalDots();
			maxValue -= pg.getMinimumSpreadOnDependencies(group.getInitialValueDependencies());
		}*/

		Set<Map<AGrouping, Integer>> result = new HashSet<Map<AGrouping, Integer>>(possibilities);
		for (int i = 1; i <= maxValue; i++) {
			int cost = i * group.getFreebieCost();
			if (cost <= remainingPoints) {
				Set<Map<AGrouping, Integer>> newPossibilities = new HashSet<Map<AGrouping, Integer>>(possibilities);
				for (Map<AGrouping, Integer> possibility : possibilities) {
					if (possibility.containsKey(group))
						continue;
					Map<AGrouping, Integer> newPosibility = new HashMap<AGrouping, Integer>(possibility);
					newPosibility.put(group, i);
					newPossibilities.add(newPosibility);
					newPossibilities.remove(possibility);
				}
				result.addAll(this.getPossibleFreebiePointSpreads(newPossibilities, newRemainingGroupings,
						remainingPoints - cost));
			}
		}
		result.addAll(this.getPossibleFreebiePointSpreads(possibilities, newRemainingGroupings, remainingPoints));
		
		Set<Map<AGrouping, Integer>> illegalResults = new HashSet<Map<AGrouping, Integer>>();
		
		for (Map<AGrouping, Integer> perm : result) {
			for (AGrouping pgroup : perm.keySet()) {
				if (pgroup.getMaster().getFreePointsList() != null) {
					Map<Integer, Integer> spreadCount = new HashMap<Integer, Integer>();
					List<Integer> spread = new LinkedList<Integer>(pgroup.getMaster().getFreePointsList());
					Collections.sort(spread);
					Collections.reverse(spread);
					
					int count = 1;
					for (Integer i : spread) {
						spreadCount.put(i, count);
						count += 1;
					}
					
					for (AGrouping g : pgroup.getMaster().getSlaves()) {
						int permVal = 0;
						if (perm.containsKey(g))
							permVal = perm.get(g);
						for (Integer sp : spreadCount.keySet()) {
							int totalDots = g.getTotalDots();
							int potentialFreeDots = sp + g.getFreeInitialPoints() + permVal;
							if (totalDots >= potentialFreeDots)
								spreadCount.put(sp, spreadCount.get(sp) - 1);
						}
					}
					for (Integer i : spreadCount.keySet())
						if (spreadCount.get(i) > 0)
							illegalResults.add(perm);
				}
			}
		}
		
		for (Map<AGrouping, Integer> perm : illegalResults)
			result.remove(perm);
		
		return result;
	}
	
	@Override
	protected void recalculateXPCost() {
		Set<AGrouping> buyableGroups = new HashSet<AGrouping>();
		for (AGrouping grouping : this.freebieBuyable) {
			if (grouping instanceof Category)
				buyableGroups.addAll(((Category) grouping).getSlaves());
			else
				buyableGroups.add(grouping);
		}
		
		int fbcount = this.freebiePoints;
		Map<AGrouping, Integer> initial = new HashMap<AGrouping, Integer>();
		for (AGrouping group : buyableGroups) {
			if (group.getXpCostNext() == -1) {
				int count = group.getTotalDots() - group.getFreeInitialPoints();
				initial.put(group, count);
				fbcount -= count * group.getFreebieCost();
			}
		}

		Set<Map<AGrouping, Integer>> initialSet = new HashSet<Map<AGrouping, Integer>>();
		initialSet.add(initial);
		
		Map<AGrouping, Integer> cheapestPermutation = null;
		int cheapestCost = Integer.MAX_VALUE;
		for (Map<AGrouping, Integer> pos : this
				.getPossibleFreebiePointSpreads(initialSet, buyableGroups, fbcount)) {
			
			//for (Entry<AGrouping, Integer> p : pos.entrySet())
			//	System.out.print(p.getKey().getName() + " " + p.getValue() + "   ");
			//System.out.println("");
			
			for (AGrouping group : buyableGroups) {
				if (pos.containsKey(group))
					group.setFreeFreebiePoints(pos.get(group));
				else
					group.setFreeFreebiePoints(0);
			}

			int currentCost = 0;
			try {
			for (AGrouping cat : this.getSlaves())
				currentCost += cat.getCheapestXPCost();
			} catch (Exception e) { currentCost = Integer.MAX_VALUE; }
			
			if (currentCost < cheapestCost || cheapestPermutation == null) {
				cheapestCost = currentCost;
				cheapestPermutation = pos;
			}
		}

		this.freebiePointsSpending = cheapestPermutation;
		this.setCheapestXPCost(cheapestCost);

		for (AGrouping group : buyableGroups) {
			if (this.freebiePointsSpending.containsKey(group))
				group.setFreeFreebiePoints(this.freebiePointsSpending.get(group));
			else
				group.setFreeFreebiePoints(0);
		}

		for (AGrouping cat : this.getSlaves()) {
			cat.recalculateXPCost();
			cat.getCheapestXPCost();
		}
		this.setRecalculationNecessary(false);

		this.setCheapestXPCost(cheapestCost);
	}

	public String toString() {
		if (this.isRecalculationNecessary()) {
			this.recalculateXPCost();
			this.setRecalculationNecessary(false);
		}
		
		StringBuilder sb = new StringBuilder();
		
		int sum = 0;
		for (AGrouping group : this.freebiePointsSpending.keySet())
			sum += group.getFreebieCost() * this.freebiePointsSpending.get(group);
		
		sb.append("freebie points (");
		sb.append(sum);
		sb.append("/");
		sb.append(this.freebiePoints);
		sb.append(")\n");
		for (AGrouping group : this.freebiePointsSpending.keySet()) {
			sb.append("\t");
			sb.append(StringUtils.rightPad(group.getName(), 20));
			sb.append(" ");
			sb.append(this.freebiePointsSpending.get(group));
			sb.append("x ");
			sb.append(group.getFreebieCost());
			sb.append(" fp\n");
		}
		
		sb.append("\n");
		
		for (AGrouping group : this.getSlaves())
			sb.append(group.toString());
		
		sb.append("\ntotal: ");
		sb.append(this.getCheapestXPCost());
		sb.append(" xp");
		
		return sb.toString();
	}

	@Override
	public int getTotalDots() {
		// TODO Auto-generated method stub
		return 0;
	}
}
