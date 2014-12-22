package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import play.libs.*;
import com.fasterxml.jackson.databind.JsonNode;

import javax.net.ssl.HttpsURLConnection;
import play.*;
import play.Play;


import play.mvc.*;

public class Application extends Controller {
    public static final String STATE = "myRandomAndSecureString";
    public static final String CLIENT_ID = "78bhyo5uvz3s2b";
	private static final String CLIENT_SECRET = Play.application().configuration().getString("linkedIn.secret");
	private static final String REDIRECT_URI = "http://localhost:9000/linkedin/callback";
	
	
	public static Result linkedInLogin(){
		return redirect("https://www.linkedin.com/uas/oauth2/authorization?response_type=code" + 
                                           "&client_id="+ CLIENT_ID +
                                           "&state="+ STATE +
                                           "&redirect_uri="+REDIRECT_URI);
	}
	
	
	public static Result linkedInOAuthCallBack(){
        String accessToken, finalResult;
		String code = request().getQueryString("code");
        if(code != null){
        	accessToken = parseTokenResponse(getAccessToken(code));
        	finalResult = accessToken;
        } else {
        	finalResult = "no code";
        }        
        return ok("access token : " + finalResult);
	}
	
	private static String getAccessToken(String code) {
		Logger.info("client secret : " + CLIENT_SECRET);
		String authURL = "https://www.linkedin.com/uas/oauth2/accessToken?";
	    String finalURL = authURL + "grant_type=authorization_code" +
	    		"&code=" + code + 
	    		"&redirect_uri=" + REDIRECT_URI +
	    		"&client_id=" + CLIENT_ID +
	    		"&client_secret=" + CLIENT_SECRET;
	    return makeRequest(finalURL);
	}
	
	//TODO : replace this with WS call
	private static String makeRequest(String finalURL){
		URL url;
	    String response = "", ret = "";
	    try {
	      HttpsURLConnection con;
	      Logger.debug("final URL = " + finalURL);
	      url = new URL(finalURL);
	      con = (HttpsURLConnection) url.openConnection();
	      Logger.debug("response = " + con.getResponseMessage());
		  BufferedReader br = 
			new BufferedReader(
				new InputStreamReader(con.getInputStream()));
	 
		  String input;
	 
		  while ((input = br.readLine()) != null){
			  response += input;
		  }
		  ret = response;
		  br.close();
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return ret;
	}

	private static String parseTokenResponse(String res){
		Logger.debug("Parsing " + res);
		JsonNode node = Json.parse(res);
		return node.findValue("access_token").asText();
	}
	
	public static Result index(){
		return ok("call linkedin/login");
	}
}
