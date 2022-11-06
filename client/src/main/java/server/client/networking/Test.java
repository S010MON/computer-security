package server.client.networking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Test
{
    public static void main(String[] args)
    {
        SpringApplication.run(Test.class, args);

        try
        {
//			System.out.println("Seeing if we're a teapot");
//			// Example GET request to the root
//			Session rootRequest = new Session("http://0.0.0.0:8000/");
//			int rootGetStatus = rootRequest.getRequest();
//			System.out.println(rootGetStatus);
//
//			// Example GET public key
//			System.out.println("Getting public key");
//			Session getPublicKey = new Session("http://0.0.0.0:8000/");
//			String public_key = getPublicKey.getPublicKeyRequest();

            System.out.println("Posting auth request");
            // Example POST request to create Client
            Session authRequest = new Session("http://0.0.0.0:8000/");
            int authStatus = authRequest.postAuthRequest(Integer.toString(1), "pasHsHs", 1, "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]");
            System.out.println(authStatus);

//            System.out.println("Posting auth request with encryption");
//            // Example POST request to create Client
//            Session authRequestEncrypted = new Session("http://0.0.0.0:8000/");
//            int authStatusEncrypted = authRequestEncrypted.postAuthRequestEncrypted(1, "pass", 100, "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]");
//            System.out.println(authStatusEncrypted);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}