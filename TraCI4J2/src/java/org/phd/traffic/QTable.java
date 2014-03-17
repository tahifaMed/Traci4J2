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

import jade.util.leap.Serializable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class QTable implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double Q[][];
	private Map<Integer, Integer> datas = new HashMap<Integer, Integer>();
	
	public QTable() {
		// TODO Auto-generated constructor stub
	}
	public QTable(int statesize,int actionsize) {
		Q = new Double[statesize][actionsize];
		for (int i = 0; i < Q.length; i++) {
			for (int j = 0; j < Q[i].length; j++) {
				Q[i][j] = 0.0;
			}
		}

	}
	public Double[][] getQ() {
		return Q;
	}
	public void setQ(Double[][] q) {
		Q = q;
	}
	
	public void printQtable() {

		for (int i = 0; i < Q.length; i++) {
			System.out.print(i+" |");
			for (int j = 0; j < Q[i].length; j++) {
				BigDecimal bd = new BigDecimal(Q[i][j]);
				bd = bd.setScale(2, BigDecimal.ROUND_UP);
				System.out.print(bd + " ");
			}
			System.out.println("|");
		}
	}
	public Map<Integer, Integer> getDatas() {
		return datas;
	}
	public void setDatas(Map<Integer, Integer> datas) {
		this.datas = datas;
	}

}
