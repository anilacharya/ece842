//import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Demo {
	public static void main(String[] args) throws IOException {
		
		Scanner userInput = new Scanner(System.in);
		
		System.out.println("Enter configuration file path");
		String configFileName;
		configFileName = userInput.next();
		
		System.out.println("Enter local name");
		String localName;
		localName = userInput.next();

		final MessagePasser mp = new MessagePasser(configFileName, localName);
		
		final int localPortNum = mp.getNodeDetails().Port;
		//String localIPAddr = mp.getNodeDetails().IP;
		
		new Thread() {
			public void run() {
				ServerSocket receiverSocket = null;
				try {
					receiverSocket = new ServerSocket(localPortNum);
				} catch (Exception e){
					System.out.println("Exception " + e);
				}
				
				while(true) {
					try {
						final Socket listenSocket = receiverSocket.accept();
						new Thread() {
							public void run() {
								try {
									ObjectInputStream inputStream = new ObjectInputStream(listenSocket.getInputStream());
									Message msg = (Message) inputStream.readObject();
									mp.preReceive(msg);
									//mp.recMsgBuffer.add(msg);
								} catch (IOException | ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							};
						}.start();
					} catch (Exception e) {
						System.out.println("Exception " + e);
					}
				}
			};
		}.start();
		
		while(true) {
			
			System.out.println("Enter send/receive data");
			System.out.println("1. Send");
			System.out.println("2. Receive");
		
			String option;
			option = userInput.next();
			
			switch(Integer.parseInt(option)) {

			case 1:
				System.out.println("Enter destination");
				String dst = userInput.next();
				System.out.println("Enter kind");
				String kind = userInput.next();
				Object data = new Object();
				
				Message msg = new Message(localName, dst, kind, data);
				
				mp.send(msg);

				break;
				
			case 2:
				Message message = mp.receive();
				System.out.println(message.messageId);
				break;
				
			default:
				continue;
				
			}
		}
		
		
/*		
		MessagePasser mp = new MessagePasser("Lab0.yaml", "alice");
		
		System.out.println(mp.getSendAction("bob", "alice", "Ack", 4));
		System.out.println(mp.getSendAction("bob", "alice", "Lookup", 4));
		System.out.println(mp.getSendAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
		System.out.println(mp.getReceiveAction("charlie", "alice", "Lookup", 4));
*/		
	}
}
