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

		// Example GET request to the root
		Request rootRequest = new Request("http://127.0.0.1/");
		int rootGetStatus = rootRequest.getRequest();
		System.out.println(rootGetStatus);
	}

}

