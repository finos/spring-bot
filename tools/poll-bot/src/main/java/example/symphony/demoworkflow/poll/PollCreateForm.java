package example.symphony.demoworkflow.poll;

import java.time.temporal.ChronoUnit;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

@Work(name = "Poll Setup", editable = true)
@Template(edit="classpath:/template/poll-create-form.ftl")
public class PollCreateForm {

	enum TimeUnit {
		MINUTES, HOURS, DAYS
	};

	public String question;

	public String option1;
	public String option2;
	public String option3;
	public String option4;
	public String option5;
	public String option6;

	@Min(0)
	@Max(60)
	private Integer time = 15;
	private ChronoUnit timeUnit = ChronoUnit.MINUTES;
	
	private boolean endAutomatically = true;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getOption1() {
		return option1;
	}

	public void setOption1(String option1) {
		this.option1 = option1;
	}

	public String getOption2() {
		return option2;
	}

	public void setOption2(String option2) {
		this.option2 = option2;
	}

	public String getOption3() {
		return option3;
	}

	public void setOption3(String option3) {
		this.option3 = option3;
	}

	public String getOption4() {
		return option4;
	}

	public void setOption4(String option4) {
		this.option4 = option4;
	}

	public String getOption5() {
		return option5;
	}

	public void setOption5(String option5) {
		this.option5 = option5;
	}

	public String getOption6() {
		return option6;
	}

	public void setOption6(String option6) {
		this.option6 = option6;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public ChronoUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(ChronoUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public boolean isEndAutomatically() {
		return endAutomatically;
	}

	public void setEndAutomatically(boolean endAutomatically) {
		this.endAutomatically = endAutomatically;
	}

}
