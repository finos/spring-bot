package org.finos.symphony.toolkit.workflow.content;

public interface CodeBlock extends Content {

	public static CodeBlock of(String s) {
		return new CodeBlock() {
			
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

		};
	}

}
