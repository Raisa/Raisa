package raisa.vis;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Visualizer extends JPanel {
    public List<Spot> spots;
    
    public Visualizer() {
        setBackground(Color.black);
    }
    
    public void paintComponent(Graphics g) {
        float cx = getBounds().width * 0.5f;
        float cy = getBounds().height * 0.5f;
        setForeground(Color.gray);
        for (Spot spot : spots) {
            float w = 3.0f;
            float h = 3.0f;
            float x = cx + spot.x - 0.5f * w;
            float y = cy + spot.y - 0.5f * h;
            g.fillRect((int) x, (int) y, (int) w, (int) h);
        }
    }
    
    public static void main(String[] args) {
        ExampleWorld1 world = new ExampleWorld1();
        float x = 0.0f;
        float y = 0.0f;
        List<String> sampleStrings = new ArrayList<String>();
        for (int i = 0; i < 100; ++i) {
            sampleStrings.add(world.sample(x, y, i / 50.0f * (float)Math.PI));
        }
        List<Sample> samples = new ArrayList<Sample>();
        for (String str : sampleStrings) {
            samples.add(new Sample(x, y, str));
        }
        List<Spot> spots = new ArrayList<Spot>();
        for (Sample sample : samples) {
            spots.add(sample.getSpot());
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        Visualizer visualizer = new Visualizer();
        visualizer.spots = spots;
        frame.add(visualizer);
        frame.setVisible(true);
    }
}
