package boot.spring.elastic.index;

import java.util.List;
import java.util.Map;

public interface IndexService {
	/**
	 * 创建索引并设置字段类型
	 * @param indexname
	 * @param mapping
	 */
	void createMapping(String indexname, Map<String, Object> mapping);
	
	void saveOrUpdateIndexDoc(String indexName, String indexType, Map<String, Object> doc);
	
	void indexDocs(String indexName, String indexType, List<Map<String,Object>> docs);
	
	int deleteDoc(String indexName, String indexType, String id);
	
	
}
