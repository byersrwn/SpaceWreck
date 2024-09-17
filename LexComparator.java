import java.util.Comparator;

public class LexComparator implements Comparator<String>{
    @Override
    public int compare(String o1, String o2) {
        int smallest;
        if(o1.length() > o2.length()){
            smallest = o2.length();
        }
        else if(o1.length() < o2.length()){
            smallest = o1.length();
        }

        else{
            smallest = o1.length();
        }

        for(int i = 0 ; i < smallest ; i++){
            char o1Char = o1.charAt(i);
            char o2Char = o2.charAt(i);

            if(o1Char > o2Char){
                return 1;
            }

            if(o1Char < o2Char){
                return -1;
            }

        }
        return 0;
    }


}
