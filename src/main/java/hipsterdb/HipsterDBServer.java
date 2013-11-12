package hipsterdb;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import com.github.kohanyirobert.ebson.BsonDocument;
import com.github.kohanyirobert.ebson.BsonDocuments;

public class HipsterDBServer {

	public Map<String, ByteBuffer> memMap = new HashMap<String, ByteBuffer>();
	private Socket conn;
	private ServerSocket sock;

	/**
	 * @return the memMap
	 */
	public Map<String, ByteBuffer> getMemMap() {
		return memMap;
	}

	public void createServerSocket(String mode) throws IOException {
		if (mode.equals("server")) {
			sock = new ServerSocket(11256);
			while (true) {
				conn = sock.accept();
				Thread t = new Thread() {
					@Override
					public void run() {
						try {
							handleConnection(conn);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				t.start();
			}
		}

	}

	public synchronized static ByteBuffer allocateMap(ByteBuffer byteBuffer,
			int size, boolean direct) {
		if (byteBuffer == null
				|| (byteBuffer.capacity() - byteBuffer.limit() < size)) {
			if (direct)
				byteBuffer = ByteBuffer.allocateDirect(size);
			else
				byteBuffer = ByteBuffer.allocate(size);
		}

		byteBuffer.limit(byteBuffer.position() + size);
		ByteBuffer view = byteBuffer.slice();
		byteBuffer.position(byteBuffer.limit());

		return view;
	}

	public void put(String key, String json) {
		BsonDocument document = BsonDocuments.of(key, json);
		// grab a little-endian byte buffer
		ByteBuffer buffer = ByteBuffer.allocate(200000).order(
				ByteOrder.LITTLE_ENDIAN);
		// use the documents utility class to write the document into the buffer
		BsonDocuments.writeTo(buffer, document);
		// use the serialized data
		buffer.flip();

		// add to the memMap
		memMap.put(key, buffer);
	}

	public Object get(String key) {
		BsonDocument newDocument = BsonDocuments.readFrom(getMemMap().get(key));
		return newDocument.get(key);

	}

	public void flushToDisk(String filename) throws ClassNotFoundException {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filename + ".hip");

			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(memMap);
			oos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readFromDisk(String filename) throws IOException,
			ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filename + ".hip");
		ObjectInputStream ois = new ObjectInputStream(fis);
		ois.close();

		// setMemMap(dataMap);

	}

	private void handleConnection(Socket conn) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		PrintWriter out = new PrintWriter(conn.getOutputStream(), false);

		while (true) {
			String line = in.readLine();
			if (line.equals("")) {
				out.printf("Cannot do anything with a blank line!\r\n");
				out.flush();
				break;
			}
			String[] parts = line.split("/");
			if (parts.length < 2) {
				out.printf("Insufficient arguments!\r\n");
				out.flush();
				break;
			} else {
				String cmd = parts[0];
				Object data;
				switch (cmd) {
				case "put":
					if (parts.length > 2) {
						// check for key AND value...
						put(parts[1], parts[2]);
						out.printf("Put " + parts[1] + " "
								+ parts[2] + "\r\n");
					}
					break;
				case "get":
					out.printf("Getting key " + parts[1] + "\r\n");
					data = get(parts[1]);
					out.printf("%s\r\n", data);
					break;
				
				default: 
					out.printf("Unknown command!\r\n");
				}
				out.flush();
			}
		}

	}
}