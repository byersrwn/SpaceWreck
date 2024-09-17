import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import javax.swing.RowFilter.Entry;

public class SpaceWreck {

    String filename;
    int numberOfRooms;
    int numberOfEdges;
    ArrayList<Room> rooms;
    ArrayList<Edge> edges;
    ArrayList<String> successPaths;
    int lStart;
    int rStart;
    HashMap<Room, ArrayList<Edge>> gameBoard;
    HashMap<State, ArrayList<StateEdge>> stateGraph;

    State winState;
    ArrayList<State> winStates;

    State startState;
    HashMap<State, State> parentTable;
    HashMap<State, Integer> distance;

    public SpaceWreck(String filename){
        this.filename = filename;
        rooms = new ArrayList<>();
        edges = new ArrayList<>();
        gameBoard = new HashMap<>();
        stateGraph = new HashMap<>();
        this.winState = new State(true);
        winStates = new ArrayList<>();
        successPaths = new ArrayList<>();
    }

    public boolean readFile(){
        try{
            String data;
            String[] splitData;
            File f = new File(this.filename);
            Scanner s = new Scanner(f);
            // Room number and edge number (line 1)
            data = s.nextLine();
            splitData = data.split(" ");
            numberOfRooms = Integer.parseInt(splitData[0]);
            numberOfEdges = Integer.parseInt(splitData[1]);

            if(numberOfEdges == 0 || numberOfRooms == 0){
                System.out.println("NO PATH");
                return false;
            }

            // Color Room line (line 2)
            data = s.nextLine();
            splitData = data.split(" ");
            for(int i = 0 ; i < splitData.length ; i++){
                rooms.add(new Room(splitData[i], i + 1));
            }
            rooms.add(new Room("WIN", splitData.length + 1)); // Add final room that is win room
            
            // L and R start (line 3)
            data = s.nextLine();
            splitData = data.split(" ");
            rStart = Integer.parseInt(splitData[0]);
            lStart = Integer.parseInt(splitData[1]);
            
            // Remaining lines: edge creation
            while(s.hasNextLine()){
                data = s.nextLine();
                splitData = data.split(" ");
                edges.add(new Edge(splitData[2], rooms.get(Integer.parseInt(splitData[0]) - 1), 
                rooms.get(Integer.parseInt(splitData[1]) - 1)));
            }
            
            rooms.get(lStart - 1).inhabitantList.add("L");
            rooms.get(rStart - 1).inhabitantList.add("R");

        }catch(FileNotFoundException e){

        }
        
        return true;

    }

    public void createGameBoard() {
        // Initiate gameboard with all Rooms as keys and just initiate new arraylsit for edges
        for(int i = 0 ; i < rooms.size() ; i++){
            this.gameBoard.put(rooms.get(i), new ArrayList<>());
        }
        // put all edges into room list based on "from" room
        for(int i = 0 ; i < edges.size() ; i++){
            Edge curr = edges.get(i);
            this.gameBoard.get(curr.from).add(curr);
        }
    }

    public void makeStateGraph(){
        this.startState = new State("L" + this.lStart, "R" + this.rStart);
        this.stateGraph.put(startState, new ArrayList<>()); //input start state as first state in graph

        Room luckyCurrRoom, rocketCurrRoom;
        ArrayList<Edge> luckyEdgeList, rocketEdgeList;

        Stack<State> remainingState = new Stack<>();
        //Queue<State> remainingState = new ArrayDeque<>();
        remainingState.add(startState);
        while(remainingState.isEmpty() == false){
            State currState = remainingState.pop();
            //System.out.println(remainingState.size());
            luckyCurrRoom = this.rooms.get(Integer.parseInt(currState.LuckyState.substring(1)) - 1);//NEed to parese room num correctly
            rocketCurrRoom = this.rooms.get(Integer.parseInt(currState.RocketState.substring(1)) - 1);
            luckyEdgeList = gameBoard.get(luckyCurrRoom);
            rocketEdgeList = gameBoard.get(rocketCurrRoom);

            ArrayList<State> newStatesLucky = runThroughEdgeList(luckyEdgeList, rocketCurrRoom, currState, "L", "R");
            ArrayList<State> newStatesRocket = runThroughEdgeList(rocketEdgeList, luckyCurrRoom, currState, "R", "L");
            remainingState.addAll(newStatesLucky);
            remainingState.addAll(newStatesRocket);
            //printStateGraph();
        }

        Set<State> set = new HashSet<>(this.winStates);
        this.winStates.clear();
        this.winStates.addAll(set);
            
    }

