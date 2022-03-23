
import java.util.Map.Entry;
import java.util.*;


public class Map {
    public static ArrayList<Road> roads;
    public static HashMap<String, Intersection> intersectionMap;
    public static ArrayList<Road> minWeightSpanTree;
    public static Intersection [] dijkstraPath;
    public static PriorityQueue<Intersection> unknownIntersectionsHeap;
    public static PriorityQueue<Road> kruskalsRoads;
    public static HashMap<String, HashSet<String>> intersectionSets;


    public HashMap<String, LinkedList> graph;
    public static int numIntersections;

    public static double minLat, maxLat, minLong, maxLong;

    public Map(int numVertices) {

        graph = new HashMap<>();

        numIntersections = numVertices;
        roads = new ArrayList<>();
        intersectionMap = new HashMap<>();

        Comparator<Road> comparator2 = new Comparator<Road>() {  //Road comparator

            @Override
            public int compare(Road r1, Road r2) {

                if(r1.distance < r2.distance) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        };

        Comparator<Intersection> comparator = new Comparator<Intersection>() { //intersection comparator

            @Override
            public int compare(Intersection i1, Intersection i2) {

                if(i1.distance < i2.distance) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
        };


        unknownIntersectionsHeap = new PriorityQueue<>(numVertices, comparator);
        kruskalsRoads = new PriorityQueue<>(numVertices*3, comparator2);


        minLat = minLong = Integer.MAX_VALUE;
        maxLat = maxLong = Integer.MIN_VALUE;
        //This minLat and maxLat will help us in computing the distances of each intersection

    }


    public static double dijkstraPathLength() {
        return dijkstraPath[0].distance * 0.000621371;          //I want to deal with miles not meters
    }

    public static Intersection smallestUnknownVertex() {
        Intersection temp = unknownIntersectionsHeap.remove();

        return intersectionMap.get(temp.IntersectionID);

    }

   
    public void kruskals() {


        createSet();


        minWeightSpanTree = new ArrayList<Road>();

        Road currentRoad;


        HashSet<String> u;
        HashSet<String> v;


        while(kruskalsRoads.size() > 0) {


            currentRoad = kruskalsRoads.remove();

            u = intersectionSets.get(currentRoad.intersect1);
            v = intersectionSets.get(currentRoad.intersect2);

            if(!u.equals(v)) {

                minWeightSpanTree.add(currentRoad);

                u.addAll(v);

                for(String intersectionID: u) {
                    intersectionSets.put(intersectionID, u);
                }
            }
        }
    }

    public static String formPath(String endID) {
        
        Intersection temp = intersectionMap.get(endID);

        String [] path = new String[intersectionMap.size()];

        int counter = 0;


        while(temp.path != null) {
            path[counter] = temp.IntersectionID;
            temp = temp.path;
            counter++;
        }


        path[counter] = temp.IntersectionID;

        int totalPath = 0;

        for(int i = 0; i < path.length; i++) {
            if(path[i] == null) {
                totalPath = i;
                break;
            }
        }


        dijkstraPath = new Intersection [totalPath];

        for(int i = 0; i < totalPath; i++) {
            dijkstraPath[i] = intersectionMap.get(path[i]);
        }

        String finalPath = "";


        for(int i = counter ; i > -1; i--) {
            finalPath = finalPath + path[i] + "\n";
        }

        return finalPath;
    }


    public void createSet() {

        intersectionSets = new HashMap<>();

        HashSet<String> intersections;

        Iterator<Entry<String, LinkedList>> iterator = graph.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, LinkedList> pair =iterator.next();


            intersections = new HashSet<>();



            intersections.add(pair.getKey());

            intersectionSets.put(pair.getKey(), intersections);

        }


    }

    public void dijkstra(String intersectionID) {
        Intersection start = intersectionMap.get(intersectionID);

        start.distance = 0;

        unknownIntersectionsHeap.remove(start);





        unknownIntersectionsHeap.add(start);

        double cost;


        int numUnknownVertices = intersectionMap.size();

        while(numUnknownVertices > 0) {


            Intersection temp = smallestUnknownVertex();

            temp.known = true;
            numUnknownVertices--;


            LinkedList currentVertex = graph.get(temp.IntersectionID);


            Edge currentRoad = currentVertex.head.edge;
            Intersection currentIntersection;


            while(currentRoad != null) {


                if(currentRoad.road.intersect1.equals(temp.IntersectionID)) {
                    currentIntersection = intersectionMap.get(currentRoad.road.intersect2);
                }
                else {
                    currentIntersection = intersectionMap.get(currentRoad.road.intersect1);
                }


                if(currentIntersection.known == false) {


                    cost = findCost(temp, currentIntersection);

                    if(temp.distance + cost < currentIntersection.distance) {


                        unknownIntersectionsHeap.remove(currentIntersection);


                        currentIntersection.distance = temp.distance + cost;
                        currentIntersection.path = temp;


                        unknownIntersectionsHeap.add(currentIntersection);
                    }
                }

                currentRoad = currentRoad.next;
            }
        }
    }


    public double findCost(Intersection int1, Intersection int2) {


        LinkedList temp = graph.get(int1.IntersectionID);


        return temp.findCost(int2);
    }



    public static Intersection intersectLookup(String intersectID) {

        return intersectionMap.get(intersectID);

    }


    public static double roadDist(Intersection int1, Intersection int2) {

        return calcDist(int1.latitude, int1.longitude, int2.latitude, int2.longitude);

    }

    //Calulating distance between two intersection
    public static double calcDist(double lat1, double long1, double lat2, double long2) {

        int earthRadius = 6371000;

        lat1 = Math.toRadians(lat1);
        long1 = Math.toRadians(long1);
        lat2 = Math.toRadians(lat2);
        long2 = Math.toRadians(long2);

        double changeLat = lat2-lat1;
        double changeLong = long2-long1;

        double a = (Math.sin(changeLat/2) * Math.sin(changeLat/2)) + (Math.cos(lat1) * Math.cos(lat2) * Math.sin(changeLong/2) * Math.sin(changeLong/2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadius * c;

    }

    public void insert(Intersection i) {



        if(i.latitude < minLat) {
            minLat = i.latitude;
        }

        if(i.latitude > maxLat) {
            maxLat = i.latitude;
        }

        if(i.longitude < minLong) {
            minLong = i.longitude;
        }

        if(i.longitude > maxLong) {
            maxLong = i.longitude;
        }


        intersectionMap.put(i.IntersectionID, i);


        unknownIntersectionsHeap.add(i);


        LinkedList temp = new LinkedList();


        temp.insert(i);


        graph.put(i.IntersectionID, temp);
    }


    public void insert(Road e) {


        LinkedList int1 = graph.get(e.intersect1);
        LinkedList int2 = graph.get(e.intersect2);


        int1.insert(e);
        int2.insert(e);


        kruskalsRoads.add(e);


        roads.add(e);
    }

}
