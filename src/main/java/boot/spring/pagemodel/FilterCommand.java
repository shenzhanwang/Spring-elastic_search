package boot.spring.pagemodel;

import java.util.List;

import boot.spring.elastic.search.querytypes.FuzzyQuery;
import boot.spring.elastic.search.querytypes.MatchQuery;
import boot.spring.elastic.search.querytypes.RangeQuery;
import boot.spring.elastic.search.querytypes.TermQuery;

public class FilterCommand {
	List<RangeQuery> ranges;
	
	List<TermQuery> terms;
	
	List<FuzzyQuery> fuzzys;
	
	List<MatchQuery> matches;
	
	public List<RangeQuery> getRanges() {
		return ranges;
	}

	public void setRanges(List<RangeQuery> ranges) {
		this.ranges = ranges;
	}

	public List<TermQuery> getTerms() {
		return terms;
	}

	public void setTerms(List<TermQuery> terms) {
		this.terms = terms;
	}

	public List<FuzzyQuery> getFuzzys() {
		return fuzzys;
	}

	public void setFuzzys(List<FuzzyQuery> fuzzys) {
		this.fuzzys = fuzzys;
	}

	public List<MatchQuery> getMatches() {
		return matches;
	}

	public void setMatches(List<MatchQuery> matches) {
		this.matches = matches;
	}
	
}
