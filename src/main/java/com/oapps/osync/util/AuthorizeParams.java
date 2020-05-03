package com.oapps.osync.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Component
public class AuthorizeParams {

	private String client_id;
	private String client_secret;
	private String code;
	private String state;
	private String location;
	private String context;
	private String scope;
	private String grant_type;
	private String redirect_uri;
	
	private String access_token;
	private String token_type;
	private String expires_in;
	private String refresh_token;
	private String created_at;
}
