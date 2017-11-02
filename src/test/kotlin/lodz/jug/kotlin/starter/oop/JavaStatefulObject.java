package lodz.jug.kotlin.starter.oop;

public class JavaStatefulObject {

    private Object state;

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "JavaStatefulObject{" +
                "state=" + state +
                '}';
    }
}
