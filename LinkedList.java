
public class LinkedList {




    public int size;
    public Node head;


    public LinkedList() {
        head = new Node();
        size = 0;
    }



    public double findCost(Intersection int2) {

        Edge temp2 = head.edge;


        while(temp2 != null) {

            if(temp2.road.intersect1.equals(int2.IntersectionID) || temp2.road.intersect2.equals(int2.IntersectionID)) {
                return temp2.road.distance;
            }

            temp2 = temp2.next;
        }

        return -1;

    }

    public void insert(Road road) {

        Edge tempEdge = new Edge();
        tempEdge.road = road;

        tempEdge.next = head.edge;
        head.edge = tempEdge;


    }

    public void insert(Intersection intersect) {

        if(head.intersection == null) {
            head.intersection = intersect;
        }

        size++;
    }


}
