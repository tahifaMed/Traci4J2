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

import java.util.ArrayList;
import java.util.List;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class CollectDataAgent extends Agent {

	private List<String> agents = new ArrayList<>();
	private List<String> localagents = new ArrayList<>();
	private Integer numAgent = 4;
	private Integer numVehicle=0;
	private Integer currentstep=0;
	private String file = new String();
	//private final XYSeries series = new XYSeries("Number Of vehicles");
	private Data data;
	private Serializating serial;

	@Override
	protected void setup() {
		file = (String) getArguments()[0];
		for (int i = 0; i < numAgent; i++) {

			agents.add("agent" + (i + 1));
		}
		localagents.addAll(agents);
		 this.data = new Data();
		 this.serial = new Serializating(file);
		addBehaviour(new CollectDataBehavior());

	}

	class CollectDataBehavior extends Behaviour {

		@Override
		public void action() {
			MessageTemplate mt1 = MessageTemplate
					.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message1 = myAgent.receive(mt1);
			
			if (message1 != null){
				try {
					numVehicle += ((Data) message1.getContentObject()).getNumberVehicle();
					currentstep = ((Data) message1.getContentObject()).getCurrentstep();
					//System.out.println(message1.getSender().getLocalName()+"  "+numVehicle);
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				localagents.remove(message1.getSender().getLocalName());
			}
			
			else if (localagents.isEmpty()) {
				System.out.println(currentstep+"  "+numVehicle);
				
				//series.add(currentstep, numVehicle);
				data.getSteps().add((double)currentstep);
				data.getVehicles().add(numVehicle);
				numVehicle=0;
				localagents.addAll(agents);
			}

		}

		@Override
		public boolean done() {
			MessageTemplate mt1 = MessageTemplate
					.MatchPerformative(ACLMessage.FAILURE);
			ACLMessage message1 = myAgent.receive(mt1);
			
			if (message1 != null){
				serial.writeObject(data);
			}
			return false;
		}

	}
}
