/*   
    Copyright (C) 2014 ApPeAL Group, Politecnico di Torino

    This file is part of TraCI4J2.

    TraCI4J2 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    TraCI4J2 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with TraCI4J2.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phd.traffic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Magic {

	private static final Integer wholeStep = 30200 ;
	private List<String> agents = new ArrayList<>();
	private Integer numAgent = 4;
	private Connection connection;
	private List<Junction> junctions;
	private List<LearningAlgorithm> learningAlgorithms;
	private Double step = 100.0;
	private Data data;
	private Serializating serial;

	public Magic() throws IOException, InterruptedException {
		
		this.serial = new Serializating("AdaptEnv2");
		this.connection = Connection.getInstance();
		for (int i = 0; i < numAgent; i++) {

			agents.add("agent" + (i + 1));
		}
		data = new Data();
		junctions = new ArrayList<>();
		learningAlgorithms = new ArrayList<>();
		connection.getTraciconnection().runServer();
		for (int i = 0; i < agents.size(); i++) {
			junctions.add(i, new Junction(connection, agents.get(i)));
		}
		for (int i = 0; i < agents.size(); i++) {
			learningAlgorithms.add(i, new LearningAlgorithm(junctions.get(i)));
		}
		

		while (connection.getTraciconnection().getCurrentSimStep() <= wholeStep) {

			Double nextstep = connection.getTraciconnection()
					.getCurrentSimStep() + step;

			while (connection.getTraciconnection().getCurrentSimStep() < nextstep) {

				try {
					connection.getTraciconnection().nextSimStep();
					
					for (int i = 0; i < agents.size(); i++) {
//						System.out.println("here1");
						junctions.get(i).calculateNumberVehiclesInDetectors();
						junctions.get(i).poolVehicles();
//						System.out.println("here2");
						
					}	
				} catch (IllegalStateException | IOException e) {
					e.printStackTrace();
				}

			}
			for (int i = 0; i < agents.size(); i++) {
				junctions.get(i).getEdgeNumberVehicle();
				
			}
			
			for (int i = 0; i < agents.size(); i++) {
				
				learningAlgorithms.get(i).QLearning();
			}
			Integer numVehicle =0;
			Double waitingTime=0.0;
			for (int i = 0; i < agents.size(); i++) {
				
				junctions.get(i).initializeNumberVehiclesInDetectors();
				numVehicle += junctions.get(i).getCurrentNumberVehicleInJunction();
				waitingTime+= junctions.get(i).calculateNumberofWaitingTimeInjunction();
			}
			
			data.getSteps().add((double)connection.getTraciconnection().getCurrentSimStep()/3600);
			data.getVehicles().add(numVehicle);
			data.getWaitingtime().add(waitingTime/numVehicle);
			
			
			
			
		}
		
		for (int i = 0; i < agents.size(); i++) {
			learningAlgorithms.get(i).getqTable().printQtable();
			Serializating sr = new Serializating(agents.get(i));
			sr.writeObject(learningAlgorithms.get(i).getqTable());
		}
		
		serial.writeObject(data);
		connection.getTraciconnection().close();

	}
	
	public static void main(String[] args) {
		try {
			new Magic();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
