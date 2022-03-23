import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFrame;


public class Main{

    public static void main(String [] args) throws FileNotFoundException {
        boolean useThickerLines = false;

        long startTime = System.currentTimeMillis();//start the timer


        File mapData = new File(args[0]);//get the name of the file

        if(args[0].equals("ur.txt")){
            useThickerLines = true;  //U of R map is pretty small, so use big lines to draw
        }



        //first scanner finds the number of intersections in the entire graph
        Scanner scan = new Scanner(mapData);

        int numIntersects = 0;

        while(scan.nextLine().startsWith("i")) {
            numIntersects++;
        }

        scan.close();

        String intersectionID;
        double lat, longitude;
        Intersection v;

        //scan2 scans through all the data
        Scanner scan2 = new Scanner(mapData);

        //create the Map
        Map map = new Map(numIntersects);

        String currentLine = scan2.nextLine();

        String [] info;

        //INSERTING INTERSECTIONS
        while(currentLine.startsWith("i")) {

            info = currentLine.split("\t");

            intersectionID = info[1];
            lat = Double.parseDouble(info[2]);
            longitude = Double.parseDouble(info[3]);

            //create the new Intersection
            v = new Intersection();
            v.distance = Integer.MAX_VALUE;
            v.IntersectionID = intersectionID;
            v.latitude = lat;
            v.longitude = longitude;
            v.known = false;

            currentLine = scan2.nextLine();

            //add the new intersection into the map
            map.insert(v);
        }

        String roadID, int1, int2;

        Intersection w, x;

        double distance;

        //INSERTING ROADS
        while(currentLine.startsWith("r")) {

            info = currentLine.split("\t");

            roadID = info[1];

            int1 = info[2];
            int2 = info[3];

            w = Map.intersectLookup(int1);
            x = Map.intersectLookup(int2);

            distance = Map.roadDist(w, x);

            //create and add the new road
            map.insert(new Road(roadID, int1, int2, distance));

            if(scan2.hasNextLine() == false) {
                break;
            }

            currentLine = scan2.nextLine();

        }


        String fileName;

        //used for title of JFrame
        if(args[0].equals("ur.txt")) {
            fileName = "U of R Campus Map";
        }
        else {
            if(args[0].equals("monroe.txt")) {
                fileName = "Map of Monroe County";
            }
            else {
                if(args[0].equals("nys.txt")) {
                    fileName = "Map of New York State";
                }
                else {
                    fileName = "Map";
                }
            }
        }

        boolean showMap = false;              //show the map
        boolean dijkstras = false;          //want to see the shorttest path
        boolean mwst = false;               // want to see minimu weight tree


        //This will help me to draw the map even when the end and start points haven't been given
        String directionsStart = "i27";
        String directionsEnd = "i80";

        //checks the command line arguments
        //I implemented this as given from the project paper
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("-show")) {
                showMap = true;
            }

            if(args[i].equals("-directions")) {
                dijkstras = true;

                directionsStart = args[i+1];
                directionsEnd = args[i+2];
            }

            if(args[i].equals("-meridianmap")) {
                mwst = true;
            }

        }




        if(mwst == true) {     //We want minimum spanning tree

            map.kruskals();

            System.out.println("\nRoads Taken to Create Minimum Weight Spanning Tree for " + fileName + ":\n");

            for(Road r : Map.minWeightSpanTree) {
                System.out.println(r.roadID);
            }

        }

        if(useThickerLines) {      //U of R map is small so use
            GUI.thickLines = true;
        }

        if(showMap == true) {       //We want to see the GUI

            JFrame frame = new JFrame("Map");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.getContentPane().add(new GUI(Map.roads, Map.intersectionMap, Map.minLat, Map.maxLat, Map.minLong, Map.maxLong));
            frame.pack();
            frame.setVisible(true);
        }

        if(dijkstras == true) {     //If we need shortest distance directions

            map.dijkstra(directionsStart);

            System.out.println("\nThe shortest path from " + directionsStart + " to " + directionsEnd + " is: ");
            System.out.println(Map.formPath(directionsEnd));

            System.out.println("Length of the path from " + directionsStart + " to " + directionsEnd + " is: " + Map.dijkstraPathLength() + " miles.");
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime-startTime;

        System.out.println("\n\nTime required to do everything: " + elapsedTime/1000 + " seconds.");


        scan2.close();
    }

}