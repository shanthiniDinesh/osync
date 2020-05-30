package com.oapps.osync.fields;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(staticName = "of")
@ToString
public class Fields {

	@Getter
	List<TextField> textFields = new ArrayList<TextField>();
	@Getter
	List<NumberField> numberFields = new ArrayList<NumberField>();
	@Getter
	List<BooleanField> booleanFields = new ArrayList<BooleanField>();
	
	@Getter
	List<DoubleField> doubleFields=new ArrayList<DoubleField>();
	
	
	

	public class TextField extends Field {
		public TextField(String id, String displayName) {
			super("text", id, displayName);
		}
	}

	public class NumberField extends Field {
		public NumberField(String id, String displayName) {
			super("number", id, displayName);
		}

	}

	public class BooleanField extends Field {
		public BooleanField(String id, String displayName) {
			super("boolean", id, displayName);
		}
	}
	
	public class DoubleField extends Field {
		public DoubleField(String id, String displayName) {
			super("Double", id, displayName);
		}
	}

	public void addTextField(TextField tf) {
		textFields.add(tf);
	}

	public void addNumberField(NumberField nf) {
		numberFields.add(nf);
	}

	public void addBooleanField(BooleanField bf) {
		booleanFields.add(bf);
	}
	
	public void addDoubleField(DoubleField df) {
		doubleFields.add(df);
	}
	
	
	

	public Fields number(String id, String displayName) {
		NumberField nf = new NumberField(id, displayName);
		this.addNumberField(nf);
		return this;
	}

	public Fields text(String id, String displayName) {
		TextField nf = new TextField(id, displayName);
		this.addTextField(nf);
		return this;
	}

	public Fields bool(String id, String displayName) {
		BooleanField nf = new BooleanField(id, displayName);
		this.addBooleanField(nf);
		return this;
	}
	
	public Fields doublee(String id, String displayName) {
		DoubleField df = new DoubleField(id, displayName);
		this.addDoubleField(df);
		return this;
	}
	
	
	
}
