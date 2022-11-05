package server.client;

import org.junit.jupiter.api.Test;
import server.client.networking.Session;

public class EncryptionTest
{
    @Test
    void testEncryption()
    {
        try
        {
            Session session = new Session();
            String message = "thisisasecretmessage";
            String encryptedMessage = session.encrypt(message);
            System.out.println("Encrypted message: " + encryptedMessage);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
