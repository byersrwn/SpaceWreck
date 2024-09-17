public class State {
    String LuckyState;
    String RocketState;
    boolean isWin;
    String discovered;

    public State(String l, String r){
        this.LuckyState = l;
        this.RocketState = r;
        this.discovered = "Undiscovered";
    }

    public State(boolean w){
        isWin = w;
        LuckyState = "W";
        RocketState = "W";
        this.discovered = "WIN STATE";
    }

    @Override
    public String toString(){
        return "I am a state with: " + this.LuckyState + this.RocketState;
    }

    @Override
    public boolean equals(Object o) {
        State s1 = (State) o;
        boolean t1 = s1.LuckyState.equals(this.LuckyState);
        boolean t2 = s1.RocketState.equals(this.RocketState);
        boolean test = s1.LuckyState.equals(this.LuckyState) && s1.RocketState.equals(this.RocketState);
        return test;
    }

    public boolean compareState(State s1){
        boolean t1 = s1.LuckyState.equals(this.LuckyState);
        boolean t2 = s1.RocketState.equals(this.RocketState);
        boolean test = s1.LuckyState.equals(this.LuckyState) && s1.RocketState.equals(this.RocketState);
        return test;
    }

    // @Override
    // public int hashCode() {
    //     int ret = ;
    // }

    
}
