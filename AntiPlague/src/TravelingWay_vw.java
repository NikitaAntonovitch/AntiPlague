import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class TravelingWay_vw {
    JPanel container_panel;
    private Point P1;
    private Point P2;
    private JCheckBox checkboxBanTraveling;
    private Image transport_Image;
    private Timer timer;

    private Point transport_coordinates;//coordinates will change during animation in a parallel thread by timer
    private Direction direction; // Added direction field

    //public TravelingWay_vw(JPanel container, Point p1, Point p2, boolean bHorizontal, boolean bDiagonal, Transport transport_type) {
    public TravelingWay_vw(JPanel container, Point p1, Point p2, Direction direction, Transport transport_type) {
        P1 = p1;
        P2 = p2;

        this.direction = direction;

        container_panel = container;
        transport_coordinates = new Point(P1);//starting coordinates at point P1
        container.setLayout(null);

        checkboxBanTraveling = new JCheckBox();
        checkboxBanTraveling.setToolTipText("block traveling");
        checkboxBanTraveling.setName("travelCB");

        container.add(checkboxBanTraveling);
        checkboxBanTraveling.setSelected(true);

        // set direction of travel line
        switch (direction) {
            case Direction.Horizontal -> checkboxBanTraveling.setLocation((p1.x + (p2.x - p1.x) / 2), p1.y - 5);
            case Direction.Diagonal -> checkboxBanTraveling.setLocation((p1.x + (p2.x - p1.x) / 2), (p1.y + (p2.y - p1.y) / 2) - 5);
            case Direction.Vertical -> checkboxBanTraveling.setLocation(p1.x - 5, (p1.y + (p2.y - p1.y) / 2));
        }

        checkboxBanTraveling.setSize(20, 15);

        // get image of correct transport
        switch (transport_type) {
            case Transport.PLANE:
                transport_Image = new ImageIcon("Img/airplane.jpg").getImage();
                break;
            case Transport.CAR:
                transport_Image = new ImageIcon("Img/car.jpg").getImage();
                break;
            case Transport.SHIP:
                transport_Image = new ImageIcon("Img/ship.jpg").getImage();
                break;
            default:
                transport_Image = new ImageIcon("Img/airplane.jpg").getImage();
                break;
        }
        //Start animation
        animateTransport();
    }

    public void paintDirection(Graphics g) {
        // Cast Graphics to Graphics2D for more control (optional)
        Graphics2D g2d = (Graphics2D) g;

        Stroke stroke = g2d.getStroke(); // get style of the line
        g2d.setStroke(new BasicStroke(5)); // set up Line thickness of 5
        Color clr = g2d.getColor();
        g2d.setColor(Color.pink); // Set line color

        // Draw a line from P1 to P2
        g2d.drawLine(P1.x, P1.y, P2.x, P2.y);

        // get direction and logic how to draw it on the map
        switch (direction) {
            case Direction.Horizontal -> checkboxBanTraveling.setLocation((P1.x + (P2.x - P1.x) / 2), P1.y - 5);
            case Direction.Diagonal -> checkboxBanTraveling.setLocation((P1.x + (P2.x - P1.x) / 2), (P1.y + (P2.y - P1.y) / 2) - 5);
            case Direction.Vertical -> checkboxBanTraveling.setLocation(P1.x - 5, (P1.y + (P2.y - P1.y) / 2));
        }

        //draw image
        g.drawImage(transport_Image, transport_coordinates.x, transport_coordinates.y, 20, 20, container_panel);
        //return back styles
        g2d.setStroke(stroke);
        g2d.setColor(clr);
    }
    private void animateTransport() {//animation of transport with help of the timer thread
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if (checkbox.get) // check if checkbox is checked then animate, otherwise omit animation !!!!!!!!!
                // Diagonal movement
                switch (direction) {
                    case Direction.Diagonal:
                        if (transport_coordinates.x < P2.x && transport_coordinates.y < P2.y) {
                            transport_coordinates.x += 5; // Move horizontally
                            transport_coordinates.y += 5; // Move vertically
                        } else {
                            transport_coordinates.x = P1.x; // Return to starting position horizontally
                            transport_coordinates.y = P1.y; // Return to starting position vertically
                        }
                        break;

                    case Direction.Horizontal:
                        if (transport_coordinates.x < P2.x) {
                            transport_coordinates.x += 5; // Move horizontally
                        } else {
                            transport_coordinates.x = P1.x; // Return to starting position horizontally
                        }
                        break;

                    case Direction.Vertical:
                        if (transport_coordinates.y < P2.y) {
                            transport_coordinates.y += 5; // Move vertically
                        } else {
                            transport_coordinates.y = P1.y; // Return to starting position vertically
                        }
                        break;

                    default:
                        throw new IllegalStateException("Invalid direction: " + direction);
                }
                container_panel.repaint(); // push to redraw the panel
            }
        });
        timer.start();

        checkboxBanTraveling.addActionListener(e -> { // check if checkBox is selected object is running else object stop
            if (checkboxBanTraveling.isSelected()) {
                timer.start();
            } else {
                timer.stop();
            }
        });
    }
    // rescale logic
    public void reScale(double xScale, double yScale)
    {
        P1.x = (int) (P1.x * xScale);
        P1.y = (int) (P1.y * yScale);
        P2.x = (int) (P2.x * xScale);
        P2.y = (int) (P2.y * yScale);
        transport_coordinates.x = (int) (transport_coordinates.x * xScale);
        transport_coordinates.y = (int) (transport_coordinates.y * yScale);
    }

    // check status of checkbox
    public Boolean getCheckboxBanTraveling() {
        return checkboxBanTraveling.isSelected();
    }
}

// class describes travel ways
class TravelWayKey {
    String destination_region;
    String source_region;
    double travel_way_index;

    TravelWayKey(String source_region, String destination_region, double travel_way_index) {
        this.source_region = source_region;
        this.destination_region = destination_region;
        this.travel_way_index = travel_way_index;//Transport weight (transport index) - how many infected people moved with this transport
    }

    // check the class of its parameter
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        return ((travel_way_index == ((TravelWayKey)obj).travel_way_index))&& // compare index + either source or destination equals
                (source_region.equals(((TravelWayKey)obj).source_region))&&((destination_region.equals(((TravelWayKey)obj).destination_region)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(source_region+destination_region+travel_way_index);
    }

    @Override
    public String toString() {
        return "{SourceRegion = " + source_region + " DestinationRegion = }" + destination_region + " TravelWayIndex(weight) = " + travel_way_index;
    }
}
