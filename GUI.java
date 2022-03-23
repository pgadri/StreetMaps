
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;

public class GUI extends JPanel {




    public static ArrayList<Road> roads;
    public static HashMap<String, Intersection> intersectionMap;
    public static boolean thickLines = false;

    public static double minLat, minLong, maxLat, maxLong;
    public static double xScale, yScale;

    public GUI(ArrayList<Road> roads, HashMap<String, Intersection> intersectMap, double minimumLat, double maximumLat, double minimumLong, double maximumLong) {

        GUI.roads = roads;
        GUI.intersectionMap = intersectMap;

        minLat = minimumLat;
        maxLat = maximumLat;
        minLong = minimumLong;
        maxLong = maximumLong;
        setBackground(Color.black);
        setPreferredSize(new Dimension(1000, 1000));

    }


    public void paintComponent(Graphics page) {

        Graphics2D page2 = (Graphics2D) page;
        super.paintComponent(page2);

        page2.setColor(Color.WHITE);

        if(thickLines) {
            page2.setStroke(new BasicStroke(3));
        }


        xScale = this.getWidth() / (maxLong - minLong);
        yScale = this.getHeight() / (maxLat - minLat);

        Intersection int1, int2;

        double x1, y1, x2, y2;

        for(Road r : roads) {       //painting the roads

            scale();

            int1 = intersectionMap.get(r.intersect1);
            int2 = intersectionMap.get(r.intersect2);

            x1 = int1.longitude;
            y1 = int1.latitude;
            x2 = int2.longitude;
            y2 = int2.latitude;

            page2.draw(new Line2D.Double((x1-minLong) * xScale, getHeight() - ((y1 - minLat) * yScale),
                    (x2-minLong) * xScale, getHeight() - ((y2 - minLat) * yScale)));

        }

        if(Map.minWeightSpanTree != null) {
            for(Road r : Map.minWeightSpanTree) {

                page2.setColor(Color.BLUE);

                int1 = intersectionMap.get(r.intersect1);
                int2 = intersectionMap.get(r.intersect2);

                x1 = int1.longitude;
                y1 = int1.latitude;
                x2 = int2.longitude;
                y2 = int2.latitude;

                page2.draw(new Line2D.Double((x1-minLong) * xScale, getHeight() - ((y1 - minLat) * yScale),
                        (x2-minLong) * xScale, getHeight() - ((y2 - minLat) * yScale)));

            }
        }

        if(Map.dijkstraPath != null) {          //shortest path

            page2.setColor(Color.RED);

            for(int i = 0; i < Map.dijkstraPath.length - 1; i++) {

                x1 = Map.dijkstraPath[i].longitude;
                y1 = Map.dijkstraPath[i].latitude;
                x2 = Map.dijkstraPath[i+1].longitude;
                y2 = Map.dijkstraPath[i+1].latitude;

                page2.draw(new Line2D.Double((x1-minLong) * xScale, getHeight() - ((y1 - minLat) * yScale),
                        (x2-minLong) * xScale, getHeight() - ((y2 - minLat) * yScale)));

            }


        }




    }

    public void scale() {       //rescaling

        xScale = this.getWidth() / (maxLong - minLong);
        yScale = this.getHeight() / (maxLat - minLat);

    }



}
