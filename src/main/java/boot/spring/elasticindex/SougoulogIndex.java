package boot.spring.elasticindex;



import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "sougoulog",type = "docs", shards = 1, replicas = 0)
public class SougoulogIndex {
	@Id
	private Integer id;
	@Field(type = FieldType.Date, format = DateFormat.hour_minute_second)
	private String visittime;
	@Field(type = FieldType.Text)
	private String userid;
	@Field(type = FieldType.Text, analyzer = "ik_smart")
	private String keywords;
	@Field(type = FieldType.Integer)
	private Integer rank;
	@Field(type = FieldType.Integer)
	private Integer clicknum;
	@Field(type = FieldType.Text)
	private String url;

	public String getVisittime() {
		return visittime;
	}

	public void setVisittime(String visittime) {
		this.visittime = visittime;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Integer getClicknum() {
		return clicknum;
	}

	public void setClicknum(Integer clicknum) {
		this.clicknum = clicknum;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
