package example.symphony.demoworkflow.expenses;

import org.finos.symphony.toolkit.workflow.java.Work;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Display;

import javax.validation.constraints.Min;

@Work(name = "New Claim Details")
public class StartClaim {

	@Display(name = "Description")
	String description;

	@Display(name = "Amount")
	@Min(0)
	Number amount;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Number getAmount() {
		return amount;
	}

	public void setAmount(Number amount) {
		this.amount = amount;
	}
	
	
}
