package com.github.deutschebank.symphony.workflow.response;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;

public class DataResponse implements Response {

	private final EntityJson data;
	private final Addressable stream;
	private final Workflow workflow;
	private final String name;
	private final String instructions;

	public DataResponse(Workflow wf, Addressable stream, EntityJson data, String name, String instructions) {
		super();
		this.data = data;
		this.stream = stream;
		this.workflow = wf;
		this.name = name;
		this.instructions = instructions;
	}

	public EntityJson getData() {
		return data;
	}
	
	public Addressable getAddress() {
		return stream;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	@Override
	public String toString() {
		return "DataResponse [data=" + data + ", stream=" + stream + ", workflow=" + workflow + "]";
	}

	public String getName() {
		return name;
	}

	public String getInstructions() {
		return instructions;
	}
	
}
