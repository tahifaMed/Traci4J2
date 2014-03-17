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

import it.polito.appeal.traci.Edge;
import it.polito.appeal.traci.InductionLoop;
import it.polito.appeal.traci.Logic;
import it.polito.appeal.traci.Phase;
import it.polito.appeal.traci.Repository;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.TLState;
import it.polito.appeal.traci.TrafficLight;
import it.polito.appeal.traci.Vehicle;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Junction {

	private String agentName;
	private Connection connection;
	private SumoTraciConnection traciconnection;
	private TrafficLight trafficLight;
	private Repository<Edge> edges;
	private Repository<InductionLoop> detectors;
	
	private Map<String, Edge> mapEdges;
	private Map<String, InductionLoop> mapDetectors;
	private Map<String, Vehicle> poolvehicles;
	private Map<String, Vehicle> poolvehiclenorth;
	private Map<String, Vehicle> poolvehicleest;
	private Map<String, Vehicle> poolvehicleouest;
	private Map<String, Vehicle> poolvehiclesouth;
	private Map<String, Integer> currentVehiclesMap = new HashMap<>();

	private Map<String, Integer> pastVehiclesMap = new HashMap<>();

	private String right = "1_0";
	private String Left = "1_1";

	private Integer numberVehicleSouth = 0;

	private Integer numberVehicleNorth = 0;

	private Integer numberVehicleEst = 0;

	private Integer numberVehicleOuest = 0;

	private TLState[] tlStates;

	public Junction() {

	}

	public Junction(Connection connection, String agentName)
			throws IOException, InterruptedException {

		this.agentName = agentName;
		this.connection = connection;
		traciconnection = connection.getTraciconnection();
		poolvehicles = new HashMap<>();
		mapEdges = new HashMap<>();
		mapDetectors = new HashMap<>();
		edges = traciconnection.getEdgeRepository();
		detectors = traciconnection.getInductionLoopRepository();
		//System.out.println(" junction agentName : " + this.agentName);

		synchronized (edges) {
			Set<String> keys = edges.getAll().keySet();
			for (String key : keys) {
				if (key.startsWith(this.agentName)) {
					//System.out.println(key);
					mapEdges.put(key, edges.getAll().get(key));
				}
			}

		}
		synchronized (detectors) {
			Set<String> keys = detectors.getAll().keySet();
			for (String key : keys) {
				if (key.startsWith(this.agentName)) {
					mapDetectors.put(key, detectors.getAll().get(key));
					// System.out.println("key : "+key);
				}
			}
			//System.out.println(this.agentName);

			trafficLight = this.connection.getTraciconnection()
					.getTrafficLightRepository().getByID(this.agentName);
		}

		currentVehiclesMap.put("south", 1);
		currentVehiclesMap.put("north", 1);
		currentVehiclesMap.put("est", 1);
		currentVehiclesMap.put("ouest", 1);
		pastVehiclesMap.put("south", 1);
		pastVehiclesMap.put("north", 1);
		pastVehiclesMap.put("est", 1);
		pastVehiclesMap.put("ouest", 1);
		poolvehiclenorth = new HashMap<>();
		poolvehicleouest = new HashMap<>();
		poolvehiclesouth = new HashMap<>();
		poolvehicleest = new HashMap<>();
		
		tlStates = new TLState[] { new TLState("rrrrrrrrrrrrrrrr"),
				new TLState("GGGGrrrrrrrrrrrr"),
				new TLState("yyyyrrrrrrrrrrrr"),
				new TLState("rrrrGGGGrrrrrrrr"),
				new TLState("rrrryyyyrrrrrrrr"),
				new TLState("rrrrrrrrGGGGrrrr"),
				new TLState("rrrrrrrryyyyrrrrr"),
				new TLState("rrrrrrrrrrrrGGGG"),
				new TLState("rrrrrrrrrrrryyyy") };
	}

	

	public SumoTraciConnection getConnection() {
		return traciconnection;
	}

	public void changeTrafficLightDuration(List<Integer> durations)
			throws IOException {

		Phase[] phases = new Phase[durations.size()];
		phases[0] = new Phase(durations.get(0),  tlStates[0]);
		int i = 1;
		while (i < phases.length) {
			phases[i] = new Phase(durations.get(i) * 1000, tlStates[i]);
			i++;
		}

		Logic expectedLogic = new Logic(this.agentName, 0, phases);
		synchronized (SumoTraciConnection.class) {
			trafficLight.queryChangeCompleteProgramDefinition().setValue(
					expectedLogic);

			trafficLight.queryChangeCompleteProgramDefinition().run();

		}

	}



	public TrafficLight getTrafficLight() {
		return trafficLight;
	}

	public Map<String, Integer> getEdgeNumberVehicle() throws IOException {

		//System.out.println("step : "+connection.getTraciconnection().getCurrentSimStep()+"pastVehicleMap "+pastVehiclesMap.toString());
		pastVehiclesMap.put("south", currentVehiclesMap.get("south"));
		pastVehiclesMap.put("north", currentVehiclesMap.get("north"));
		pastVehiclesMap.put("est", currentVehiclesMap.get("est"));
		pastVehiclesMap.put("ouest", currentVehiclesMap.get("ouest"));
		

		mapEdges.get(agentName + "south1").queryReadLastStepHaltingNumber()
				.setObsolete();

		mapEdges.get(agentName + "north1").queryReadLastStepHaltingNumber()
				.setObsolete();

		mapEdges.get(agentName + "est1").queryReadLastStepHaltingNumber()
				.setObsolete();

		mapEdges.get(agentName + "ouest1").queryReadLastStepHaltingNumber()
				.setObsolete();

		currentVehiclesMap.put("south", mapEdges.get(agentName + "south1").queryReadLastStepHaltingNumber().get());
		currentVehiclesMap.put("north", mapEdges.get(agentName + "north1").queryReadLastStepHaltingNumber().get());
		currentVehiclesMap.put("est", mapEdges.get(agentName + "est1").queryReadLastStepHaltingNumber().get());
		currentVehiclesMap.put("ouest", mapEdges.get(agentName + "ouest1").queryReadLastStepHaltingNumber().get());

		return currentVehiclesMap;

	}

	public void calculateNumberVehiclesInDetectors() throws IOException {
		
		
		Set<Vehicle> vehiclesouths = new HashSet<>();
		Set<Vehicle> vehiclenorth = new HashSet<>();
		Set<Vehicle> vehicleest = new HashSet<>();
		Set<Vehicle> vehicleouest = new HashSet<>();
		
						
		vehiclesouths.addAll(mapDetectors.get(agentName + "south" + right + "l_Detector").queryReadLastStepVehicles().get());
		vehiclesouths.addAll(mapDetectors.get(agentName + "south" + Left + "l_Detector").queryReadLastStepVehicles().get());
		Iterator<Vehicle> it =vehiclesouths.iterator();
		while (it.hasNext()) {
			Vehicle v = it.next();
			if (!poolvehiclesouth.containsKey(v.getID())) {
				poolvehiclesouth.put(v.getID(), v);
			}
			
		}
		
		
		
		vehiclenorth.addAll(mapDetectors.get(agentName + "north" + right + "l_Detector").queryReadLastStepVehicles().get());
		vehiclenorth.addAll(mapDetectors.get(agentName + "north" + Left + "l_Detector").queryReadLastStepVehicles().get());
		Iterator<Vehicle> it1 =vehiclenorth.iterator();
		while (it1.hasNext()) {
			Vehicle v = it1.next();
			if (!poolvehiclenorth.containsKey(v.getID())) {
				poolvehiclenorth.put(v.getID(), v);
			}
			
		}
				
		vehicleest.addAll(mapDetectors.get(agentName + "est" + right + "l_Detector").queryReadLastStepVehicles().get());
		vehicleest.addAll(mapDetectors.get(agentName + "est" + Left + "l_Detector").queryReadLastStepVehicles().get());
		Iterator<Vehicle> it2 =vehicleest.iterator();
		while (it2.hasNext()) {
			Vehicle v = it2.next();
			if (!poolvehicleest.containsKey(v.getID())) {
				poolvehicleest.put(v.getID(), v);
			}
			
		}
				
		vehicleouest.addAll(mapDetectors.get(agentName + "ouest" + right + "l_Detector").queryReadLastStepVehicles().get());
		vehicleouest.addAll(mapDetectors.get(agentName + "ouest" + Left + "l_Detector").queryReadLastStepVehicles().get());
		Iterator<Vehicle> it3 =vehicleouest.iterator();
		while (it3.hasNext()) {
			Vehicle v = it3.next();
			if (!poolvehicleouest.containsKey(v.getID())) {
				poolvehicleouest.put(v.getID(), v);
			}
			
		}
//		System.out.println(this.agentName + " Est : "+poolvehicleest.size()+ " North : "+poolvehiclenorth.size()
//				+ " Ouest : "+poolvehicleouest.size()+ " south : "+poolvehiclesouth.size());		
	}
	

//	public void printNumberVehiclesInDetectors() {
////		System.out.println("south : "+numberVehicleSouth + " north : " + numberVehicleNorth + " est : "
////				+ numberVehicleEst + " ouest : " + numberVehicleOuest);
//
//	}

	public void initializeNumberVehiclesInDetectors() {
		numberVehicleSouth = 0;
		numberVehicleNorth = 0;
		numberVehicleEst = 0;
		numberVehicleOuest = 0;
		poolvehicleest.clear();poolvehiclenorth.clear();poolvehicleouest.clear();poolvehiclesouth.clear();
	}


	public void poolVehicles() {
		this.addVehicletoPool();
		this.removeVehicletoPool();

	}

	public Double calculateNumberofWaitingTimeInjunction() {
		Double numberOfwaitingtime = 0.0;
		Iterator<String> it = poolvehicles.keySet().iterator();
		while (it.hasNext()) {
			String id = it.next();
			Vehicle v = poolvehicles.get(id);
			numberOfwaitingtime += connection.getTraciconnection()
					.getCurrentSimStep() - v.getTimeStepEnter();
		}
		return numberOfwaitingtime;
	}

	public void addVehicletoPool() {
		try {
			
			Set<Vehicle> vehicles = new HashSet<>();
			vehicles.addAll(mapDetectors.get(agentName + "est" + Left + "e_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "est" + right + "e_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "south" + Left + "e_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "south" + right + "e_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "north" + Left + "e_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "north" + right + "e_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "ouest" + Left + "e_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "ouest" + right + "e_Detector").queryReadLastStepVehicles().get());

			Iterator<Vehicle> it = vehicles.iterator();
			while (it.hasNext()) {
				Vehicle v = it.next();
				v.setTimeStepEnter(connection.getTraciconnection()
						.getCurrentSimStep());
				if (!poolvehicles.containsKey(v.getID())) {
					poolvehicles.put(v.getID(), v);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void removeVehicletoPool() {
		try {
			// System.out.println(mapDetectors);
			Set<Vehicle> vehicles = new HashSet<>();
			vehicles.addAll(mapDetectors.get(agentName + "est" + Left + "l_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "est" + right + "l_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "south" + Left + "l_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "south" + right + "l_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "north" + Left + "l_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "north" + right + "l_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "ouest" + Left + "l_Detector").queryReadLastStepVehicles().get());
			vehicles.addAll(mapDetectors.get(agentName + "ouest" + right + "l_Detector").queryReadLastStepVehicles().get());

			Iterator<Vehicle> it = vehicles.iterator();
			while (it.hasNext()) {
				Vehicle v = it.next();

				if (poolvehicles.containsKey(v.getID())) {
					poolvehicles.remove(v.getID());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public String getAgentName() {
		return agentName;
	}

	public Integer getCurrentNumberVehicleInJunction() {

		Integer num = currentVehiclesMap.get("south")
				+ currentVehiclesMap.get("north")
				+ currentVehiclesMap.get("est")
				+ currentVehiclesMap.get("ouest");

		return num;
	}

	public Integer getPastNumberVehicleInJunction() {
		Integer num = pastVehiclesMap.get("south")
				+ pastVehiclesMap.get("north") + pastVehiclesMap.get("est")
				+ pastVehiclesMap.get("ouest");

		return num;
	}

	public Map<String, Integer> getCurrentVehiclesMap() {
		return currentVehiclesMap;
	}

	public void setCurrentVehiclesMap(Map<String, Integer> currentVehiclesMap) {
		this.currentVehiclesMap = currentVehiclesMap;
	}

	public Map<String, Integer> getPastVehiclesMap() {
		return pastVehiclesMap;
	}

	public void setPastVehiclesMap(Map<String, Integer> pastVehiclesMap) {
		this.pastVehiclesMap = pastVehiclesMap;
	}

	public Integer getNumberVehicleSouth() {
		return numberVehicleSouth=poolvehiclesouth.size();
	}

	public Integer getNumberVehicleNorth() {
		return numberVehicleNorth=poolvehiclenorth.size();
	}

	public Integer getNumberVehicleEst() {
		return numberVehicleEst=poolvehicleest.size();
	}

	public Integer getNumberVehicleOuest() {
		return numberVehicleOuest=poolvehicleouest.size();
	}

}
