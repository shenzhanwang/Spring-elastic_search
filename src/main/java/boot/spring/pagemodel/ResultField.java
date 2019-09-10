package boot.spring.pagemodel;

public class ResultField {
	String fieldname;
	String value;
	String chineseCode;
	
	public ResultField(String fieldname, String value, String chineseCode) {
		super();
		this.fieldname = fieldname;
		this.value = value;
		this.chineseCode = chineseCode;
	}
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getChineseCode() {
		return chineseCode;
	}
	public void setChineseCode(String chineseCode) {
		this.chineseCode = chineseCode;
	}
	
}
