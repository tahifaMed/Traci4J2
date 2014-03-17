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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test2 {

	public static void main(String[] args) {
		FileInputStream fis;
		FileOutputStream fos;
			try {
				fis = new FileInputStream("AdaptEnv2");
				ObjectInputStream ois1 = new ObjectInputStream(fis);
				Data d = (Data) ois1.readObject();
				List<Double> steps = new ArrayList();
//				d.getWaitingtime().remove(118);
//				d.getWaitingtime().add(118, 450.0);
//				d.getWaitingtime().remove(119);
//				d.getWaitingtime().add(119, 450.0);
////				d.getWaitingtime().remove(201);
////				d.getWaitingtime().add(201, 600.0);
////				d.getWaitingtime().remove(202);
////				d.getWaitingtime().add(202, 600.0);
				
				for (int i = 0; i < d.getWaitingtime().size(); i++) {
//					if(i>185){
//						Double k = d.getWaitingtime().get(i);
//						d.getWaitingtime().remove(i);
//						d.getWaitingtime().add(i, k - 200.0);
//						
//					}
					System.out.println(d.getWaitingtime().get(i) +"  "+d.getSteps().get(i)*3600);
					//d.getSteps().get(i)= ;
					//steps.add(d.getSteps().get(i)*3600);
					
				}
				//d.setSteps(steps);
				ois1.close();
				fis.close();
//				fos = new FileOutputStream("AdaptEnv2");
//				ObjectOutputStream ois = new ObjectOutputStream(fos);
//				ois.writeObject(d);
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				}

}
