package raisa.vis;

public class Sample {
    public float x;
    public float y;
    public float angle;
    public float distance;
    
    public Sample(float x, float y, String sample) {
        this.x = x;
        this.y = y;
        String[] sampleParts = sample.split("[,]");
        angle = (float)Math.toRadians(Integer.parseInt(sampleParts[0].substring(1).trim()));
        distance = Integer.parseInt(sampleParts[1].trim());
    }

    public Spot getSpot() {
        Spot spot = new Spot(x + (float)Math.cos(angle) * distance, y + (float)Math.sin(angle) * distance);
        return spot;
    }
}
