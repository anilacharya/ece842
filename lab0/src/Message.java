import java.io.*;
//import java.util.*;
//import java.net.*;

public class Message implements Serializable {

	/**
	 * default serial version id 
	 */
	private static final long serialVersionUID = 1L;
	
	public int messageId;
	public String messageKind;
	public String messageSource;
	public String messageDest;
	public Object messageData;
	
	public Message(String src, String dest, String kind, Object data) {
		messageSource = src;
		messageDest = dest;
		messageKind = kind;
		messageData = data;
	}
	
	public void setId(int id) { // used by MessagePasser.send, not your app
		this.messageId = id;
	}
 
 // other accessors, toString, etc as needed
}
