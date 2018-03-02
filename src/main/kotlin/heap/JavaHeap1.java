package heap;

import java.util.LinkedList;
import java.util.List;

public class JavaHeap1 {
    public static void main(String[] args) {
        List<Integer> l=new LinkedList<>();

        List<Integer> damn=pollute(l);

        //explosion
        System.out.println(damn.get(0));  //this will actually work and display ojojojo
//        Integer i=damn.get(0); //here there is no escape - ClassCastException

    }


    private  static List<Integer> pollute(List l){
        List<String> s=l;
        s.add("ojojojo");
        return l;
    }

}
