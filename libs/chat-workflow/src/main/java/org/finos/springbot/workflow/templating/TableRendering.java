package org.finos.springbot.workflow.templating;

import java.util.List;
import java.util.Map;

/**
 * Adds support for rendering tables.
 */
public interface TableRendering<X> extends Rendering<X> {

	X table(Variable v, X headers, X body);

	X tableCell(Map<String, String> attributes, X content);

	X tableRow(Variable v, Variable r, List<X> cells);

	X tableRowCheckBox(Variable variable, Variable r);

	X tableRowEditButton(Variable variable, Variable r);

	X tableHeaderRow(List<X> out);

}