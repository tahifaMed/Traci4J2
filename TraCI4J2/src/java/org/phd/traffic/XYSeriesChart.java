package org.phd.traffic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class XYSeriesChart extends ApplicationFrame {

    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
    public XYSeriesChart(final String title,DataChart dataschart) {

        super(title);
        
        List<XYSeries> series = new ArrayList<>();
        Double wait=0.0;
        for (int i = 0; i < dataschart.getDatas().size(); i++) {
        	 XYSeries serie = new XYSeries(dataschart.getNames().get(i));
        	 Data data = dataschart.getDatas().get(i);
        	 
        	 for (int j = 0; j < data.getSteps().size(); j++) {
             	wait = (data.getWaitingtime().get(j)/100);
     			serie.add(data.getSteps().get(j),wait);
     			
     		}
        	 series.add(serie);
        	
		}
//        final XYSeries series1 = new XYSeries("withoutLearning");
//        final XYSeries series2 = new XYSeries("withLearning");
//        Double wait=0.0;
//        for (int i = 0; i < datachart1.getSteps().size(); i++) {
//        	wait = (datachart1.getWaitingtime().get(i)/100);
//			series1.add(datachart1.getSteps().get(i),wait);
//			
//		}
//        
//        for (int i = 0; i < datachart2.getSteps().size(); i++) {
//        	
//        	wait = (datachart2.getWaitingtime().get(i)/100);
//        	System.out.println(wait);
//			series2.add(datachart2.getSteps().get(i),wait);
//		}
        
        
        
        final XYSeriesCollection data = new XYSeriesCollection();
        for (int i = 0; i < series.size(); i++) {
        	data.addSeries(series.get(i));
		}
//        data.addSeries(series1);
//        data.addSeries(series2);
        final JFreeChart chart = ChartFactory.createXYLineChart(
            "waiting Time(minute/km)",
            "time/hour", 
            "waitingTime/minute", 
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 800));
        setContentPane(chartPanel);
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);

    }
    
//    public static void main(String[] args) {
//    	
//    	Map<Integer, Integer> data = new HashMap<Integer, Integer>();
//    	data.put(1, 33);
//    	data.put(2, 55);
//    	System.out.println("here");
//		XYSeriesChart demo =new XYSeriesChart("traffic Congestion", data);
//		demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
//	}

   
}