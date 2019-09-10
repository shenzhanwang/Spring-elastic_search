package boot.spring.pagemodel;

import java.util.HashMap;


/**
 * 搜狗日志的搜索请求参数
 * @author shenzhanwang
 *
 */
public class SougoulogSearchRequest {
	// 查询条件
	private QueryCommand query;
	// 过滤条件
	private HashMap<String, String> queryFilter;
	
	public QueryCommand getQuery() {
		return query;
	}
	public void setQuery(QueryCommand query) {
		this.query = query;
	}
	public HashMap<String, String> getQueryFilter() {
		return queryFilter;
	}
	public void setQueryFilter(HashMap<String, String> queryFilter) {
		this.queryFilter = queryFilter;
	}
	
}
