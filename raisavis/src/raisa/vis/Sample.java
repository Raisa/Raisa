package raisa.vis;

public class Sample {
    public float x;
    public float y;
    public float angle;
    public float distance;
    
    public Sample(float x, float y, String sample) {
        this.x = x;
        this.y = y;
        angle = (float)Math.toRadians(Integer.parseInt(sample.substring(1, 4).trim()));
        distance = Integer.parseInt(sample.substring(5, 8).trim());
    }

    public Spot getSpot() {
        Spot spot = new Spot(x + (float)Math.cos(angle) * distance, y + (float)Math.sin(angle) * distance);
        return spot;
    }
}
