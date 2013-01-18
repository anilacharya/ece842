import java.io.*;
import java.util.*;
import java.net.*;

public class MessagePasser {
 public MessagePasser(String configuration_filename, String local_name) throws IOException {
	 FileInputStream fstream = new FileInputStream(configuration_filename);
	 DataInputStream in = new DataInputStream(fstream);
	 BufferedReader cfBuffer = new BufferedReader(new InputStreamReader(in));
	 System.out.println(cfBuffer.readLine());
 }
 
 void send(Message message) {
	 
 }
 
 Message receive( ) {// may block
	 return null;
 }
}