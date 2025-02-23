//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//public class MapPanel extends JPanel {
//    public MapPanel() {
//        GameWindow gameWindow = new GameWindow();
//        // Add mouse listener to handle clicks
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//            Point clickedPoint = e.getPoint();
//
//            // Check if the click is within any region
//            for (Region region : gameWindow.getRegions()) {
//                if (region.contains(clickedPoint)) {
//                    gameWindow.setSelectedRegion(region);
//                }
//            }
//            repaint(); // Redraw the panel
//            }
//        });
//    }
//    @Override
//    protected void paintComponent(Graphics g) {
//
//        GameWindow gameWindow = new GameWindow();
//        super.paintComponent(g);
//        // Draw the world map
//        g.drawImage(gameWindow.getWorldMapImage(), 0, 0, getWidth(), getHeight(), this);
//
//        // Draw the interactive regions (semi-transparent)
//        for (Region region : gameWindow.getRegions()) {
//            g.setColor(new Color(0, 0, 255, 50)); // Semi-transparent blue
//            g.fillRect(region.getBounds().x, region.getBounds().y,
//                    region.getBounds().width, region.getBounds().height);
//
//            // Highlight the selected region
//            if (region == gameWindow.getSelectedRegion()) {
//                g.setColor(new Color(255, 0, 0, 100)); // Semi-transparent red
//                g.fillRect(region.getBounds().x, region.getBounds().y,
//                        region.getBounds().width, region.getBounds().height);
//            }
//
//            // Draw region name
//            g.setColor(Color.BLACK);
//            g.drawString(region.getName(),
//                    region.getBounds().x + 10,
//                    region.getBounds().y + 20);
//        }
//    }
//}