    public ArrayList<State> runThroughEdgeList(ArrayList<Edge> edgeList, Room compareRoom, State currState, String currCharacter, String toChar){
        ArrayList<State> newStatesToCheckList = new ArrayList<>();
        for(int i = 0 ; i < edgeList.size() ; i++){
            Edge currEdge = edgeList.get(i);
            if(currEdge.color.equals(compareRoom.color)){
                String newStr = currCharacter + currEdge.to.roomNumber;
                State newState;
                if(currCharacter == "L"){
                     newState = new State(newStr, toChar + compareRoom.roomNumber);
                }
                else{
                     newState = new State(toChar + compareRoom.roomNumber, newStr);
                }
                StateEdge newMove = new StateEdge(newStr, currState, newState);
                int winflag = 0;
                if(currEdge.to.roomNumber == rooms.size()){ // win state found
                    StateEdge winMove = new StateEdge("W", newState, this.winState);
                    ArrayList<StateEdge> temp = new ArrayList<>();
                    temp.add(winMove);
                    this.stateGraph.put(newState, temp);
                    winflag = 1;
                }
                
                int existingKeyInMap = 0;
                if(this.stateGraph.keySet().contains(newState)){
                        newMove.to = newState;
                        this.stateGraph.put(newState, this.stateGraph.get(newState));
                        existingKeyInMap = 1;
                        break;
                }
                this.stateGraph.get(currState).add(newMove);
                if(winflag == 1){
                    if(this.winStates.size() == 0){
                        this.winStates.add(newState);
                    }
                    else{
                        boolean winInList = winListCompare(newState);
                        if(!winInList){
                            this.winStates.add(newState);
                        }
                    }
                    
                    winflag = 0;
                }
                if(existingKeyInMap == 0){
                    this.stateGraph.put(newState, new ArrayList<>());
                    newStatesToCheckList.add(newState);
                }
            }
        }
        return newStatesToCheckList;
    }

    public void printAllStates(){
        ArrayList<String> s = new ArrayList<>();
        String t = "";
        for(int i = 1 ; i <= 8 ; i++){
            for(int j = 1 ; j <= 8 ; j++){
                t += "L" + String.valueOf(i) + "R" + String.valueOf(j) + "\n";
            }
        }
        System.out.println(t);
    }

    public boolean winListCompare(State s1){
        for(int i = 0 ; i < this.winStates.size() ; i++){
            if(s1.compareState(this.winStates.get(i))){
                return true;
            }
        } 
        return false;
    }
    public void BFS(){ //create data structure of State to parent State.
        Queue<State> queue = new ArrayDeque<>();
        HashMap<State, State> parents = new HashMap<>();
        HashMap<State, Integer> distanceMap = new HashMap<>();
        distanceMap.put(this.startState, 0);
        this.startState.discovered = "Discovered";
        queue.add(this.startState);

        while(!queue.isEmpty()){
            //
            State curr = queue.poll();
            ArrayList<StateEdge> possibleMoves = this.stateGraph.get(curr);
            possibleMoves = getLexoNext(possibleMoves);
            for(StateEdge se : possibleMoves){
                if(se.to.discovered.equals("Undiscovered")){
                    se.to.discovered = "Discovered";
                    parents.put(se.to, curr); // Map child to parent node
                    distanceMap.put(se.to, distanceMap.get(curr) + 1); //put distance in for each state
                    queue.add(se.to);
                }
            }
            curr.discovered = "Processed";
        }
        this.distance = distanceMap;
        this.parentTable = parents;
    }

    public ArrayList<StateEdge> getLexoNext(ArrayList<StateEdge> posibleMoves){
        ArrayList<String> addingChar = new ArrayList<>();
        ArrayList<String> lexInOrder = new ArrayList<>();
        ArrayList<StateEdge> lexEdges = new ArrayList<>();

        for(StateEdge se : posibleMoves){
            addingChar.add(se.change + "R");
        }
        for(int i = 0 ; i < addingChar.size() ; i++){
        }
        LexComparator lexCom = new LexComparator(); 
        Collections.sort(addingChar, lexCom);

        for(int i = 0 ; i < addingChar.size() ; i++){
        }
        for(int i = 0 ; i < addingChar.size() ; i++){
            String curr = addingChar.get(i);
            lexInOrder.add(curr.substring(0,curr.length() - 1));
        }

        for(int i = 0 ; i < lexInOrder.size() ; i++){
            String currChange = lexInOrder.get(i);
            for(int j = 0 ; j < posibleMoves.size() ; j++){
                if(posibleMoves.get(j).change.equals(currChange)){
                    lexEdges.add(posibleMoves.get(j));
                }
            }
        }

        return lexEdges;
    }

    public static HashMap<State,Integer> sortByDist(HashMap<State,Integer> dist){
        List<Map.Entry<State, Integer>> list = new LinkedList<Map.Entry<State, Integer>>(dist.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<State, Integer>> () {
            public int compare(Map.Entry<State, Integer> one,
                                Map.Entry<State, Integer> two){
                                    return (one.getValue().compareTo(two.getValue()));
                                }
        });
        HashMap<State, Integer> temp = new LinkedHashMap<>();
        for(Map.Entry<State, Integer> a : list) {
            temp.put(a.getKey(), a.getValue());
        }
        return temp;
    }

