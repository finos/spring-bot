package org.finos.symphony.toolkit.quickfix;

import java.util.ArrayList;
import java.util.List;

import quickfix.Field;
import quickfix.FieldMap;
import quickfix.Group;

/**
 * This structure exists so that we can keep track of the field order in the JSON.
 * 
 * @author Rob Moffat
 *
 */
public class TempFieldMap extends FieldMap {

	private static final long serialVersionUID = -3100743622763939444L;

	private List<Field<?>> fieldOrder = new ArrayList<>();
	
	private List<FieldMap> tempGroups = new ArrayList<>();
	
	private FieldMap tempHeader, tempTrailer;
	
	private String groupName;

	public TempFieldMap() {
		super();
	}
	
	@Override
	public int[] getFieldOrder() {
		return fieldOrder.stream().map(f -> f.getTag()).mapToInt(i -> (int) i).toArray();
	}

	@Override
	public void setField(int key, Field<?> field) {
		fieldOrder.add(field);
		super.setField(key, field);
	}

	@SuppressWarnings("unchecked")
	public Group toGroup(String messageClass) {
		try {
			Class<Group> clz = (Class<Group>) Class.forName(messageClass+"$"+groupName);
			Group g = clz.newInstance();
			g.setFields(this);
			return g;
		} catch (Exception  e) {
			throw new UnsupportedOperationException("Couldn't construct class for group: "+groupName, e);
		}
	}	
	
	public FieldMap getTempHeader() {
		return tempHeader;
	}

	public void setTempHeader(FieldMap tempHeader) {
		this.tempHeader = tempHeader;
	}

	public FieldMap getTempTrailer() {
		return tempTrailer;
	}

	public void setTempTrailer(FieldMap tempTrailer) {
		this.tempTrailer = tempTrailer;
	}

	public void addTempGroup(FieldMap group) {
		this.tempGroups.add(group);
	}

	public List<FieldMap> getTempGroups() {
		return tempGroups;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}