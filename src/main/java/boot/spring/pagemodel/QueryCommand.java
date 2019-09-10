package boot.spring.pagemodel;

/**
 * Description:检索参数
 */
public class QueryCommand {
	//关键词
	private String keyWords;
	//搜索域
	private String search_field;
	//逻辑连接词
	private String operator;
	//起始位置
	private int start;
	//返回条数
	private int rows;
	//返回字段
	private String return_filed;
	//切面检索域
	private String facetField;
	//时间切面
	private String facetDateFiled;
	//切面增长步长
	private String step;
	//切面检索
	private String facetQuery;
	//pivot域
	private String[] pivotFields;
	//权重值
	private String boost;
	//是否开启高亮显示
	private boolean highlightOn;
	//高亮显示域
	private String[] highlightField;
	//当前经纬度
	private String pt;
	//检索半径
	private String d;

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getSearch_field() {
		return search_field;
	}

	public void setSearch_field(String search_field) {
		this.search_field = search_field;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getReturn_filed() {
		return return_filed;
	}

	public void setReturn_filed(String return_filed) {
		this.return_filed = return_filed;
	}

	public String getFacetField() {
		return facetField;
	}

	public void setFacetField(String facetField) {
		this.facetField = facetField;
	}

	public String getFacetQuery() {
		return facetQuery;
	}

	public void setFacetQuery(String facetQuery) {
		this.facetQuery = facetQuery;
	}

	public String[] getPivotFields() {
		return pivotFields;
	}

	public void setPivotFields(String[] pivotFields) {
		this.pivotFields = pivotFields;
	}

	public String getBoost() {
		return boost;
	}

	public void setBoost(String boost) {
		this.boost = boost;
	}

	public boolean isHighlightOn() {
		return highlightOn;
	}

	public void setHighlightOn(boolean highlightOn) {
		this.highlightOn = highlightOn;
	}

	public String[] getHighlightField() {
		return highlightField;
	}

	public void setHighlightField(String[] highlightField) {
		this.highlightField = highlightField;
	}

	public String getFacetDateFiled() {
		return facetDateFiled;
	}

	public void setFacetDateFiled(String facetDateFiled) {
		this.facetDateFiled = facetDateFiled;
	}

	public String getPt() {
		return pt;
	}

	public void setPt(String pt) {
		this.pt = pt;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}
}
