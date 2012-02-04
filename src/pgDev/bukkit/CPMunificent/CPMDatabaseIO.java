package pgDev.bukkit.CPMunificent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ConcurrentHashMap;

public class CPMDatabaseIO {
	
	public static void saveDB(ConcurrentHashMap<String, Float> db) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CPMMain.dbLocation)));
			for (String name : db.keySet()) {
				out.write(name + "=" + db.get(name) + "\r\n");
			}
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find PartialPoints database.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ConcurrentHashMap<String, Float> getDB() {
		ConcurrentHashMap<String, Float> output = new ConcurrentHashMap<String, Float>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(CPMMain.dbLocation)));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("=");
				try {
					output.put(parts[0], Float.parseFloat(parts[1]));
				} catch (NumberFormatException e) {
					System.out.println("Error loading partial-points of: " + parts[0]);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not find PartialPoints database.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}
	
}
