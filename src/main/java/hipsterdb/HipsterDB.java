package hipsterdb;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.json.simple.JSONObject;

public class HipsterDB {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		HipsterDBServer server = new HipsterDBServer();
		server.createServerSocket("server");

		// EXAMPLE CLIENT CODE FOR "local" TESTING
		
//		JSONObject obj = new JSONObject();
//
//		for (int i = 0; i < 1000; i++) {
//			obj.put("name", "Paul");
//			obj.put("num", new Integer(i));
//			obj.put("surname", "Scott");
//			obj.put("date", new Date());
//			
//			StringWriter out = new StringWriter();
//			obj.writeJSONString(out);
//
//			String jsonText = out.toString();
//
//			String key = "mystuff_"+i;
//
//			server.put(key, jsonText);
//
//		}
//		
//		System.out.println(server.get("mystuff_100"));
	}

}
