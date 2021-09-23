package com.geetanjali.twtdw.resources;

import com.codahale.metrics.annotation.Metered;
import com.geetanjali.twtdw.TwitterDropWizardApplication;
import com.geetanjali.twtdw.api.Representation;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.caching.CacheControl;
import io.dropwizard.jersey.params.*;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.HashtagEntity;
import twitter4j.Place;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Path("/api/1.0/twitter")
@Produces(MediaType.APPLICATION_JSON)
public class Resource {
    private final String message;
    private Twitter twitter ;
    private Status status;
    private List<Status> statuses;
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private static final Logger logger = LoggerFactory.getLogger(TwitterDropWizardApplication.class);
    TwitterStream twitterStream;


    public Resource(String message, String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        this.message = message;
        this.statuses = new ArrayList<Status>();

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(this.consumerKey)
                .setOAuthConsumerSecret(this.consumerSecret)
                .setOAuthAccessToken(this.accessToken)
                .setOAuthAccessTokenSecret(this.accessTokenSecret);
        Configuration c = cb.build();
        TwitterFactory tf = new TwitterFactory(c);
        twitter = tf.getInstance();
        twitterStream = new TwitterStreamFactory(c).getInstance();
        StatusListener statusListener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println(status.getUser().getName() + " : " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { }

            @Override
            public void onTrackLimitationNotice(int i) {}

            @Override
            public void onScrubGeo(long l, long l1) {}

            @Override
            public void onStallWarning(StallWarning stallWarning) {}

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        };
        twitterStream.addListener(statusListener);
    }

    @Path("/timeline")
    @GET
    public Response fetchTweet() throws TwitterException, IOException
    {
        try
        {
            Paging page = new Paging(1,200);
            statuses.addAll(twitter.getHomeTimeline(page));
            //List<String> str = new ArrayList<String>();
            int count = statuses.size();
            logger.info("Fetch successful. Displaying Tweets");
            Representation r[] = new Representation[count];
            for(int i=0; i<count; i++)
            {
                System.out.println("Tweet "+ i +"="+ statuses.get(i).getText());
                r[i] = new Representation(statuses.get(i).getText(), statuses.get(i).getUser().getName(),
                        statuses.get(i).getUser().getScreenName(), statuses.get(i).getUser().getProfileImageURL(),
                        statuses.get(i).getCreatedAt());
                //str.add(statuses.get(count).getText());
                //return Response.ok().entity(r).build();
            }
            return Response.ok().entity(r).build();
           // return Response.ok().entity("Tweets retrieved successfully").build();
        }
        catch (TwitterException e)
        {
           return Response.serverError().entity("Error in retrieving tweets").build();
        }
    }

    @Path("/filter")
    @GET
    public void filterTweets() throws TwitterException
    {
        FilterQuery query = new FilterQuery();
        query.track("India");
        query.language("en");
        twitterStream.filter(query);
        twitterStream.sample();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        twitterStream.shutdown();
                    }
                },
                5000
        );
    }

    @Path("/tweet")
    @POST
    public Response postTweet() throws TwitterException, IOException
    {
        try {
            status = twitter.updateStatus(message);
            System.out.println("Status update successful to " + status.getText() + "\n");
            return Response.ok().entity("Status updated successfully").build();
        } catch (TwitterException e) {
            return Response.serverError().entity("Error in updating status").build();
        }
    }
}