    public ArrayList<String> getShortestPaths(){
        ArrayList<String> paths = new ArrayList<>();
        for(int i = 0 ; i < this.winStates.size() ; i++){
            State curr = this.winStates.get(i);
            StringBuilder sb = new StringBuilder();
            while(this.parentTable.containsKey(curr)){
                    State par = this.parentTable.get(curr);
                    String move = stateChange(par.LuckyState + par.RocketState , curr.LuckyState + curr.RocketState);
                    sb.append(move);
                    curr = par;
            }
            paths.add(reverseList(sb.toString()));
        }
        return paths;
    }

    public String reverseList(String s){
        StringBuilder sb = new StringBuilder();
        String temp = "";
        Stack<String> stack = new Stack<>();
        for(int i = s.length() - 1 ; i > 0 ; i--){
            for(int j = i ; j > i - 4 ; j--){
                char t = s.charAt(j);
                if(t == 'L' || t == 'R'){
                    stack.add(t + "");
                    i = j;
                    break;
                }
                stack.add(t + "");
            }
            while(stack.isEmpty() == false){
                temp+=stack.pop();
            }
            sb.append(temp);
            temp="";
        }

        return sb.toString();
    }

    public String stateChange(String s1, String s2){ //parent and then child
        int RindexOne = s1.indexOf('R');
        int RindexTwo = s2.indexOf('R');
        String rNumTwo= s2.substring(RindexOne);

        String lNumOne = s1.substring(0, RindexOne);
        String lNumTwo = s2.substring(0, RindexTwo);

        if(!lNumOne.equals(lNumTwo)){
            return lNumTwo;
        }

        return rNumTwo;

    }

    public ArrayList<String> retMinListOfPaths(ArrayList<String> input){
        int smallest = input.get(0).length();
        ArrayList<String> smallestPaths = new ArrayList<>();
        for(int i = 0 ; i < input.size() ; i++){
            int check = input.get(i).length();
            if(check < smallest){
                smallest = check;
            }
        }

        for(int i = 0 ; i < input.size() ; i++){
            if(input.get(i).length() == smallest){
                smallestPaths.add(input.get(i));
            }
        }
        return smallestPaths;
    }

    public void printDistanceMap(){
        StringBuilder sb = new StringBuilder();
        sb.append("---------Distance Map--------\n");
        for(State s : this.distance.keySet()){
            sb.append(s.toString() + " ---> " + this.distance.get(s) + "\n");
        }
        System.out.println(sb.toString());
    }

    public void printParentTable(){
        StringBuilder sb = new StringBuilder();
        sb.append("CHILD ----> PARENT\n");
        for(State s : this.parentTable.keySet()){
            sb.append(s.toString() + " -> " + this.parentTable.get(s).toString() + "\n");
        }
        System.out.println(sb.toString());
    }

    public void printStateGraph(){
        StringBuilder sb = new StringBuilder();
        for(State s : this.stateGraph.keySet()){
            sb.append(s.toString() + " with moves listed below:\n");
            ArrayList<StateEdge> currList = this.stateGraph.get(s);
            for(int i = 0 ; i < currList.size() ; i++){
                sb.append(" " + currList.get(i).toString() + "\n");
            }
        }
        System.out.println("------------------------NEW STATE PRINT----------------- SIZE OF: " + this.stateGraph.size());
        System.out.println(sb.toString());
    }

    public void allRoomPrint(){ // Print all rooms after creation
        for(int i = 0 ; i < rooms.size() ; i++){
            System.out.println(rooms.get(i).toString());
        }
    }

    public void printStateGraphString(){
        StringBuilder sb = new StringBuilder();
        for(State s : this.stateGraph.keySet()){
            for(StateEdge subState : this.stateGraph.get(s)){
                sb.append(s.LuckyState + s.RocketState + "->" + subState.to.LuckyState + 
                subState.to.RocketState + "\n");
            }
        }
        System.out.println(sb.toString());
    }

    public void printGameBoard(){
        StringBuilder sb = new StringBuilder();
        for(Room r : this.gameBoard.keySet()){
            sb.append("For room " + r.roomNumber + " I have the following conencting edge list:\n");
            ArrayList<Edge> edgeList = this.gameBoard.get(r);
            for(int i = 0 ; i < edgeList.size(); i++){
                sb.append(edgeList.get(i).toString() + "\n");
            }
        }

        System.out.println(sb.toString());
    }

    public void allEdgePrint(){
        // Print all edges
            for(int i = 0 ; i < edges.size() ; i++){
                System.out.println(edges.get(i).toString());
            }
    }

    public void winStateListPrint(){
        System.out.println("-------------SW WIN STATES--------------- SIZE OF WIN IS: " + winStates.size());
        for(int i = 0 ; i < winStates.size() ; i++){
            System.out.println("SW WIN STATE: " + i + " " + winStates.get(i).toString());

        }
    }
    
}
