package example.symphony.demoworkflow.poll.bot;

import java.util.Objects;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PollResult {
	public String answer;
	public long count;
	public int width;

    public PollResult(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "{" + answer + "=" + count + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof PollResult)) { return false; }
        PollResult that = (PollResult) o;
        return answer.equals(that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer);
    }
}
