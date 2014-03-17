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

import it.polito.appeal.traci.SumoTraciConnection;

public class Connection {
	
	
	
	private static final String SIM_CONFIG_LOCATION = "C:/Users/Tahifa med/Desktop/Traci4J/traffic1/data/hello.sumocfg";
	private  SumoTraciConnection traciconnection;
	private  static Connection connection = new Connection();
	private Connection() {
		System.setProperty("it.polito.appeal.traci.sumo_exe",
					"C:/Users/Tahifa med/Downloads/sumo-0.19.0/bin/sumo-gui");
		traciconnection = new SumoTraciConnection(SIM_CONFIG_LOCATION,
				12345);

	}
	
	public SumoTraciConnection getTraciconnection() {
		return traciconnection;
	}

	public static Connection getInstance(){
		return  connection;
	}
	

}
