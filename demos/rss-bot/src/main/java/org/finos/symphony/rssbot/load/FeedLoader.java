package org.finos.symphony.rssbot.load;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.ws.rs.client.WebTarget;

import org.finos.symphony.toolkit.spring.api.properties.ProxyProperties;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.symphony.api.bindings.jersey.JerseyApiBuilder;

public class FeedLoader {
	
	ProxyProperties pp;
	
	public FeedLoader(ProxyProperties proxy) {
		this.pp = proxy;
	}

	public SyndFeed createSyndFeed(String url) throws FeedException, IOException, MalformedURLException {
		SyndFeedInput input = new SyndFeedInput();
		input.setAllowDoctypes(true);
		SyndFeed feed = input.build(new XmlReader(downloadContent(url)));
		return feed;
	}

	public InputStream downloadContent(String url) throws MalformedURLException {
		JerseyApiBuilder jab = new JerseyApiBuilder(url);
		if (pp != null) {
			pp.configure(jab);
		}
		WebTarget wt = jab.newWebTarget(url);
		InputStream out = wt.request().get().readEntity(InputStream.class);
		return out;
	}}
