package lodz.jug.kotlin.coroutines.bonus;


import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class Bonus1 {
//jar -f "/home/pawel/.m2/repository/org/jetbrains/kotlin/kotlin-stdlib-common/1.3.50/kotlin-stdlib-common-1.3.50.jar"
    public static void main(String[] args) {
//            example1();
            example2();
//
    }

    private static void example2() {
        CompletableFuture<Integer> result = JavaAPI.INSTANCE.callFunction1();


        result.whenComplete((r,e) -> System.out.println("Result is : "+r));

        result.join();

    }


    private static void example1(){
        //BuildersKt.runBlocking();

        Continuation<Integer> c2 = new Continuation<Integer>() {

            @Override
            public void resumeWith(@NotNull Object o) {
                System.out.println("result : "+o);
            }

            @NotNull
            @Override
            public CoroutineContext getContext() {
                return  EmptyCoroutineContext.INSTANCE;
            }
        };


        Continuation<Integer> c1 = new Continuation<Integer>() {
            @Override
            public void resumeWith(@NotNull Object o) {
//                ResultKt result=(ResultKt)o;
                //result.isSuccess-impl();
                System.out.println(o.getClass().getSimpleName());
                Integer r=(Integer)o;

                Bonus1JavaIntegration.INSTANCE.function2(r,c2);
            }

            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

        };

        Bonus1JavaIntegration.INSTANCE.function1(c1);

        sleep(2000);
    }



    static void sleep(int milliseconds){
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
