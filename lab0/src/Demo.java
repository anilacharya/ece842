import java.io.IOException;


public class Demo {
	public static void main(String[] args) throws IOException {
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
	}
}