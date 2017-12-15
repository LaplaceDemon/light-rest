package sjq.light.rest.http.url.params;

import java.util.Map;

public class MatchParams {
	private boolean match;
	private Map<String, String> params = null;
	
	public MatchParams(boolean match, Map<String, String> params) {
		super();
		this.match = match;
		this.params = params;
	}

	public boolean isMatch() {
		return match;
	}
	
	public String get(String k){
		return this.params.get(k);
	}
	
	public Map<String, String> getParams() {
		return params;
	}

	public int size(){
		if(params==null){
			return 0;
		}
		return params.size();
	}
}
