import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Region_vw {
    private String regionName;
    Point startPoint;
    private int width;
    private int height;
    private Rectangle rectangle;

    private JLabel label_infected;
    private JLabel label_cured;
    private JLabel label_died;

    private JLabel sliderHospital_label;
    private JSlider slider_hospitalscapacity;

    private JCheckBox checkbox_vaccine;
    private JCheckBox checkBoxMask_obligation;
    private JCheckBox checkboxRemote_work;
    private JCheckBox checkBoxQuarantine;

    private int InCome_Rate;

    public Region_vw(JPanel container, String regionName, Point startPoint, int width, int height) {
        this.regionName = regionName;
        this.startPoint = startPoint;
        this.width = width;
        this.height = height;
        rectangle = new Rectangle(startPoint.x, startPoint.y, width, height);
        //Add labels to show infected, cured, died
        label_infected = new JLabel("Infected: 0");
        label_infected.setFont(new Font("Arial", Font.BOLD, 12));

        label_cured = new JLabel("cured: 0");
        label_cured.setFont(new Font("Arial", Font.BOLD, 12));

        label_died = new JLabel("died: 0");
        label_died.setFont(new Font("Arial", Font.BOLD, 12));

        sliderHospital_label = new JLabel("Hospitals: ");
        sliderHospital_label.setFont(new Font("Arial", Font.BOLD, 10));
        sliderHospital_label.setFont(new Font("Arial", Font.BOLD, 10));

        // add checkBoxes to show what country do contrary virus // INIT CHECKBOXES
        checkbox_vaccine = new JCheckBox();
        checkbox_vaccine.setName("vaccine");
        checkBoxMask_obligation = new JCheckBox();
        checkBoxMask_obligation.setName("mask");
        checkboxRemote_work = new JCheckBox();
        checkboxRemote_work.setName("remote");
        checkBoxQuarantine = new JCheckBox();
        // add slider to show count of hospitals
        slider_hospitalscapacity = new JSlider();
        slider_hospitalscapacity.setMinimum(1);
        slider_hospitalscapacity.setMaximum(9);
        slider_hospitalscapacity.setValue(1);
        slider_hospitalscapacity.setMajorTickSpacing(1);  // Major ticks every 9 units

        // attach components to pane
        container.add(label_infected); // 1
        container.add(label_cured); // 2
        container.add(label_died); // 3
        container.add(sliderHospital_label); // 4
        container.add(checkbox_vaccine); // 5
        container.add(checkBoxMask_obligation); // 6
        container.add(checkBoxQuarantine); // 7
        container.add(checkboxRemote_work); // 8
        container.add(slider_hospitalscapacity); // 9

        // set labels size
        label_infected.setSize(120, 8);
        label_cured.setSize(120, 8);
        label_died.setSize(120, 8);
        sliderHospital_label.setSize(120, 8);

        // SET TOLTIPS FOR CONTROLS
        label_cured.setToolTipText("amount cured people");
        label_cured.setToolTipText("amount died");
        checkbox_vaccine.setName("vaccine");
        checkbox_vaccine.setToolTipText("vaccine");
        checkBoxMask_obligation.setName("mask");
        checkBoxMask_obligation.setToolTipText("Mask obligation");
        checkboxRemote_work.setName("remote");
        checkboxRemote_work.setToolTipText("remote work");
        checkBoxQuarantine.setName("quarantine");
        checkBoxQuarantine.setToolTipText("Quarantine");

        slider_hospitalscapacity.setToolTipText("Hospitals capacity");
        slider_hospitalscapacity.setName("hospital");
        checkbox_vaccine.setSize(20, 15);
        checkBoxMask_obligation.setSize(20, 15);
        checkBoxQuarantine.setSize(20, 15);
        checkboxRemote_work.setSize(20, 15);
        slider_hospitalscapacity.setSize(50, 10);
        slider_hospitalscapacity.setValue(0);
    }

    public void paintRectangleRegion(Graphics g, boolean bSelected) {
        // set default color for unselected
        Color colorSemiTransparent = new Color(0, 0, 255, 50);
        g.setColor(colorSemiTransparent); // Semi-transparent blue
        g.fillRect(getBounds().x, getBounds().y,
                getBounds().width, getBounds().height);

        // Highlight the selected region
        if (bSelected) {
            g.setColor(new Color(255, 100, 100, 80)); // Semi-transparent red
            g.fillRect(getBounds().x, getBounds().y,
                    getBounds().width, getBounds().height);
        }

        // write region name
        g.setColor(Color.BLACK);
        g.drawString(getRegionName(),
                getBounds().x + 10,
                getBounds().y + 20);

        label_infected.setLocation(startPoint);
        label_infected.setForeground(Color.red);

        label_cured.setLocation(startPoint.x, startPoint.y + 15);
        label_cured.setForeground(Color.GREEN);

        label_died.setLocation(startPoint.x, startPoint.y + 30);
        label_died.setForeground(Color.BLUE);

        sliderHospital_label.setLocation(startPoint.x, startPoint.y + 60);

        checkbox_vaccine.setBackground(colorSemiTransparent);
        checkBoxMask_obligation.setBackground(colorSemiTransparent);
        checkBoxQuarantine.setBackground(colorSemiTransparent);
        checkboxRemote_work.setBackground(colorSemiTransparent);

        checkbox_vaccine.setLocation(startPoint.x + 110, startPoint.y);
        checkBoxMask_obligation.setLocation(startPoint.x + 110, startPoint.y + 12);
        checkboxRemote_work.setLocation(startPoint.x + 110, startPoint.y + 24);
        checkBoxQuarantine.setLocation(startPoint.x + 110, startPoint.y + 36);

        slider_hospitalscapacity.setLocation(startPoint.x + 110, startPoint.y + 60);
        slider_hospitalscapacity.setBackground(colorSemiTransparent);
    }

    public void reScale(double xScale, double yScale) {
        startPoint.x = (int) (startPoint.x * xScale);
        startPoint.y = (int) (startPoint.y * yScale);
        width = (int) (width * xScale);
        height = (int) (height * yScale);
        rectangle = new Rectangle(startPoint.x, startPoint.y, width, height);
    }

    public Rectangle getBounds() {
        return rectangle;
    }
    public String getRegionName() {
        return regionName;
    }
    public boolean contains(Point p) {
        return rectangle.contains(p); // check if point p is in rectangle
    }

    public void setInfectedAmount(int amount) {
        label_infected.setText("Infected: " + amount);
    };

    public void setLabel_curedAmount (int amount) {
        label_cured.setText("Cured: " + amount);
    }
    public void setLabel_diedAmount (int amount) {
        label_died.setText("Died: " + amount);
    }
    public void setSliderHospital_label (int amount) {
        sliderHospital_label.setText("Hospitals: " + amount);
    }
    //
    public boolean get_CBVaccine () {
        return checkbox_vaccine.isSelected();
    }
    public boolean get_Msk_obligation () {
        return checkBoxMask_obligation.isSelected();
    }
    public boolean getRemote_work () {
        return checkboxRemote_work.isSelected();
    }
    public boolean getQuarantine () {
        return checkBoxQuarantine.isSelected();
    }
    public int getHospitalCapacity() {
        return slider_hospitalscapacity.getValue();
    }
}