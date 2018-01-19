package lodz.jug.kotlin.starter.types;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class JavaInvariance {

    public static void main(String[] args) {
        List<String> strings= new LinkedList<>();
        strings.add("aaa");
        strings.add("bbb");

        List<Object> object=new LinkedList<>();
        object.add("aaa");
        object.add(2);

//        object=strings;  -> ERROR -> EXPLAIN WHY

        List<? extends Object> parent=strings;


        //BUG BY DESIGN WITH ARRAYS

//        String[] stringsArray={"aaa","bbb"};
//        Object[] objectsArray=stringsArray;
//        objectsArray[1]=55;

    }


    //show how and then and compose are implemented in java.util.Function
    public static <A,B> B highOrderGenericFunction(Function<? super A, ? extends B> f ,A argument){
        return f.apply(argument);
    }

}
