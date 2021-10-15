package org.finos.springbot.workflow.content;

public interface CodeBlock extends Content {
	
	public static class CodeBlockImpl implements CodeBlock {
		
		private final String s;
		
		public CodeBlockImpl(String s) {
			super();
			this.s = s;
		}

		@Override
		public String getText() {
			return s;
		}

		@Override
		public int hashCode() {
			return s.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CodeBlock) {
				return this.getText().equals(((CodeBlock) obj).getText());
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return "CodeBlock ["+s+"]";
		}
		
	}

	public static CodeBlock of(String s) {
		return new CodeBlockImpl(s);
	}

}
