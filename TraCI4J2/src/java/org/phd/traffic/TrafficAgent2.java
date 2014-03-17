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

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TrafficAgent2 extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String agentName;
	private Junction junction;
	private Connection connection;
	private LearningAlgorithm learningAlgorithm;
	private Serializating serial;

	@Override
	protected void setup() {
		agentName = (String) getArguments()[0];
		this.connection = Connection.getInstance();
		//this.serial = new Serializating(this.agentName);
		addBehaviour(new InitiliazeBehavior());
		addBehaviour(new learnBehavior());
		// addBehaviour(new QTableBehaviour());
	}

	class InitiliazeBehavior extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Boolean flag = false;

		@Override
		public void action() {

			MessageTemplate mt = MessageTemplate
					.MatchPerformative(ACLMessage.CONFIRM);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				try {
					System.out.println("connexTrafficAgent : " + connection
							+ " " + agentName);
					junction = new Junction(connection, agentName);

					learningAlgorithm = new LearningAlgorithm(junction,agentName);

				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				ACLMessage message = new ACLMessage(ACLMessage.AGREE);
				message.addReceiver(new AID("centralagent", AID.ISLOCALNAME));

				message.setLanguage("English");
				message.setContent("startlearn");
				//System.out.println(message);
				send(message);
				flag = true;

			}

		}

		@Override
		public boolean done() {
			if (flag == true) {

				return true;
			}
			return false;
		}
	}

	class learnBehavior extends Behaviour {

		private static final long serialVersionUID = 1L;
		private Boolean flag1 = false;

		@Override
		public void action() {
			MessageTemplate mt1 = MessageTemplate
					.MatchPerformative(ACLMessage.FAILURE);
			ACLMessage messagestop = myAgent.receive(mt1);
			if (messagestop != null) {
				addBehaviour(new QTableBehaviour());
				flag1 = true;
			} else {

				MessageTemplate mt = MessageTemplate
						.MatchPerformative(ACLMessage.INFORM);
				ACLMessage msg = myAgent.receive(mt);

				if (msg != null) {

					if (msg.getContent().compareTo("learn") == 0) {

						try {
							Integer numVehicle=0;
							//learningAlgorithm.QLearning();
							learningAlgorithm.exploitQlearning();
							synchronized (Junction.class) {
								junction.getEdgeNumberVehicle();
								numVehicle = junction.getCurrentNumberVehicleInJunction();
							}
							
							Data data = new Data(numVehicle, connection.getTraciconnection().getCurrentSimStep());
							ACLMessage message = new ACLMessage(ACLMessage.INFORM);
							message.addReceiver(new AID("dataagent", AID.ISLOCALNAME));
							
							message.setLanguage("English");
							message.setContentObject(data);
							send(message);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					ACLMessage message = new ACLMessage(ACLMessage.AGREE);
					message.addReceiver(new AID("centralagent", AID.ISLOCALNAME));

					message.setLanguage("English");
					message.setContent("startlearn");
					send(message);
				}
			}

		}

		@Override
		public boolean done() {
			if (flag1 == true){
				//System.out.println("trafficAgent Done");
				return true;
			}
				
			return false;

		}

	}

	class QTableBehaviour extends OneShotBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			synchronized (LearningAlgorithm.class) {
				System.out.println("agentName : " + agentName);
				learningAlgorithm.getqTable().printQtable();
				//serial.writeObject(learningAlgorithm.getqTable());
				
			}
			
		}

	}
}
