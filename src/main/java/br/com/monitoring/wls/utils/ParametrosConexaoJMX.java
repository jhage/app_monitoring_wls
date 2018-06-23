package br.com.monitoring.wls.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParametrosConexaoJMX {
	
	private static final Logger logger = LoggerFactory.getLogger(ParametrosConexaoJMX.class);
	
	private String host;
	private Integer port;
	private String type;
	private String user;
	private String pass;
	
	public ParametrosConexaoJMX(String host, Integer port, String user, String pass, String type) {
		
		this.host = host;
		this.port = port;
		this.decoderUserSenha( user, pass);
	}
	
	private void decoderUserSenha(String user, String senha) {
    	
    	try {
    		
    		this.user = URLDecoder.decode(user, "UTF-8");
    		this.pass = URLDecoder.decode(senha, "UTF-8");
		}catch (UnsupportedEncodingException e) {
			
			logger.error("Error on processing URLDecoder", e);
		}
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}


	public static Logger getLogger() {
		return logger;
	}


	public String getType() {
		return type;
	}
}
