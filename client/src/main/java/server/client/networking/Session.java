package server.client.networking;

import lombok.Getter;
import lombok.Setter;
import server.client.gui.Direction;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Session
{
    private final String url = "http://0.0.0.0:8000/";
    private URL baseUrl;
    private String jwt = "";
    private HttpURLConnection conn;
    @Getter private String id;
    @Setter private String ip;
    @Setter private int port;
    @Getter private int counter;
    private PublicKey serverPublicKey;

    public Session() throws Exception
    {
        baseUrl = new URL(url);
        ip = "\"" + baseUrl.getHost() + "\"";
        port = baseUrl.getPort();
        generateServerPublicKey();

        conn = (HttpURLConnection) this.baseUrl.openConnection();
    }

    public Session(String url) throws Exception
    {
        baseUrl = new URL(url);
        ip = "\"" + baseUrl.getHost() + "\"";
        port = baseUrl.getPort();
        generateServerPublicKey();

        conn = (HttpURLConnection) this.baseUrl.openConnection();
    }

    public int getRequest() throws Exception
    {
        conn.setRequestMethod("GET");
        conn.connect();

        //Safety
        conn.disconnect();

        return conn.getResponseCode();
    }

    public int postAuthRequest(String id, String password, int delay, String steps) throws Exception
    {
        URL url = new URL(baseUrl.toString() + "auth");
        this.id = id;
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        //Set the request header content type
        conn.setRequestProperty("Content-Type", "application/json");
        //Set the response header content type
        conn.setRequestProperty("Accept", "application/json");
        // Enable write access to output stream
        conn.setDoOutput(true);

        //Write JSON string to output stream as bytes
        String jsonBodyString = formatAuthRequestBody(
                encrypt(id),
                encrypt(password),
                delay,
                steps);
        System.out.println(jsonBodyString);

        OutputStream os = conn.getOutputStream();
        byte[] input = jsonBodyString.getBytes("utf-8");
        os.write(input, 0, input.length);

        //Read jwt if successful
        if(conn.getResponseCode() == 201)
            jwt = jwtParser(getResponseBody());

        //Safety
        conn.disconnect();
        System.out.println(conn.getResponseMessage());
        return conn.getResponseCode();
    }

    public int postChangeRequest(Direction direction, String id, int amount) throws Exception
    {
        String path = changeRequestPath(direction.toString());

        URL url = new URL(baseUrl.toString() + path);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        //Set the request header content type
        conn.setRequestProperty("Content-Type", "application/json");
        //Set the response header content type
        conn.setRequestProperty("Accept", "application/json");
        // Enable write access to output stream
        conn.setDoOutput(true);

        //Write JSON string to output stream as bytes
        String jsonBodyString = RequestBody.formatChangeRequest(
                id,
                jwt,
                amount);
        System.out.println(jsonBodyString);

        OutputStream os = conn.getOutputStream();
        byte[] input = jsonBodyString.getBytes("utf-8");
        os.write(input, 0, input.length);

        if (conn.getResponseCode() == 200)
            counter = counterParser(getResponseBody());

        //Safety
        conn.disconnect();

        return conn.getResponseCode();
    }

    public String formatAuthRequestBody(String id, String password, int delay, String steps)
    {
        return RequestBody.formatAuthRequest(id,
                password,
                delay,
                steps,
                port,
                ip);
    }

    private String getResponseBody() throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String response = "";
        while((line = br.readLine()) != null)
        {
            response = response + line;
        }
        return response;
    }

    private String jwtParser(String responseMessage)
    {
        int jsonIndexStartSeparator = responseMessage.indexOf(":\"") + 1;
        int jsonIndexEndSeparator = responseMessage.indexOf("\"}") + 1;
        String jwtFormatted = responseMessage.substring(jsonIndexStartSeparator, jsonIndexEndSeparator).replace("\"", "");
        return jwtFormatted;
    }

    private int counterParser(String responseMessage)
    {
        int jsonIndexStartSeparator = responseMessage.indexOf(":") + 1;
        int jsonIndexEndSeparator = responseMessage.indexOf("}");
        String counterValue = responseMessage.substring(jsonIndexStartSeparator, jsonIndexEndSeparator);
        return Integer.parseInt(counterValue);
    }

    private String changeRequestPath(String change)
    {
        switch(change)
        {
            case "INCREASE":
                return "increase";
            case "DECREASE":
                return "decrease";
            default:
                return "";
        }
    }

    private void generateServerPublicKey() throws Exception
    {
        String publicKeyString = serverPublicKeyRequest();
        generateServerPublicKeyObject(publicKeyString);
    }

    // Encrypt any string using the server public key
    public String encrypt(String inputString) throws Exception
    {
        // Turn key and input to byte arrays
        byte[] input = inputString.getBytes();

        // Create Instance of Cipher and encode input using key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        byte[] encrypted = cipher.doFinal(input);

        return new String(Base64.getEncoder().encode(encrypted));
    }
    private String serverPublicKeyRequest() throws Exception
    {
        URL url = new URL(baseUrl.toString() + "public_key");
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        //Get the public key
        String public_key = publicKeyParser(getResponseBody());

        //Safety
        conn.disconnect();

        return public_key;
    }
    private String publicKeyParser(String responseMessage)
    {
        int jsonIndexStartSeparator = responseMessage.indexOf(":\"") + 2;
        int jsonIndexEndSeparator = responseMessage.indexOf("\"}");
        String publicKey = responseMessage.substring(jsonIndexStartSeparator, jsonIndexEndSeparator);
        return publicKey;
    }

    private void generateServerPublicKeyObject(String publicKeyString) throws Exception
    {
        byte[] key = publicKeyString.getBytes();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        serverPublicKey = keyFactory.generatePublic(keySpec);
    }
}



