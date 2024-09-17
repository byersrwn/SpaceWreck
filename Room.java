import java.util.ArrayList;

public class Room {

    ArrayList<String> inhabitantList;
    String color;
    int roomNumber;

    public Room(String c, int rm){
        color = c;
        this.inhabitantList = new ArrayList<>();
        this.roomNumber = rm;
    }

    public void inhabitantPrint(){
        for(int i = 0 ; i < inhabitantList.size() ; i++){
            System.out.println("I am room: " + this.roomNumber + " with inhabitant(s) " + inhabitantList.get(i));
        }
    }

    @Override
    public String toString() {
        String s = "I am room " + roomNumber + " with color " + color + " and number of inhabitants " 
        + inhabitantList.size();
        return s;
    }
    
}
