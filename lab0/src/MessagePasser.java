import java.io.*;
import java.util.*;
import java.net.*;

import org.yaml.snakeyaml.*;

public class MessagePasser {
	
	public static int newMsgId;
	ArrayList<Message> recMsgBuffer;
	ArrayList<Message> recDelayBuffer;
	ArrayList<Message> sendMsgBuffer;
	// Local information for this node
	//private Socket thisSocket;
	private String local_name;
	private PortIP local_portip;
	
	// Sockets to other nodes
	private Map<String, PortIP> otherSockets;
	
	// Send and receive rules; of the form {Name=name, Src=src, N=n}, where n is a number counting the times the rule was applied
	private List<Map> sendRules;
	private List<Map> receiveRules;
	
	public enum Action {drop, duplicate, delay};
	File config;	
	Yaml yaml;
		
	 public MessagePasser(String configuration_filename, String local_name) throws IOException {
		 
		 newMsgId = 0;
		 this.recMsgBuffer = new ArrayList<Message>();
		 this.recDelayBuffer = new ArrayList<Message>();
		 this.sendMsgBuffer = new ArrayList<Message>();
		 this.local_name = local_name;
		 local_portip = null;
		 //thisSocket = null;
		 
		 yaml = new Yaml();
		 
		 config = new File(configuration_filename);
		 
		 loadSockets();
		 updateFile();
		 
	 }
	 
	 public PortIP getNodeDetails() {
		 return local_portip;
	 }
	 
	 public PortIP getRemoteDetails(String r_name) {
		 return otherSockets.get("r_name");
	 }
	 
	 private void loadSockets() throws IOException {
		 Reader reader = new FileReader(config);
		 Map<String,List<Map>> yamlMap = (Map<String,List<Map>>) yaml.load(reader);
		 reader.close();
		 
		 // Parse through the sockets in the configuration file
		 List<Map> config = yamlMap.get("Configuration");
		 for(Map cfg : config) {
			 System.out.println(cfg);
			 // Set up the local socket
			 if(cfg.get("Name").equals(local_name)) {
				 int l_port = (int) cfg.get("Port");
				 String l_ip = (String) cfg.get("IP");
				 //thisSocket = new Socket(l_ip, l_port);
				 local_portip = new PortIP(l_port, l_ip);
			 } else {
				 String o_name = (String) cfg.get("Name");
				 String o_ip = (String) cfg.get("IP");
				 int o_port = (int) cfg.get("Port");
				 PortIP temp_portip = new PortIP(o_port, o_ip);
				 otherSockets.put(o_name, temp_portip);
			 }
		 }
	 }
	 
	 private void updateFile() throws IOException {
		 
		 Reader reader = new FileReader(config);
		 Map<String,List<Map>> yamlMap = (Map<String,List<Map>>) yaml.load(reader);
		 reader.close();
		 
		 sendRules = yamlMap.get("SendRules");
		 receiveRules = yamlMap.get("ReceiveRules");
		 
		 // zero the instances of each rule being called
		 for (Map rule : sendRules) {
			 rule.put("N", "0");
		 }
		 for (Map rule : receiveRules) {
			 rule.put("N", "0");
			 System.out.println(rule);
		 }
	 }
	 
	 public Action getSendAction(String src, String dest, String kind, int id) {
		 return getAction(src, dest, kind, id, sendRules);
	 }
	 
	 public Action getReceiveAction(String src, String dest, String kind, int id) {
		 return getAction(src, dest, kind, id, receiveRules);
	 }
	 
	 private Action getAction(String src, String dest, String kind, int id, List<Map> rules) {
		 for (Map rule : rules) {
			 if((!rule.containsKey("Src") || rule.get("Src").equals(src)) &&
				(!rule.containsKey("Dest") || rule.get("Dest").equals(dest)) &&
				(!rule.containsKey("Kind") || rule.get("Kind").equals(kind)) &&
				(!rule.containsKey("ID") || (int) (rule.get("ID")) == id)) {
				 	// Update the number of times the rule has been called
				 	rule.put("N", "" + (Integer.parseInt((String) rule.get("N")) + 1));
				 	
				 	// Check for Nth and EveryNth rules
				 	if ((!rule.containsKey("Nth") || (int) rule.get("Nth") == Integer.parseInt((String) rule.get("N"))) &&
				 		(!rule.containsKey("EveryNth") || Integer.parseInt((String) rule.get("N")) % ((int)rule.get("EveryNth")) == 0)) {
				 			return Action.valueOf((String)rule.get("Action"));
				 	}
			 }
		 }
		 return null;
	 }

	 void checkSendBuffer() {
		 for(int i = 0; i < sendMsgBuffer.size(); i++) {
			 sendMessage(sendMsgBuffer.get(i));
		 }
		 sendMsgBuffer.clear();
	 }
	 
	 void sendMessage(Message message) {
		 PortIP destSocket = getRemoteDetails(message.messageDest);
		 try {
			Socket sendSocket = new Socket(destSocket.IP, destSocket.Port);
			ObjectOutputStream outStream = new ObjectOutputStream(sendSocket.getOutputStream());
			outStream.writeObject(message);
			sendSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
	 void send(Message message) {
			newMsgId ++;
			message.setId(newMsgId);
			
			Action sendAction = getSendAction(message.messageSource, message.messageDest, message.messageKind, message.messageId);
			
			switch(sendAction) {
			case delay:
				sendMsgBuffer.add(message);
				break;
			case drop:
				break;
			case duplicate:
				sendMessage(message);
				sendMessage(message);
				break;
			default:
				checkSendBuffer();
				sendMessage(message);
			}
	 }
	 
	 void preReceive(Message message) {
		 
		 Action recAction = getReceiveAction(message.messageSource, message.messageDest, message.messageKind, message.messageId);
		 
			switch(recAction) {
			case delay:
				recDelayBuffer.add(message);
				break;
			case drop:
				break;
			case duplicate:
				recMsgBuffer.add(message);
				recMsgBuffer.add(message);
				break;
			default:
				for(int i = 0; i < recDelayBuffer.size(); i++) {
					recMsgBuffer.add(recDelayBuffer.get(i));
				}
				recDelayBuffer.clear();
				recMsgBuffer.add(message);
			}
	 }
	 
	 Message receive( ) {// may block
		 int first = 0;
		 
		 if(recMsgBuffer.size() != 0) {
			 return recMsgBuffer.remove(first);
		 }
		 
		 return null;
	 }
	 
}
