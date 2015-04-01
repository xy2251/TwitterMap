package tmpkg;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint(value="/markerupdate")
public class MapUpdateServer extends HttpServlet{
	private static final long serialVersionUID = 1L;
    private static Logger log = Logger.getAnonymousLogger();
	private static Set<Session> sessionslist = Collections.synchronizedSet(new HashSet<Session>());
	
   /* public void init(){
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
					//twit.setKeyword(mh.getKeyword(status.getText()));
					twit.setUrl(status.getSource());
					TwitterJson tj = new TwitterJson(Json.createObjectBuilder()
		        			.add("latitude",twit.getLatitude())
		        			.add("longtitude", twit.getLongtitude())
		        			.add("text", twit.getText())
		        			.add("username", twit.getUsername())
		        			.add("timestamp", twit.getTimestamp())
		        			.build());				
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
    }*/
    @OnOpen
    public void onOpen(Session session){
        sessionslist.add(session);
        
        try {
            session.getBasicRemote().sendText("Connection Established");
            System.out.println("successful connect!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("Message from " + session.getId() + ": " + message);

        
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session){
        sessionslist.remove(session);
    }
    
    public void broadcastData(Twitter tj) {
    	for (Session s : sessionslist){
                try {
					s.getBasicRemote().sendObject(tj);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (EncodeException e) {
					e.printStackTrace();
				}
    	}
    }
    
    @Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
    	try {
            // Scan request into a string
            Scanner scanner = new Scanner(request.getInputStream());
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            // Parse the JSON message
            InputStream stream = new ByteArrayInputStream(builder.toString().getBytes());
            Map<String, String> message = new ObjectMapper().readValue(stream, Map.class);

            // Confirm the subscription
            if (message.get("Type").equals("SubscriptionConfirmation")) {
                
                new URL(message.get("SubscribeURL")).openStream();
                log.info("Confirmed: " + message.get("TopicArn"));

            } else if (message.get("Type").equals("Notification")) {
                log.info("Received: " + message.get("Message"));
            }
            log.info(builder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.print("Come on, I am another type of server. You are trying to access this uri in a wrong way.");
	}

}
