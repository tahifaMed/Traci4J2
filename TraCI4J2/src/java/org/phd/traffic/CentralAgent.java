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
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CentralAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String agentName;
	private Connection connection;
	private static final Integer wholeStep = 60000 /*1000*/;
	private List<String> agents = new ArrayList<>();
	private List<String> localagents = new ArrayList<>();
	private Integer numAgent = 4;

	@Override
	protected void setup() {
		agentName = (String) getArguments()[0];
		this.connection = Connection.getInstance();
		for (int i = 0; i < numAgent; i++) {

			agents.add("agent" + (i + 1));
		}
		localagents.addAll(agents);
		addBehaviour(new Behavior());

	}

	class Behavior extends OneShotBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {

			try {

				System.out.println("CentalAgent OneShotBehaviour");
				connection.getTraciconnection().runServer();
				System.out.println("run");
				ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
				for (int i = 0; i < agents.size(); i++) {
					msg.addReceiver(new AID(agents.get(i), AID.ISLOCALNAME));
				}

				

				msg.setContent("start");
				send(msg);
				System.out.println("messageSended");
				addBehaviour(new LearningBehavior());

			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	class LearningBehavior extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Double step = 100.0;
		private Boolean flag1 = false;

		@Override
		public void action() {
			MessageTemplate mt1 = MessageTemplate
					.MatchContent("continue");
			ACLMessage message1 = myAgent.receive(mt1);
			if (message1 != null && flag1==false ) {
				localagents.remove(message1.getContent());
			}

			else if (localagents.isEmpty()) {

				if (connection.getTraciconnection().getCurrentSimStep() >= wholeStep) {
					try {
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						for (int i = 0; i < agents.size(); i++) {
							msg.addReceiver(new AID(agents.get(i),
									AID.ISLOCALNAME));
						}
						
						msg.setContent("stop");
						send(msg);
						flag1 = true;
						System.out.println("flag :     "+flag1);
//						synchronized (Connection.class) {
//							connection.getTraciconnection().close();	
//						}
										
					} catch ( Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					
					ACLMessage msg3 = new ACLMessage(ACLMessage.INFORM);
					for (int i = 0; i < agents.size(); i++) {
						msg3.addReceiver(new AID(agents.get(i), AID.ISLOCALNAME));
					}
					msg3.setContent("stepMove");
					send(msg3);

					Double nextstep = connection.getTraciconnection()
							.getCurrentSimStep() + step;
					
					while (connection.getTraciconnection().getCurrentSimStep() < nextstep) {

						try {
							connection.getTraciconnection().nextSimStep();
							for (int i = 0; i < agents.size(); i++) {
								
							}
						} catch (IllegalStateException | IOException e) {
							e.printStackTrace();
						}

					}
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					for (int i = 0; i < agents.size(); i++) {
						msg.addReceiver(new AID(agents.get(i), AID.ISLOCALNAME));
					}
					msg.setContent("learn");
					send(msg);
					
					localagents.addAll(agents);
				}
			}

		}

		public boolean done() {
			
			if (flag1 == true) {
				System.out.println("learning Behavior Done");
				ACLMessage message = new ACLMessage(ACLMessage.FAILURE);
				message.addReceiver(new AID("dataagent", AID.ISLOCALNAME));
				
				message.setLanguage("English");
				message.setContent("Done");
				send(message);
				return true;
			}
			return false;

		}

	}

}
