package sjq.light.rest.http.rest;

import sjq.light.rest.http.url.params.MatchParams;

public class MatchAction {
	private String url;
	private MatchParams matchParams;
	private RestHandler restHandler;

	public MatchAction(String url, MatchParams matchParams, RestHandler restHandler) {
		super();
		this.url = url;
		this.matchParams = matchParams;
		this.restHandler = restHandler;
	}

	public String getUrl() {
		return url;
	}

	public MatchParams getMatchParams() {
		return matchParams;
	}

	public RestHandler getRestHandler() {
		return restHandler;
	}

}
