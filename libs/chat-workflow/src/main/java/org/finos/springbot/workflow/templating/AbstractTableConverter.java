package org.finos.springbot.workflow.templating;

import java.lang.reflect.Type;

public abstract class AbstractTableConverter<X> extends AbstractComplexTypeConverter<X> {


    public AbstractTableConverter(int priority, TableRendering<X> r) {
        super(priority, r);
    }
	
    
    public X createTable(Type t, boolean editMode, Variable variable, WithField<X> headerDetail, WithField<X> rowDetail, WithType<X> controller) {
       return getR().table(variable, rowHeaders(t, editMode, variable, headerDetail, controller),
    		   rowDetails(t, editMode, variable, rowDetail, controller));

    }
   
	protected abstract X rowDetails(Type t, boolean editMode, Variable variable, WithField<X> rowDetail, WithType<X> controller);

    protected abstract X rowHeaders(Type t, boolean editMode, Variable variable, WithField<X> headerDetails, WithType<X> controller);


    public TableRendering<X> getR() {
    	return (TableRendering<X>) r;
    }

}