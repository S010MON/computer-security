package server.client.networking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ClientApplication.class, args);

		try
		{
			// Example GET request to the root
			Session rootRequest = new Session("http://127.0.0.1/");
			int rootGetStatus = rootRequest.getRequest();
			System.out.println(rootGetStatus);

			// Example POST request to create Client
			Session authRequest = new Session("http://127.0.0.1/");
			int authStatus = authRequest.postAuthRequest(1, "pass", 100, "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]");
			System.out.println(authStatus);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

