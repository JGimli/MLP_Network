import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataVisualizer extends JFrame {

    private Map<String, List<Point>> categorizedData = new HashMap<>();

    public DataVisualizer(String filename) {
        setTitle("Data Visualization");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        categorizedData.put("P", new ArrayList<>());
        categorizedData.put("R", new ArrayList<>());
        categorizedData.put("S", new ArrayList<>());

        loadData(filename);

        add(new DataPanel(), BorderLayout.CENTER);
    }

    private void loadData(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0) {
                    try {
                        double x = Double.parseDouble(values[0]);
                        double y = Double.parseDouble(values[1]);
                        String category = values[values.length - 1].trim(); // Get the last value

                        categorizedData.get(category).add(new Point((int) (x * 200), (int) (y * 200))); // Scale for better visualization
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        System.err.println("Error parsing line: " + line);
                        continue; // Skip to the next line
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class DataPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Define colors for categories
            Map<String, Color> categoryColors = new HashMap<>();
            categoryColors.put("P", Color.RED);
            categoryColors.put("R", Color.GREEN);
            categoryColors.put("S", Color.BLUE);

            for (Map.Entry<String, List<Point>> entry : categorizedData.entrySet()) {
                String category = entry.getKey();
                List<Point> points = entry.getValue();
                Color color = categoryColors.get(category);

                g.setColor(color);
                for (Point point : points) {
                    g.fillOval(point.x + 100, point.y + 100, 5, 5); // Offset to ensure visibility
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataVisualizer dv = new DataVisualizer("Train.txt");
            dv.setVisible(true);
        });
    }
}