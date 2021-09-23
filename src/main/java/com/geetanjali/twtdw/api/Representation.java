package com.geetanjali.twtdw.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import twitter4j.Status;
import twitter4j.TwitterException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Representation
{
        private String message;
        private String username;
        private String twitterHandle;
        private String profileImageURL;
        private Date createdAt;

        public Representation(String message, String username, String twitterHandle,
                              String profileImageURL, Date createdAt)
        {
            this.message = message;
            this.username = username;
            this.twitterHandle = twitterHandle;
            this.profileImageURL = profileImageURL;
            this.createdAt = createdAt;
        }

        @JsonProperty
        public String getMessage() {
            return message;
        }

        @JsonProperty
        public String getUsername() {
            return username;
        }

        @JsonProperty
        public String getTwitterHandle() {
            return twitterHandle;
        }

        @JsonProperty
        public String getProfileImageURL() {
            return profileImageURL;
        }

        @JsonProperty
        public Date getCreatedAt() {
            return createdAt;
        }

        @Override
        public String toString()
        {
            return "Tweets{ Message:" + message + " Username:" + username + " Twitter Handle:" + twitterHandle + " Profile image URL:"
                    + profileImageURL + " Created At:" + createdAt + " }";
        }
}

