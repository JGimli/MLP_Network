import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Okno extends JFrame {

    MyComponent komponent = null;

    class MyComponent extends JComponent {
        private ArrayList<DrawPoint> points = new ArrayList<>();
        private Color currentColor = Color.BLACK;
        private int brushSize = 5;

        public MyComponent() {
            setBackground(Color.WHITE);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    points.add(new DrawPoint(e.getX(), e.getY(), currentColor, brushSize));
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (DrawPoint p : points) {
                g.setColor(p.color);
                g.fillOval(p.x - p.size / 2, p.y - p.size / 2, p.size, p.size);
            }
        }

        public void clear() {
            points.clear();
            repaint();
        }

        public void setColor(Color color) {
            this.currentColor = color;
        }

        public void setBrushSize(int size) {
            this.brushSize = size;
        }
    }

    class DrawPoint {
        int x, y, size;
        Color color;

        public DrawPoint(int x, int y, Color color, int size) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = size;
        }
    }

    public Okno(String string) {
        super(string);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension dim = kit.getScreenSize();
        setBounds(dim.width / 4, dim.height / 4, dim.width * 2 / 3, dim.height * 2 / 3);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel buttonPanel = new JPanel();

        JButton clearButton = new JButton("Wyczyść");
        clearButton.addActionListener(e -> komponent.clear());
        buttonPanel.add(clearButton);

        JButton colorButton = new JButton("Kolor");
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Wybierz kolor", komponent.currentColor);
            if (newColor != null) {
                komponent.setColor(newColor);
            }
        });
        buttonPanel.add(colorButton);

        JSlider sizeSlider = new JSlider(1, 20, 5);
        sizeSlider.addChangeListener(e -> komponent.setBrushSize(sizeSlider.getValue()));
        buttonPanel.add(new JLabel("Rozmiar:"));
        buttonPanel.add(sizeSlider);

        add(buttonPanel, BorderLayout.SOUTH);
        add(komponent = new MyComponent(), BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Okno("Interfejs"));
    }
}
