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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jade.util.leap.Serializable;

public class Data implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private  Integer currentstep;
	private  Integer numberVehicle;
	private List<Double> steps ;
	private List<Integer> vehicles;
	private List<Double> waitingtime;
	
	
	public Data() {
		this.currentstep=0;
		this.numberVehicle=0;
		this.steps = new ArrayList<>();
		this.vehicles = new ArrayList<>();
		this.waitingtime = new ArrayList<>();
		
	}
	
	public Data(Integer numberVehicle,Integer currentstep) {
		this.currentstep= currentstep;
		this.numberVehicle = numberVehicle;
		this.steps = new ArrayList<>();
		this.vehicles = new ArrayList<>();
		this.waitingtime = new ArrayList<>();
	}


	public Integer getCurrentstep() {
		return currentstep;
	}


	public void setCurrentstep(Integer currentstep) {
		this.currentstep = currentstep;
	}


	public Integer getNumberVehicle() {
		return numberVehicle;
	}


	public void setNumberVehicle(Integer numberVehicle) {
		this.numberVehicle = numberVehicle;
	}

	public List<Double> getSteps() {
		return steps;
	}

	public void setSteps(List<Double> steps) {
		this.steps = steps;
	}

	public List<Integer> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<Integer> vehicles) {
		this.vehicles = vehicles;
	}

	public List<Double> getWaitingtime() {
		return waitingtime;
	}

	public void setWaitingtime(List<Double> waitingtime) {
		this.waitingtime = waitingtime;
	}

	
	
	
	

}
