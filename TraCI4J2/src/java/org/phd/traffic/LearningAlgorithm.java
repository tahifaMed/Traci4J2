/*   
    Copyright (C) 2013 ApPeAL Group, Politecnico di Torino

    This file is part of TraCI4J.

    TraCI4J is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TraCI4J is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TraCI4J.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phd.traffic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeMap;

public class LearningAlgorithm {

	private Map<String, Integer> states = new HashMap<String, Integer>();
	private Map<Integer, List<Integer>> actions = new HashMap<>();
	private Random generator;
	private QTable qTable;
	private Junction junction;
	private Double learningRate = 0.6;
	private Double discountFactor = 0.1;
	private Double epsilon = 0.5;
	private Double epsilonStep = 0.0;

	private Integer pastState = 0;

	private Integer currentState = 0;

	private Double reward = 0.0;

	private Double matrixReward[][];

	private Integer currentshoosenaction;
	private Integer pastshoosenaction;

	public LearningAlgorithm() {
		states.put("south|north|est|ouest", 0);
		states.put("south|north|ouest|est", 1);
		states.put("south|est|north|ouest", 2);
		states.put("south|ouest|north|est", 3);
		states.put("south|est|ouest|north", 4);
		states.put("south|ouest|est|north", 5);
		states.put("north|south|est|ouest", 6);
		states.put("north|south|ouest|est", 7);
		states.put("est|south|north|ouest", 8);
		states.put("ouest|south|north|est", 9);
		states.put("est|south|ouest|north", 10);
		states.put("ouest|south|est|north", 11);
		states.put("north|est|south|ouest", 12);
		states.put("north|ouest|south|est", 13);
		states.put("est|north|south|ouest", 14);
		states.put("ouest|north|est|south", 15);
		states.put("est|ouest|south|north", 16);
		states.put("ouest|est|south|north", 17);
		states.put("north|est|ouest|south", 18);
		states.put("north|ouest|est|south", 19);
		states.put("est|north|ouest|south", 20);
		states.put("ouest|north|south|est", 21);
		states.put("est|ouest|north|south", 22);
		states.put("ouest|est|north|south", 23);

		actions.put(0,
				Arrays.asList(new Integer[] { 1, 33, 2, 33, 2, 13, 2, 13, 2 }));
		actions.put(1,
				Arrays.asList(new Integer[] { 1, 33, 2, 13, 2, 33, 2, 13, 2 }));
		actions.put(2,
				Arrays.asList(new Integer[] { 1, 33, 2, 13, 2, 13, 2, 33, 2 }));
		actions.put(3,
				Arrays.asList(new Integer[] { 1, 13, 2, 33, 2, 33, 2, 13, 2 }));
		actions.put(4,
				Arrays.asList(new Integer[] { 1, 13, 2, 33, 2, 13, 2, 33, 2 }));
		actions.put(5,
				Arrays.asList(new Integer[] { 1, 13, 2, 13, 2, 33, 2, 33, 2 }));
		actions.put(6,
				Arrays.asList(new Integer[] { 1, 33, 2, 23, 2, 23, 2, 13, 2 }));
		actions.put(7,
				Arrays.asList(new Integer[] { 1, 33, 2, 23, 2, 13, 2, 23, 2 }));
		actions.put(8,
				Arrays.asList(new Integer[] { 1, 33, 2, 13, 2, 23, 2, 23, 2 }));
		actions.put(9,
				Arrays.asList(new Integer[] { 1, 23, 2, 33, 2, 23, 2, 13, 2 }));
		actions.put(10,
				Arrays.asList(new Integer[] { 1, 23, 2, 33, 2, 13, 2, 23, 2 }));
		actions.put(11,
				Arrays.asList(new Integer[] { 1, 13, 2, 33, 2, 23, 2, 23, 2 }));
		actions.put(12,
				Arrays.asList(new Integer[] { 1, 23, 2, 23, 2, 33, 2, 13, 2 }));
		actions.put(13,
				Arrays.asList(new Integer[] { 1, 23, 2, 13, 2, 33, 2, 23, 2 }));
		actions.put(14,
				Arrays.asList(new Integer[] { 1, 13, 2, 23, 2, 33, 2, 23, 2 }));
		actions.put(15,
				Arrays.asList(new Integer[] { 1, 23, 2, 23, 2, 13, 2, 33, 2 }));
		actions.put(16,
				Arrays.asList(new Integer[] { 1, 23, 2, 13, 2, 23, 2, 33, 2 }));
		actions.put(17,
				Arrays.asList(new Integer[] { 1, 13, 2, 23, 2, 23, 2, 33, 2 }));
		actions.put(18,
				Arrays.asList(new Integer[] { 1, 23, 2, 23, 2, 23, 2, 23, 2 }));
		currentshoosenaction = 0;
		pastshoosenaction = 0;
		FileInputStream fis1;

		try {
			fis1 = new FileInputStream("rewardMatrix");
			ObjectInputStream ois1 = new ObjectInputStream(fis1);
			matrixReward = (Double[][]) ois1.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		generator = new Random();

	}

	public LearningAlgorithm(Junction junction) throws IOException,
			InterruptedException {

		this();

		qTable = new QTable(states.size(), actions.size());

		this.junction = junction;
		this.learningRate = 0.9;
		this.discountFactor = 0.1;
		this.epsilon = 0.9;

	}

	public LearningAlgorithm(Junction junction, String file)
			throws IOException, InterruptedException {

		this();
		this.junction = junction;
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			qTable = (QTable) ois.readObject();
			ois.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		synchronized (LearningAlgorithm.class) {
			System.out.println("agentName : " + junction.getAgentName());
			this.getqTable().printQtable();

		}

		this.learningRate = 0.5;
		this.discountFactor = 0.1;
		this.epsilon = 0.4;

	}

	public QTable getqTable() {
		return qTable;
	}

	public Integer chooseAction(Integer state) {
		Integer action = 0;
		if (epsilonStep <= epsilon) {
			epsilonStep += 0.101;
			action = generator.nextInt(19);
			System.out
					.println("                                        randomAction :"
							+ epsilonStep + " Action : " + action);

			return action;
		} else if (epsilonStep >= epsilon) {
			epsilonStep += 0.101;
			action = BestAction(state);
			System.out
					.println("                                         BestAction:"
							+ epsilonStep + " Action : " + action);

			if (epsilonStep >= 0.99) {
				epsilonStep = 0.0;
			}
			return action;
		}
		
		else
			return 0;

	}

	public void QLearning() throws IOException {

		synchronized (Junction.class) {

			currentState = calculateState(junction.getCurrentVehiclesMap());
		}
		System.out.println();
		System.out.println("Next State : " + currentState);
		currentshoosenaction = chooseAction(currentState);

		synchronized (Junction.class) {
			junction.changeTrafficLightDuration(actions
					.get(currentshoosenaction));
		}

		reward = calculateReward();
		System.out
				.println("                                                    reward : "
						+ reward);
		Double maxQ = calculateMaxQ(currentState);

		if (reward >= 5.0) {

			System.out.println("Q[" + pastState + "][" + pastshoosenaction
					+ "] = " + qTable.getQ()[pastState][pastshoosenaction]
					+ "+" + learningRate + "* (" + reward + "+"
					+ matrixReward[pastState][pastshoosenaction] + " ("
					+ discountFactor + "*" + maxQ + ") - "
					+ qTable.getQ()[pastState][pastshoosenaction] + "))");

			qTable.getQ()[pastState][pastshoosenaction] = qTable.getQ()[pastState][pastshoosenaction]
					+ learningRate
					* (reward + matrixReward[pastState][pastshoosenaction]
							+ (discountFactor * maxQ) - qTable.getQ()[pastState][pastshoosenaction]);

			System.out.println("Q[" + pastState + "][" + pastshoosenaction
					+ "] = " + qTable.getQ()[pastState][pastshoosenaction]);

		}
		pastState = currentState;
		pastshoosenaction = currentshoosenaction;

	}

	public Integer BestAction(Integer state) {
		Double max = 0.0;
		Integer bestaction = 0;
		for (int i = 0; i < qTable.getQ()[state].length; i++) {
			if (max < qTable.getQ()[state][i]) {
				max = qTable.getQ()[state][i];
				bestaction = i;
			}
		}
		return bestaction;
	}

	public void exploitQlearning() throws IOException {

		Integer state = calculateState(junction.getCurrentVehiclesMap());
		Integer action = BestAction(state);
		System.out
				.println(junction.getAgentName() + " state : " + state
						+ " action : " + action + " R : "
						+ matrixReward[state][action]);
		synchronized (Junction.class) {
			junction.changeTrafficLightDuration(actions.get(action));
		}

	}

	public void adaptLearning() throws IOException {

		synchronized (Junction.class) {

			currentState = calculateState(junction.getCurrentVehiclesMap());
		}
		System.out.println();
		System.out.println("Next State : " + currentState);
		currentshoosenaction = chooseAction(currentState);

		synchronized (Junction.class) {
			junction.changeTrafficLightDuration(actions
					.get(currentshoosenaction));
		}

		reward = calculateReward();
		System.out
				.println("                                                    reward : "
						+ reward);
		Double maxQ = calculateMaxQ(currentState);

		if (reward >= 8.0) {
			if (qTable.getQ()[pastState][pastshoosenaction] == 0.0)
				learningRate = 0.9;
			else
				learningRate = 0.5;

			System.out.println("Q[" + pastState + "][" + pastshoosenaction
					+ "] = " + qTable.getQ()[pastState][pastshoosenaction]
					+ "+" + learningRate + "* (" + reward + "+"
					+ matrixReward[pastState][pastshoosenaction] + " ("
					+ discountFactor + "*" + maxQ + ") - "
					+ qTable.getQ()[pastState][pastshoosenaction] + "))");

			qTable.getQ()[pastState][pastshoosenaction] = qTable.getQ()[pastState][pastshoosenaction]
					+ learningRate
					* (reward + matrixReward[pastState][pastshoosenaction]
							+ (discountFactor * maxQ) - qTable.getQ()[pastState][pastshoosenaction]);

			System.out.println("Q[" + pastState + "][" + pastshoosenaction
					+ "] = " + qTable.getQ()[pastState][pastshoosenaction]);

		}
		pastState = currentState;
		pastshoosenaction = currentshoosenaction;


	}

	public Double calculateMaxQ(Integer state) {

		Double max = 0.0;
		for (int i = 0; i < qTable.getQ()[state].length; i++) {
			if (max < qTable.getQ()[state][i])
				max = qTable.getQ()[state][i];
		}
		return max;
	}

	public Double calculateReward() {

		Map<String, Integer> pastVehiclesMap = junction.getPastVehiclesMap();
		Double north = (double) junction.getNumberVehicleNorth();
		Double south = (double) junction.getNumberVehicleSouth();
		Double est = (double) junction.getNumberVehicleEst();
		Double ouest = (double) junction.getNumberVehicleOuest();
		Double sommeVehicles = (double) junction
				.getPastNumberVehicleInJunction();

		Double rewardnorth = 0.0;
		if (north <= pastVehiclesMap.get("north"))
			rewardnorth = (double) ((north * ((double) pastVehiclesMap
					.get("north") + 1)) / sommeVehicles+1);
		// else
		// rewardnorth = -((double) ((north/sommeVehicles)));

		Double rewardsouth = 0.0;
		if (south <= pastVehiclesMap.get("south"))
			rewardsouth = (double) ((south * ((double) pastVehiclesMap
					.get("south") + 1)) / sommeVehicles+1);
		// else
		// rewardsouth = -((double) ((south/sommeVehicles)));

		Double rewardest = 0.0;
		if (est <= pastVehiclesMap.get("est"))
			rewardest = (double) ((est * ((double) pastVehiclesMap.get("est") + 1)) / sommeVehicles+1);
		// else
		// rewardest = -((double) ((est/sommeVehicles)));

		Double rewardouest = 0.0;
		if (ouest <= pastVehiclesMap.get("ouest"))
			rewardouest = (double) ((ouest * ((double) pastVehiclesMap
					.get("ouest") + 1)) / sommeVehicles+1);
		// else
		// rewardouest = -((double) ((ouest/sommeVehicles)));

		List<Integer> action3 = actions.get(pastshoosenaction);
		System.out.println("choosenAction : " + " north : " + action3.get(1)
				+ " ouest : " + action3.get(3) + " south : " + action3.get(5)
				+ " est : " + action3.get(7));
		System.out.println(junction.getAgentName() + " north : " + north + "/"
				+ pastVehiclesMap.get("north") + " reward :" + rewardnorth
				+ " ouest : " + ouest + "/" + pastVehiclesMap.get("ouest")
				+ " reward :" + rewardouest + " south : " + south + "/"
				+ pastVehiclesMap.get("south") + " reward :" + rewardsouth
				+ " est : " + est + "/" + pastVehiclesMap.get("est")
				+ " reward :" + rewardest +

				" somme vehicles : " + sommeVehicles);

		Double reward = rewardnorth + rewardest + rewardouest + rewardsouth;

		return reward;
	}

	public Integer calculateState(Map<String, Integer> maps) {

		// HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		ValueComparator bvc = new ValueComparator(maps);
		TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

		sorted_map.putAll(maps);

		// System.out.println("results: " + sorted_map);
		NavigableSet<String> keys = sorted_map.navigableKeySet();
		Iterator<String> it = keys.iterator();
		String state = new String();
		Boolean flag = true;
		while (it.hasNext()) {
			String key = it.next();
			if (flag == false)
				state += "|";
			flag = false;
			state += key;

		}
		return states.get(state);
	}

}
