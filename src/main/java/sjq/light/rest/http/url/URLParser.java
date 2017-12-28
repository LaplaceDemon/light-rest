package sjq.light.rest.http.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sjq.light.rest.http.url.params.MatchParams;


public class URLParser {
	private URLParser() {
	}

	private List<Term> termList = new ArrayList<Term>();

	/**
	 * 解析url模板。
	 * 
	 * @param urlTpl
	 * @return
	 */
	public static URLParser parse(String urlTpl) {
		URLParser urlParser = new URLParser();
		Pattern pattern = Pattern.compile("\\{[a-zA-Z0-9]+\\}");
		Matcher matcher = pattern.matcher(urlTpl);
		
		int lastEndIndex = 0;
		while (matcher.find()) {
			String param = matcher.group();
			int startIndex = matcher.start();
			if(startIndex > lastEndIndex){
				String subUrtpl = urlTpl.substring(lastEndIndex, startIndex);
				urlParser.termList.add(new Term(subUrtpl,TermType.String));
			}
			lastEndIndex = matcher.end();
			String paramName = param.substring(1, param.length()-1);
			urlParser.termList.add(new Term(paramName,TermType.Param));
		}
		
		if(lastEndIndex < urlTpl.length()){
			String subUrtpl = urlTpl.substring(lastEndIndex, urlTpl.length());
			urlParser.termList.add(new Term(subUrtpl,TermType.String));
		}
		
		return urlParser;
	}
	
	/**
	 * 提取URL中的路径参数。
	 * @param url
	 * @return 返回匹配结果。
	 */
	public MatchParams matchPathParams(String url) {
		Map<String, String> resultMap = new HashMap<>();
		
		int lastIndex = 0;
		Term lastParamTerm = null;
		for(int i = 0;i<this.termList.size();i++){
			Term term = this.termList.get(i);
			if(term.type.equals(TermType.Param)){
				lastParamTerm = term;
			}else{
				int startIndex = url.indexOf(term.name,lastIndex);
				if(startIndex<0) {
					return new MatchParams(false, null);
				}
				if(lastParamTerm != null){
					String strValue = url.substring(lastIndex,startIndex);
					resultMap.put(lastParamTerm.name, strValue);
					lastParamTerm = null;
				}
				lastIndex = startIndex + term.name.length();
			}
		}
		
		if(lastParamTerm != null){
			String strValue = url.substring(lastIndex,url.length());
			resultMap.put(lastParamTerm.name, strValue);
			lastParamTerm = null;
		}
		
		/* 这段代码是为了解决 /test1/abc 可以匹配注解值为“/test”的问题。但实际上，只需要将匹配注解值修改为“/test/”就能解决问题。
		else { // lastParamTerm == null
			int charLen = url.length() - lastIndex;
			if(charLen > 1) {
				return new MatchParams(false, null);
			} else if(charLen == 1 && url.charAt(url.length()-1) != '/') {
				return new MatchParams(false, null);
			}
		}
		*/
		
		return new MatchParams(true, resultMap);
	}
	
	public static Map<String,String> parseQueryString(String queryString) {
		Map<String, String> resultMap = new HashMap<>();
		
		String[] equations = queryString.split("&");
		for(String equation : equations){
			if(equation.length()<=0){
				continue ;
			}
			String[] equationKV = equation.split("=");
			resultMap.put(equationKV[0], equationKV[1]);
		}
		
		return resultMap;
	}
	
}

class Term {
	public String name;
	public TermType type;
	
	public Term(String name, TermType type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	@Override
	public String toString() {
		if(type.equals(TermType.Param)){
			return "{" + name + "}";
		}
		
		return  name;
	}
	
}

enum TermType{
	String,
	Param
}

