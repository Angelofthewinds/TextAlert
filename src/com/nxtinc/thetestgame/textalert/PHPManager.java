package com.nxtinc.thetestgame.textalert;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PHPManager {

	private TextAlert plugin;
	
	public PHPManager(TextAlert plugin)
	{
		this.plugin = plugin;
	}
	
	public void Send(String type, String subject, String message, String address, String number)
	{
		try
		{
			String urls = plugin.getConfig().getString("phpurl");
			URL url = new URL(urls);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			
			String input = "type=" + type + "&subject=" + subject + "&message=" + message + "&address=" + address + "&number=" + number;
			
			out.writeBytes(input);
			out.flush();
			out.close();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line = "";
			while ((line=in.readLine())!=null)
			{
				plugin.info(line);
			}
			
		}
		catch(MalformedURLException ex)
		{  
			ex.printStackTrace();
		}

		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
