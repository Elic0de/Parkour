package elicode.parkour.util.tweet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class IntentTweetBuilder {

	private final String text;
	private final List<String> hashtags = new ArrayList<>();
	private String url;

	public IntentTweetBuilder(String text){
		this.text = text;
	}

	public IntentTweetBuilder addHashtag(String hashtag){
		hashtags.add(hashtag);
		return this;
	}

	public IntentTweetBuilder setURL(String url){
		this.url = url;
		return this;
	}

	public String build(){
		String text = "text=" + encode(this.text);
		String hashtags = this.hashtags.isEmpty() ? "" : "&hashtags=" + encode(String.join(",", this.hashtags));
		String url = this.url != null ? "&url=" + this.url : "";
		return "https://twitter.com/intent/tweet?" + text + hashtags + url;
	}

	private String encode(String text){
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch ( UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

}
