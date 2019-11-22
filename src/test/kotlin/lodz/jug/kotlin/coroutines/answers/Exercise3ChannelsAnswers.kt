package lodz.jug.kotlin.coroutines.answers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import lodz.jug.kotlin.coroutines.answers.Exercise3AnswerPipeline.Message.*
import lodz.jug.kotlin.coroutines.answers.Exercise3AnswerPipeline.createFilter
import lodz.jug.kotlin.coroutines.answers.Exercise3AnswerPipeline.createSourceMapper
import lodz.jug.kotlin.coroutines.answers.Exercise3AnswerPipeline.filterMessage
import lodz.jug.kotlin.coroutines.answers.Exercise3AnswerPipeline.rawStringToMessage
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Test

class Exercise3ChannelsAnswers {

    @Test
    fun shouldConvertRawStringToMessage(){
        rawStringToMessage("") `should equal` UnknownMessage
        rawStringToMessage("X") `should equal` SignalMessage('X')
        rawStringToMessage("1".repeat(41)) `should equal` ZippedMessage("1".repeat(41))
        rawStringToMessage("Hello") `should equal` PayloadMessage("Hello")
    }


    @Test
    fun shouldCreateSourceMapper() {
        runBlocking {
            val c=Channel<String>()
            val result=createSourceMapper(c)


//            listOf("","X","1".repeat(41),"Hello").forEach{c.send(it)}   //DEADLOCK?


            launch(Dispatchers.Default){
                listOf("","X","1".repeat(41),"Hello").forEach{c.send(it)}
            }


            result.receive() shouldEqual UnknownMessage
            result.receive() shouldEqual SignalMessage('X')
            result.receive() shouldEqual ZippedMessage("1".repeat(41))
            result.receive() shouldEqual PayloadMessage("Hello")  //what if only one receive ??
            c.close()
        }
    }


    @Test
    fun shouldFilterMessages(){
        filterMessage(UnknownMessage) shouldBe false
        filterMessage(SignalMessage('X')) shouldBe false
        filterMessage(SignalMessage('Y')) shouldBe true
        filterMessage(ZippedMessage("1".repeat(41))) shouldBe true
        filterMessage(ZippedMessage("1".repeat(61))) shouldBe false
        filterMessage(PayloadMessage("XHello")) shouldBe false
        filterMessage(PayloadMessage("Hello")) shouldBe true
    }

    @Test
    fun shouldFilterMessagesInChannels() {
        runBlocking {
            val c=Channel<String>()
            val mapper=createSourceMapper(c)
            val result = createFilter(mapper)


            launch(Dispatchers.Default){
                listOf("","X","Y", "1".repeat(61),"Hello").forEach{c.send(it)}
            }


            result.receive() shouldEqual SignalMessage('Y')
            result.receive() shouldEqual PayloadMessage("Hello")  //what if only one receive ??
            c.close()
        }
    }


}

object Exercise3AnswerPipeline {

    sealed class Message{
        data class PayloadMessage(val payload:String) : Message()
        data class SignalMessage(val signal:Char) : Message()
        data class ZippedMessage(val content: String) : Message()
        object UnknownMessage : Message()
    }


    fun CoroutineScope.createSourceMapper(someSource: ReceiveChannel<String>): ReceiveChannel<Message> =
        produce(Dispatchers.Default){
            someSource.consumeEach {
                val message= rawStringToMessage(it)
                send(message)
            }
        }


    fun CoroutineScope.createFilter(source: ReceiveChannel<Message>): ReceiveChannel<Message> =
            produce(Dispatchers.Default){
                source.consumeEach {
                    if(filterMessage(it))
                        send(it)

                }
            }

    internal fun filterMessage(m: Message): Boolean = when(m){
        is UnknownMessage -> false
        is PayloadMessage -> !m.payload.startsWith("X")
        is SignalMessage -> m.signal != 'X'
        is ZippedMessage -> m.content.length < 60
    }


    internal fun rawStringToMessage(it: String): Message {
        return when {
            it.isEmpty() -> UnknownMessage
            it.length == 1 -> SignalMessage(it[0])
            it.length > 40 -> ZippedMessage(it)
            else -> PayloadMessage(it)
        }
    }

}