//import com.symphony.api.model.V4Event;
//import com.symphony.api.model.V4User;
//import edu.stanford.nlp.time.Timex;
//import org.finos.symphony.toolkit.tools.reminders.StreamEventConsumerImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.text.ParseException;
//import java.time.Instant;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class ReminderBotTests {
//    final String userDisplayName = "Finos User 2";
//    V4User v4User;
//    String s;
//    V4Event v4Event;
//    Instant instant;
//    StreamEventConsumerImpl streamEventConsumer;
//
//
//    @BeforeEach
//    void setup(){
//        streamEventConsumer = new StreamEventConsumerImpl();
//        v4Event = new V4Event();
//
//    }
//
//    @Test
//    public void returnCurrentUserTest(V4Event t){
//        when(t.getPayload().getMessageSent().getMessage().getUser()).thenReturn(v4User);
//
//        String displayName = t.getPayload().getMessageSent().getMessage().getUser().getDisplayName();
//        assertEquals(userDisplayName,displayName);
//        //when(t.getPayload().getMessageSent().getMessage().).thenReturn()
//        when(streamEventConsumer.returnCurrentUser(any())).thenReturn(s);
//
//    }
//
//    @Test
//    public void getMessageFromChatInStringTest(V4Event t) throws Exception {
//
//        when(t.getPayload().getMessageSent().getMessage().getMessage()).thenReturn(s);
//       // when(streamEventConsumer.getMessageFromChatInString(any(v4Event))).thenReturn(v4Event);
//        when(streamEventConsumer.getMessageFromChatInString(any())).thenReturn(s);
//
//    }
//    @Test
//    public void dateToInstantTest(Timex t) throws ParseException {
//
//        when(streamEventConsumer.dateToInstant(t)).thenReturn(instant);
//        verify(streamEventConsumer,times(1)).dateToInstant(any());
//
//    }
//
//}
