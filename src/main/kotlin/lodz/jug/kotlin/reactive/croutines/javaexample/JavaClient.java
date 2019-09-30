package lodz.jug.kotlin.reactive.croutines.javaexample;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import org.jetbrains.annotations.NotNull;
import static kotlinx.coroutines.BuildersKt.runBlocking;

public class JavaClient {

    public static void main(String[] args) throws InterruptedException {

        runBlocking(EmptyCoroutineContext.INSTANCE, (context, block) -> {
            Continuation<Integer> continuation = new Continuation<Integer>() {
                @NotNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NotNull Object o) {
                    System.out.println("length is : "+o);
                }
            };
            return SomethingWithCoroutines.INSTANCE.start("someTextInJava",continuation);

        });

    }
}
