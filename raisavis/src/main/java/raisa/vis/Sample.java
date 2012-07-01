package raisa.vis;

import java.util.HashMap;
import java.util.Map;

public class Sample {
	public Map<String, Object> data = new HashMap<String, Object>();
    
    public Sample(float x, float y, String sample) {
    	data.put("x", x);
    	data.put("y", y);
        String[] sampleParts = sample.split("[;]");
        for (String part : sampleParts) {
        	if ("STA".equals(part)) {        		
        	} else if ("END".equals(part)) {
        	} else if (part.startsWith("IR")) {
                float angle = (float)Math.toRadians(Integer.parseInt(part.substring(2)));
            	data.put("ir", angle);
        	} else if (part.startsWith("ID")) {
                float distance = Integer.parseInt(part.substring(2));
        		data.put("id", distance);
        	} else {        		
        	}
        }
    }

	public boolean isSpot() {
		return data.containsKey("IR") && data.containsKey("ID");
	}

    public Spot getSpot() {
    	float x = (Float)data.get("x");
    	float y = (Float)data.get("y");
    	float angle = (Float)data.get("ir");
    	float distance = (Float)data.get("id");
        Spot spot = new Spot(x + (float)Math.cos(angle) * distance, y + (float)Math.sin(angle) * distance);
        return spot;
    }
}
