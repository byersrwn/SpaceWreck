public class StateEdge {

    String change;
    State from;
    State to;

    public StateEdge(String s, State f, State t){
        this.change = s;
        this.from = f;
        this.to = t;
    }

    @Override
    public String toString(){
        return "I am a state edge with change: " + this.change  + " from State " + from.LuckyState + from.RocketState
         + " and to state: " + to.LuckyState + to.RocketState;
    }
    
}
