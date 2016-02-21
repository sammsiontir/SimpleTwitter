package com.codepath.apps.simpletwitter.RESTAPI;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1"; // or 1.1/ ?????
	public static final String REST_CONSUMER_KEY = "gLtvI81CeKY6U0yhfmrmuAtjd";
	public static final String REST_CONSUMER_SECRET = "HOnHnaeuB4OAkcVYgbfONxrJdOjv6mjI0WWyC74EukWOeSM02Q";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public static final String GET_HOMETIMELINE_URL = "statuses/home_timeline.json";
	public static final String GET_MY_ACCOUNT_URL   = "account/verify_credentials.json";
	public static final String POST_UPDATE_URL      = "statuses/update.json";
	public static final String POST_RETWEET_URL     = "statuses/retweet/"; // + id + JSON
	public static final String POST_UNRETWEET_URL   = "statuses/unretweet/"; // +id + JSON
	public static final String POST_FAVORITE_URL    = "favorites/create.json";
	public static final String POST_UNFAVORITE_URL  = "favorites/destroy.json";
	public static final String JSON                 = ".json";
	public static final String PARAM_COUNT = "25";


	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	// Home Timeline - Get home timelime here
	/* - Get the home timeline for user
	 *   GET statuses/home_timeline.json
	 *     count=25
	 *     since_id=1 (return all tweets)
	*/
	public void getHomeTimeline(long max_id, long since_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(GET_HOMETIMELINE_URL);
		// Specify params
		RequestParams params = new RequestParams();
		params.put("count", PARAM_COUNT);
		if(max_id > 0) {
			params.put("max_id", Long.toString(max_id));
		}
		if(since_id > 0) {
			params.put("since_id", Long.toString(since_id));
		}
		// Execute request
		getClient().get(apiUrl, params, handler);
	}
	// Get my account
	public void getMyAccount(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(GET_MY_ACCOUNT_URL);
		getClient().get(apiUrl, new RequestParams(), handler);
	}

	// Post new tweet
	public void postStatusUpdate(String status, long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(POST_UPDATE_URL);
		// Specify params
		RequestParams params = new RequestParams();
		params.put("status", status);
		if(id > 0) {
			params.put("in_reply_to_status_id", id);
		}
		// Execute request
		getClient().post(apiUrl, params, handler);
	}

	// Post retweet
	public void postRetweet(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(POST_RETWEET_URL + Long.toString(id) + JSON);
		// Specify params
		RequestParams params = new RequestParams();
		params.put("id", id);
		// Execute request
		getClient().post(apiUrl, params, handler);
	}

	// Post un-retweet
	public void postUnRetweet(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(POST_UNRETWEET_URL + Long.toString(id) + JSON);
		// Specify params
		RequestParams params = new RequestParams();
		params.put("id", id);
		// Execute request
		getClient().post(apiUrl, params, handler);
	}

	// Post favorite
	public void postFavorite(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(POST_FAVORITE_URL);
		// Specify params
		RequestParams params = new RequestParams();
		params.put("id", id);
		// Execute request
		getClient().post(apiUrl, params, handler);
	}

	// Post un-favorite
	public void postUnFavorite(long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(POST_UNFAVORITE_URL);
		// Specify params
		RequestParams params = new RequestParams();
		params.put("id", id);
		// Execute request
		getClient().post(apiUrl, params, handler);
	}
}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */