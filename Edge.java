public class Edge {

    String color;
    Room from;
    Room to;

    public Edge(String c, Room f, Room t){
        color = c;
        from = f;
        to = t;
    }

    @Override
    public String toString() {
        String s = " I am an edge between " + from.roomNumber + " and " + to.roomNumber + " with color " + color;
        return s;
    }
    
}
