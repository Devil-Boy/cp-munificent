package pgDev.bukkit.CPMunificent;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Properties;

public class CPMConfig {
	private Properties properties;
	private final CPMMain plugin;
	public boolean upToDate = true;
	
	// List of Config Options
	int grantInterval;
	float grantAmount;
	
	public CPMConfig(Properties p, CPMMain plugin) {
		properties = p;
        this.plugin = plugin;
        
        // Grab values here.
        grantInterval = getInt("grantInterval", 600);
        grantAmount = (float) getDouble("grantAmount", 0.6);
	}
	
	// Value obtaining functions down below
	public int getInt(String label, int thedefault) {
		String value;
        try {
        	value = getString(label);
        	return Integer.parseInt(value);
        } catch (NoSuchElementException e) {
        	return thedefault;
        }
    }
    
    public double getDouble(String label, double thedefault) throws NoSuchElementException {
        try {
	    	String value = getString(label);
	        return Double.parseDouble(value);
        } catch (NoSuchElementException e) {
        	return thedefault;
        }
    }
    
    public File getFile(String label) throws NoSuchElementException {
        String value = getString(label);
        return new File(value);
    }

    public boolean getBoolean(String label, boolean thedefault) {
    	String values;
        try {
        	values = getString(label);
        	return Boolean.valueOf(values).booleanValue();
        } catch (NoSuchElementException e) {
        	return thedefault;
        }
    }
    
    public Color getColor(String label) {
        String value = getString(label);
        Color color = Color.decode(value);
        return color;
    }
    
    public HashSet<String> getSet(String label, String thedefault) {
        String values;
        try {
        	values = getString(label);
        } catch (NoSuchElementException e) {
        	values = thedefault;
        }
        String[] tokens = values.split(",");
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < tokens.length; i++) {
            set.add(tokens[i].trim().toLowerCase());
        }
        return set;
    }
    
    public LinkedList<String> getList(String label, String thedefault) {
    	String values;
        try {
        	values = getString(label);
        } catch (NoSuchElementException e) {
        	values = thedefault;
        }
        if(!values.equals("")) {
            String[] tokens = values.split(",");
            LinkedList<String> set = new LinkedList<String>();
            for (int i = 0; i < tokens.length; i++) {
                set.add(tokens[i].trim().toLowerCase());
            }
            return set;
        }else {
        	return new LinkedList<String>();
        }
    }
    
    public String getString(String label) throws NoSuchElementException {
        String value = properties.getProperty(label);
        if (value == null) {
        	upToDate = false;
            throw new NoSuchElementException("Config did not contain: " + label);
        }
        return value;
    }
    
    public String getString(String label, String thedefault) {
    	String value;
    	try {
        	value = getString(label);
        } catch (NoSuchElementException e) {
        	value = thedefault;
        }
        return value;
    }
    
    public String linkedListToString(LinkedList<String> list) {
    	if(list.size() > 0) {
    		String compounded = "";
    		boolean first = true;
        	for (String value : list) {
        		if (first) {
        			compounded = value;
        			first = false;
        		} else {
        			compounded = compounded + "," + value;
        		}
        	}
        	return compounded;
    	}
    	return "";
    }
    
    
    // Config creation method
    public void createConfig() {
    	try{
    		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(plugin.pluginConfigLocation)));
    		out.write("#\r\n");
    		out.write("# CommandPointsMunificent Configuration\r\n");
    		out.write("#\r\n");
    		out.write("\r\n");
    		out.write("# Grant Interval\r\n");
    		out.write("#	This is the time interval (in seconds) that\r\n");
    		out.write("#	all players on the server will receive points during.\r\n");
    		out.write("grantInterval=" + grantInterval + "\r\n");
    		out.write("\r\n");
    		out.write("# Grant Amount\r\n");
    		out.write("#	This is the quantity of commandpoints the players will\r\n");
    		out.write("#	receive during each interval. It can be a decimal to\r\n");
    		out.write("#	allow for partial points.\r\n");
    		out.write("#	Players are not granted a full point until they have\r\n");
    		out.write("#	gained enough partial points to sum up to a full point.\r\n");
    		out.write("grantAmount=" + grantAmount + "\r\n");
    		out.close();
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }
}
