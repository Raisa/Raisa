package raisa.vis;

public class ExampleWorld1 {
    public String sample(float x, float y, float angle) {
        float distance = 0.0f;
        if (angle <= Math.PI * 0.25f || angle > Math.PI * 1.75f) {
            distance = 150.0f / (float)Math.cos(angle);
        } else if (angle <= Math.PI * 0.75f) {
            distance = 150.0f / (float) Math.cos(Math.PI * 0.5f - angle);
        } else if (angle <= Math.PI * 1.25f) {
            distance = 150.0f / (float)Math.cos(angle - Math.PI);
        } else if (angle <= Math.PI * 1.75) {
            distance = 150.0f / (float) Math.cos(Math.PI * 1.5f - angle);
        }
        float a = (float)Math.toDegrees(angle);
        String sampleString = String.format("J%1$3d,%2$3d\n", (int)a, (int) distance);
        //System.out.print(sampleString);
        return sampleString;
    }
}
