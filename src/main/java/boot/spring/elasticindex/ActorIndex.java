package boot.spring.elasticindex;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "actor",type = "docs", shards = 1, replicas = 0)
public class ActorIndex {
	@Id
	private short id;
	// 不分词
	@Field(type = FieldType.Keyword)
	private String firstName;
	@Field(type = FieldType.Keyword)
	private String lastName;
	@Field(type = FieldType.Date,format = DateFormat.date)
	private String lastUpdate;
	
	public short getId() {
		return id;
	}
	public void setId(short id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
}
