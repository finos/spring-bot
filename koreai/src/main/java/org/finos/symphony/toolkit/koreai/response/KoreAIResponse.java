package org.finos.symphony.toolkit.koreai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Holder for responses from KoreAI REST Endpoint, 
 * and also to be returned for Symphony Freemarker data.
 * 
 * @author rodriva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KoreAIResponse {
    private Object text;
    
    @JsonProperty(value = "isTemplate")
    private boolean template;
    
    private List<Errors> errors;
    
    private List<String> options;
    
    private String form;

    private String messageML;
    
	public String getMessageML() {
		return messageML;
	}

	public void setMessageML(String messageML) {
		this.messageML = messageML;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

    public Object getText() {
        return text;
    }

    public void setText(Object text) {
        this.text = text;
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public List<Errors> getErrors() {
        return errors;
    }

    public void setErrors(List<Errors> errors) {
        this.errors = errors;
    }

    static class Errors {
        private String msg;
        private int code;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

		@Override
		public String toString() {
			return "Errors [msg=" + msg + ", code=" + code + "]";
		}
    }

	@Override
	public String toString() {
		return "KoreAIResponse [text=" + text + ", template=" + template + ", errors=" + errors + ", options=" + options
				+ "]";
	}
    
    
}
