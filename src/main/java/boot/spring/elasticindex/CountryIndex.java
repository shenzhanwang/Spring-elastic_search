package boot.spring.elasticindex;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "country",type = "countrydoc", shards = 3, replicas = 1)
public class CountryIndex {
	@Id
	private short country_id;
	@Field(type = FieldType.Keyword)
	private String country;
	@Field(type = FieldType.Date, format=DateFormat.date_hour_minute_second)
	private String last_update;
	
	public short getCountry_id() {
		return country_id;
	}
	public void setCountry_id(short country_id) {
		this.country_id = country_id;
	}
	public String getCountry() {
		return country;
	}
	
	public String getLast_update() {
		return last_update;
	}
	public void setLast_update(String last_update) {
		this.last_update = last_update;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	
}
