package tests;

import model.server.CollectoServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import protocol.ProtocolMessages;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    static BufferedReader reader;
    static BufferedWriter writer;
    static BufferedReader reader2;
    static BufferedWriter writer2;
    static short port = 2000;


    @Test
    @Order(1)
    void testUnvalidCommands() throws IOException {
        writer.write("LOGIN~DAN");
        writer.flush();
        writer.newLine();
        writer.flush();
        System.out.println(reader.readLine());
        System.out.println(reader.readLine());
        System.out.println(reader.readLine());
        System.out.println(reader.readLine());
        assertEquals(ProtocolMessages.ALREADYLOGGEDIN,reader.readLine());
    }
    @Test
    @Order(2)
    void testHello() throws IOException {
        writer.write("HELLO~DAN");
        writer.flush();
        writer.newLine();
        writer.flush();
        assertEquals("HELLO~Daniel's Collecto Server~CHAT",reader.readLine());
        writer2.write("HELLO~CACA");
        writer2.flush();
        writer2.newLine();
        writer2.flush();
        assertEquals("HELLO~Daniel's Collecto Server~CHAT",reader2.readLine());
    }

    /**
     * Test if login works as expected.
     * @throws IOException
     */
    @Test
    @Order(3)
    void testLogin() throws IOException {
        writer.write("LOGIN~DAN");
        writer.flush();
        writer.newLine();
        writer.flush();
        assertEquals("LOGIN",reader.readLine());
        writer2.write("LOGIN~CACA");
        writer2.flush();
        writer2.newLine();
        writer2.flush();
        assertEquals("LOGIN",reader2.readLine());
    }
    @BeforeAll
    static void beforeAll() throws IOException {
        CollectoServer.main(null);
        Socket s = new Socket("localhost",port);
        OutputStream stdin = s.getOutputStream(); // <- Eh?
        InputStream stdout = s.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stdout));
        writer = new BufferedWriter(new OutputStreamWriter(stdin));
        Socket s1 = new Socket("localhost",port);
        OutputStream stdin1 = s1.getOutputStream(); // <- Eh?
        InputStream stdout1 = s1.getInputStream();
        reader2 = new BufferedReader(new InputStreamReader(stdout1));
        writer2 = new BufferedWriter(new OutputStreamWriter(stdin1));
    }

    /**
     * Tests if after both clients queue for a game if the board is shown to both of the clients.
     * @throws IOException
     */
    @Test
    @Order(5)
    void testQueue() throws IOException {
            writer.write("QUEUE");
            writer.flush();
            writer.newLine();
            writer.flush();
            writer2.write("QUEUE");
            writer2.flush();
            writer2.newLine();
            writer2.flush();
            assertEquals(reader.readLine(),reader2.readLine());
    }

    /**
     * This tests if when a client makes a move the other receives the same move.
     * @throws IOException
     */
    @Test
    @Order(7)
    void testMove() throws IOException {
        writer.write("MOVE~10");
        writer.flush();
        writer.newLine();
        writer.flush();
        assertEquals("MOVE~10",reader2.readLine());
    }

    /**
     * This tests if after sending a LIST command it shows the correct players that are on the server.
     * @throws IOException
     */
    @Test
    @Order(4)
    void testList() throws IOException {
        writer2.write("LIST");
        writer2.flush();
        writer2.newLine();
        writer2.flush();
        assertEquals("LIST~DAN~CACA",reader2.readLine());
    }

    /**
     * This tests if the chat functionality works by sending a message from one client and see if if the other client recives the message.
     * @throws IOException
     */
    @Test
    @Order(6)
    void testChat() throws IOException {
        writer.write("CHAT~CHAT IS WORKING!");
        writer.flush();
        writer.newLine();
        writer.flush();
        assertEquals("CHAT~DAN~CHAT IS WORKING!",reader2.readLine());
        reader.readLine();
    }

}
