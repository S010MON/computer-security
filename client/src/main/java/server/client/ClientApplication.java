package server.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


@SpringBootApplication
public class ClientApplication {
	public static void main(String[] args)
	{
		SpringApplication.run(ClientApplication.class, args);

		try
		{
			URL url = new URL("http://127.0.0.1/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			System.out.println("Response message: " + conn.getResponseMessage());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

}

