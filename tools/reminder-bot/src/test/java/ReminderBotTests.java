import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4User;
import edu.stanford.nlp.time.Timex;
import org.finos.symphony.toolkit.tools.reminders.TimeFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReminderBotTests {
    final String userDisplayName = "Finos User 2";
    V4User v4User;
    String s;
    V4Event v4Event;


    @Autowired
    TimeFinder timefinder;


    @BeforeEach
    void setup(){

        v4Event = new V4Event();

    }

    @Test
    public void toLocalTimeTestCorrectValue(Timex timexCorrect){
//        timexCorrect = 2021-07-05T21:12:00;

    }
    @Test
    public void applyTest(){

    }

}
