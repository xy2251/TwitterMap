package tmpkg;
import java.util.Set;

import javax.json.Json;
import javax.websocket.Session;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterConnect {
    protected static DbHelper twitdb = new DbHelper(); 
    protected static MatcherHelper mh = new MatcherHelper();
	public static void main(String args[]) {
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("9tpVzGq9RCRQShKVdHFfIWTom")
		  .setOAuthConsumerSecret("RTHIEXkgF8ER8HcTHL40eKsLHEsJSPL1xk5uhbUsiFT7pmMw7c")
		  .setOAuthAccessToken("2980300185-npxk2IVG8nnzmeBU3Okuv2pr5DDB695YYHegTnN")
		  .setOAuthAccessTokenSecret("QrvSu7US87z7ZjBtzTmADw8OM3nLJhNMVOHekzKOdNXyy");
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();


        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                GeoLocation gl = status.getGeoLocation();
                if (gl!=null && status.getUser() != null) {
                	Twitter twit = new Twitter();
					twit.setUsername(status.getUser().getName());
					twit.setText(status.getText());
					twit.setId(String.valueOf(status.getId()));
					twit.setTimestamp(status.getCreatedAt().toString());
					twit.setLatitude(String.valueOf(gl.getLatitude()));
					twit.setLongtitude(String.valueOf(gl.getLongitude()));
					twit.setKeyword(mh.getKeyword(status.getText()));
					twit.setUrl(status.getSource());
					twitdb.addtwits(twit);		
                }  
            } 
            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        
        twitterStream.sample();
	}
}