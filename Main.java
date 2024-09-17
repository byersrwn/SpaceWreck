import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Main{
    public static void main(String[] args){
        SpaceWreck sw = new SpaceWreck("0-sample_in.txt");
        boolean possible = sw.readFile();
        if(!possible){
            return;
        }
        sw.createGameBoard();
        sw.makeStateGraph();
        sw.BFS();

        ArrayList<String> ret = sw.getShortestPaths();
        if(ret.size() == 0){
            System.out.println("NO PATH");
            return;
        }
        ret = sw.retMinListOfPaths(ret);

        Collections.sort(ret);
        System.out.println(ret.get(0));
        
    }

    
}

