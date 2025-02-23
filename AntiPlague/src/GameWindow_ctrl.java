import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class GameWindow_ctrl extends JFrame {
    private final Image worldMapImage; // World map image
    private Map<String, Region_vw> regions; // List of interactive regions
    private Map <TravelWayKey, TravelingWay_vw> travelways; // List of all travelways between regions
    private Region_vw selectedRegion = null; // Currently selected region
    private RegionGraph_mod regionGraph;
    private int start_width = 1000;
    private int start_height = 600;
    private JLabel timerLabel; // show time
    private JLabel budgetLabel;
    private JLabel finalScoreLabel;

    private JLabel worldInfectedLabel;
    private JLabel worldDiedLabel;
    private JLabel worldCuredLabel;

    private JLabel gameOverLabel;

    private Timer gameTimer;
    private int elapsedDays = 0; // Tracks elapsed time
    private long sumOfInfectedInTheWorld = 0;
    private long sumOfCuredInTheWorld = 0;
    private long sumOfDiedInTheWorld = 0;
    private long sumOfPopulationInTheWorld = 0;

    private int InfectedPupulationInRegion = 0;
    private int curedPopulationInRegion = 0;
    private int diedPopulationInRegion = 0;

    // Earning Money
    private int Budget = 50; // Starting points in the budget
    private long FinalScore; // FinalScore calculation
    public int levelNumber; // level number
    private int timerLevel = 1000;

    public GameWindow_ctrl(int levelNumber) {
        setTitle("Interactive World Map - 10 Regions");
        setSize(start_width, start_height);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        this.levelNumber = levelNumber;

        class MapPanel extends JPanel {
            public MapPanel() {
                try {
                    // Create a label to display the timer
                    timerLabel = new JLabel("Days passed: 0");
                    timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    add(timerLabel);

                    // create a label display user's budget
                    budgetLabel = new JLabel("Budget: 0");
                    budgetLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    add(budgetLabel);

                    // create a label to display final score
                    finalScoreLabel = new JLabel("Final score: 0");
                    finalScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    add(finalScoreLabel);

                    // create a label display information about whole infection
                    worldInfectedLabel = new JLabel("Infected: ");
                    worldInfectedLabel.setToolTipText("Infected amount");
                    worldInfectedLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    worldInfectedLabel.setForeground(Color.RED);
                    add(worldInfectedLabel);

                    // create a label display information about whole cured
                    worldCuredLabel = new JLabel("Cured: ");
                    worldCuredLabel.setToolTipText("Cured amount");
                    worldCuredLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    worldCuredLabel.setForeground(Color.GREEN);
                    add(worldCuredLabel);

                    // create a label display information about whole died
                    worldDiedLabel = new JLabel("Died: ");
                    worldDiedLabel.setToolTipText("Died amount");
                    worldDiedLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    worldDiedLabel.setForeground(Color.BLUE);
                    add(worldDiedLabel);

                    // Game Over label (initially hidden)
                    gameOverLabel = new JLabel("Game Over");
                    gameOverLabel.setForeground(Color.RED);
                    gameOverLabel.setFont(new Font("Serif", Font.BOLD, 24));
                    gameOverLabel.setVisible(false); // Hide initially
                    add(gameOverLabel);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Add mouse listener to handle clicks
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) { // select region
                        if (selectedRegion == null) { // check if region has been chosen prohibit to chose new region
                            Point clickedPoint = e.getPoint(); // get point where user clicked on the screen
                            // System.out.println(clickedPoint.x + " " + clickedPoint.y);// debug
                            selectedRegion = null;
                            // Check if the click is within any region
                            for (Region_vw region : regions.values()) {
                                if (region.contains(clickedPoint)) {
                                    selectedRegion = region;//assign selected region
                                    Region_mod infectedRegion = regionGraph.getRegionFromMap(selectedRegion.getRegionName()); // start infect chosen region
                                    if (infectedRegion != null)
                                        infectedRegion.setInfectedPopulation(10);//setup initial infection - first 10 people
                                    gameTimer.start();//start the infection spread by timer
                                    break;
                                }
                            }
                        } else {
                            // System.out.println("region is chosen"); // debug
                        }
                    }
                });
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw the world map
                g.drawImage(worldMapImage, 0, 0, getWidth(), getHeight(), this);

                timerLabel.setBounds((this.getWidth()-150), 10, 150, 20);
                worldInfectedLabel.setBounds(this.getWidth() - 150, 40, 150, 20);
                worldCuredLabel.setBounds(this.getWidth() - 150, 60, 150, 20);
                worldDiedLabel.setBounds(this.getWidth() - 150, 80, 150, 20);

                budgetLabel.setBounds(5, 10, 150, 20);

                finalScoreLabel.setBounds(5, 30, 150, 20);

                Graphics2D g2d = (Graphics2D) g;
                Stroke stroke = g2d.getStroke(); // defines how a shape's outline is drawn
                Color clr = g2d.getColor();
                // Draw the interactive regions (semi-transparent)
                for (Region_vw region : regions.values()) {
                    region.paintRectangleRegion(g, region == selectedRegion);
                }
                //return style
                g2d.setStroke(stroke);
                g2d.setColor(clr);

                //Draw the collection of Travelways (airs/cars/boats)
                for (TravelingWay_vw travelway : travelways.values()) {
                    travelway.paintDirection(g);
                }
                //return style
                g2d.setStroke(stroke);
                g2d.setColor(clr);
            }
        }
        // Add custom JPanel
        MapPanel mapPanel = new MapPanel();
        add(mapPanel);

        // Load the world map image
        worldMapImage = new ImageIcon("Img/world_map.jpg").getImage();
        //Add RegionGraph
        regionGraph = new RegionGraph_mod(); // init RegionGraph_mod
        //Fill regions and travelways for view and model (graph)
        regions = createRegions(mapPanel, regionGraph);
        travelways = createTravelWays(mapPanel, regionGraph);

        // Create a timer to update the elapsed time every second
        gameTimer();
        // Add resize listener to adjust objects positions and sizes on the map

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { // rescale components
                int newWidth = getWidth();
                int newHeight = getHeight();
                double xScale = (double) newWidth / start_width;
                double yScale = (double) newHeight / start_height;
                
                //rescale all components
                reScale(xScale, yScale);
                
                mapPanel.repaint();
                start_width = newWidth;
                start_height = newHeight;
            }
        });

        addCommonListenerToCheckBoxes(mapPanel);//Add one common listener for all CBs
        addInterruptKeyBinding(mapPanel); // add listener for interrupt game
        setBudgetLabel();//initial setup of Budget label
    }
    public void reScale(double xScale, double yScale) {
        for (Region_vw region : regions.values()) {
            region.reScale(xScale, yScale);
        }
        for (TravelingWay_vw travelway : travelways.values()) {
            travelway.reScale(xScale, yScale);
        }
    }

    public Map<String, Region_vw> createRegions(JPanel mapPanel, RegionGraph_mod regionGraph) { // fill in graphical part
        Map<String, Region_vw> regions = new  HashMap<>();

        // Define the regions as rectangles
        regions.put("North America", new Region_vw(mapPanel, "North America", new Point(140, 90),  135, 180));
        regionGraph.addRegion(new Region_mod("North America"));
        regionGraph.getRegionFromMap("North America").setPopulation(600*1000000);
        regionGraph.getRegionFromMap("North America").setInfectionSpreadIndex(1.1);

        regions.put("South America", new Region_vw(mapPanel, "South America", new Point(205, 305), 150, 150));
        regionGraph.addRegion(new Region_mod("South America"));
        regionGraph.getRegionFromMap("South America").setPopulation(385*1000000);
        regionGraph.getRegionFromMap("South America").setInfectionSpreadIndex(1.2);

        regions.put("Europe", new Region_vw(mapPanel, "Europe", new Point(450, 140), 130, 120));
        regionGraph.addRegion(new Region_mod("Europe"));
        regionGraph.getRegionFromMap("Europe").setPopulation(744*1000000);
        regionGraph.getRegionFromMap("Europe").setInfectionSpreadIndex(1.1);

        regions.put("Africa", new Region_vw(mapPanel, "Africa", new Point(440, 270), 150, 150));
        regionGraph.addRegion(new Region_mod("Africa"));
        regionGraph.getRegionFromMap("Africa").setPopulation(1200*1000000);
        regionGraph.getRegionFromMap("Africa").setInfectionSpreadIndex(1.3);

        regions.put("Middle East", new Region_vw(mapPanel, "Middle East", new Point(595, 205), 150, 150));
        regionGraph.addRegion(new Region_mod("Middle East"));
        regionGraph.getRegionFromMap("Middle East").setPopulation(371*1000000);
        regionGraph.getRegionFromMap("Middle East").setInfectionSpreadIndex(1.2);

        regions.put("Russia & Central Asia", new Region_vw(mapPanel, "Russia & Central Asia", new Point(590, 90), 310, 110));
        regionGraph.addRegion(new Region_mod("Russia & Central Asia"));
        regionGraph.getRegionFromMap("Russia & Central Asia").setPopulation(300*1000000);
        regionGraph.getRegionFromMap("Russia & Central Asia").setInfectionSpreadIndex(1.1);

        regions.put("South Asia", new Region_vw(mapPanel, "South Asia", new Point(750, 205), 140, 130));
        regionGraph.addRegion(new Region_mod("South Asia"));
        regionGraph.getRegionFromMap("South Asia").setPopulation(2000*1000000);
        regionGraph.getRegionFromMap("South Asia").setInfectionSpreadIndex(1.3);

        regions.put("Grenlandia", new Region_vw(mapPanel, "Grenlandia", new Point(340, 55), 110, 135));
        regionGraph.addRegion(new Region_mod("Grenlandia"));
        regionGraph.getRegionFromMap("Grenlandia").setPopulation(56000);
        regionGraph.getRegionFromMap("Grenlandia").setInfectionSpreadIndex(1.0);

        regions.put("Southeast Asia & Oceania", new Region_vw(mapPanel, "Southeast Asia & Oceania", new Point(750, 340), 220, 180));
        regionGraph.addRegion(new Region_mod("Southeast Asia & Oceania"));
        regionGraph.getRegionFromMap("Southeast Asia & Oceania").setPopulation(700*1000000);
        regionGraph.getRegionFromMap("Southeast Asia & Oceania").setInfectionSpreadIndex(1.1);

        regions.put("Antarctica", new Region_vw(mapPanel, "Antarctica", new Point(375, 505), 285, 25));
        regionGraph.addRegion(new Region_mod("Antarctica"));
        regionGraph.getRegionFromMap("Antarctica").setPopulation(10000);
        regionGraph.getRegionFromMap("Antarctica").setInfectionSpreadIndex(1.0);

        return regions;
    }

    // create connections between countries
    public Map <TravelWayKey, TravelingWay_vw> createTravelWays(JPanel mapPanel, RegionGraph_mod regionGraph) {

        Map <TravelWayKey, TravelingWay_vw> travelConnections = new HashMap<>();
        // graphical part and logic part
        travelConnections.put(new TravelWayKey("Europe", "North America", 4), new TravelingWay_vw(mapPanel, new Point(270, 230), new Point(450, 230), Direction.Horizontal, Transport.PLANE));
        regionGraph.addBound(regionGraph.getRegionFromMap("North America"), regionGraph.getRegionFromMap("Europe"), 4);
        travelConnections.put(new TravelWayKey("Europe", "North America", 3), new TravelingWay_vw(mapPanel, new Point(270, 220), new Point(450, 220), Direction.Horizontal, Transport.SHIP));
        regionGraph.addBound(regionGraph.getRegionFromMap("North America"), regionGraph.getRegionFromMap("Europe"), 3);

        travelConnections.put(new TravelWayKey("North America", "South America",2), new TravelingWay_vw(mapPanel, new Point(240, 260), new Point(240, 380), Direction.Vertical, Transport.CAR));
        regionGraph.addBound(regionGraph.getRegionFromMap("North America"), regionGraph.getRegionFromMap("South America"), 2);
        travelConnections.put(new TravelWayKey("North America", "South America",4), new TravelingWay_vw(mapPanel, new Point(230, 260), new Point(230, 380), Direction.Vertical, Transport.PLANE));
        regionGraph.addBound(regionGraph.getRegionFromMap("North America"), regionGraph.getRegionFromMap("South America"), 4);
        travelConnections.put(new TravelWayKey("North America", "South America",3), new TravelingWay_vw(mapPanel, new Point(220, 260), new Point(220, 380), Direction.Vertical, Transport.SHIP));
        regionGraph.addBound(regionGraph.getRegionFromMap("North America"), regionGraph.getRegionFromMap("South America"), 3);

        travelConnections.put(new TravelWayKey("Europe", "Africa",3), new TravelingWay_vw(mapPanel, new Point(470, 230), new Point(470, 290), Direction.Vertical, Transport.SHIP));
        regionGraph.addBound(regionGraph.getRegionFromMap("Europe"), regionGraph.getRegionFromMap("Africa"), 3);
        travelConnections.put(new TravelWayKey("Europe", "Africa",4), new TravelingWay_vw(mapPanel, new Point(460, 230), new Point(460, 290), Direction.Vertical, Transport.PLANE));
        regionGraph.addBound(regionGraph.getRegionFromMap("Europe"), regionGraph.getRegionFromMap("Africa"), 4);

        travelConnections.put(new TravelWayKey("Europe", "Grenlandia", 2), new TravelingWay_vw(mapPanel, new Point(400, 150), new Point(490, 245), Direction.Diagonal, Transport.SHIP));
        regionGraph.addBound(regionGraph.getRegionFromMap("Europe"), regionGraph.getRegionFromMap("Grenlandia"), 2);

        travelConnections.put(new TravelWayKey("Europe", "Russia & Central Asia", 5), new TravelingWay_vw(mapPanel, new Point(555, 180), new Point(665, 180), Direction.Horizontal, Transport.PLANE));
        regionGraph.addBound(regionGraph.getRegionFromMap("Europe"), regionGraph.getRegionFromMap("Russia & Central Asia"), 5);
        travelConnections.put(new TravelWayKey("Europe", "Russia & Central Asia", 4), new TravelingWay_vw(mapPanel, new Point(555, 170), new Point(665, 170), Direction.Horizontal, Transport.CAR));
        regionGraph.addBound(regionGraph.getRegionFromMap("Europe"), regionGraph.getRegionFromMap("Russia & Central Asia"), 4);

        travelConnections.put(new TravelWayKey("Russia & Central Asia", "South Asia", 3), new TravelingWay_vw(mapPanel, new Point(870, 145), new Point(870, 240), Direction.Vertical, Transport.CAR));
        regionGraph.addBound(regionGraph.getRegionFromMap("Russia & Central Asia"), regionGraph.getRegionFromMap("South Asia"), 3);
        travelConnections.put(new TravelWayKey("Russia & Central Asia", "South Asia", 5), new TravelingWay_vw(mapPanel, new Point(860, 145), new Point(860, 240), Direction.Vertical, Transport.PLANE));
        regionGraph.addBound(regionGraph.getRegionFromMap("Russia & Central Asia"), regionGraph.getRegionFromMap("South Asia"), 5);
        travelConnections.put(new TravelWayKey("Russia & Central Asia", "South Asia", 4), new TravelingWay_vw(mapPanel, new Point(850, 145), new Point(850, 240), Direction.Vertical, Transport.SHIP));
        regionGraph.addBound(regionGraph.getRegionFromMap("Russia & Central Asia"), regionGraph.getRegionFromMap("South Asia"), 4);

        travelConnections.put(new TravelWayKey("Russia & Central Asia", "Middle East", 2), new TravelingWay_vw(mapPanel, new Point(700, 145), new Point(700, 230), Direction.Vertical, Transport.CAR));
        regionGraph.addBound(regionGraph.getRegionFromMap("Russia & Central Asia"), regionGraph.getRegionFromMap("Middle East"), 2);
        travelConnections.put(new TravelWayKey("Russia & Central Asia", "Middle East", 5), new TravelingWay_vw(mapPanel, new Point(690, 145), new Point(690, 230), Direction.Vertical, Transport.PLANE));
        regionGraph.addBound(regionGraph.getRegionFromMap("Russia & Central Asia"), regionGraph.getRegionFromMap("Middle East"), 5);

        travelConnections.put(new TravelWayKey("South Asia", "Middle East", 5), new TravelingWay_vw(mapPanel, new Point(700, 280), new Point(820, 280), Direction.Horizontal, Transport.PLANE));
        regionGraph.addBound(regionGraph.getRegionFromMap("South Asia"), regionGraph.getRegionFromMap("Middle East"), 5);
        travelConnections.put(new TravelWayKey("South Asia", "Middle East", 4), new TravelingWay_vw(mapPanel, new Point(700, 270), new Point(820, 270), Direction.Horizontal, Transport.CAR));
        regionGraph.addBound(regionGraph.getRegionFromMap("South Asia"), regionGraph.getRegionFromMap("Middle East"), 4);
        travelConnections.put(new TravelWayKey("South Asia", "Middle East", 3), new TravelingWay_vw(mapPanel, new Point(700, 260), new Point(820, 260), Direction.Horizontal, Transport.SHIP));
        regionGraph.addBound(regionGraph.getRegionFromMap("South Asia"), regionGraph.getRegionFromMap("Middle East"), 3);

        travelConnections.put(new TravelWayKey("South Asia", "Southeast Asia & Oceania", 4), new TravelingWay_vw(mapPanel, new Point(795, 310), new Point(795, 430), Direction.Vertical, Transport.SHIP));
        regionGraph.addBound(regionGraph.getRegionFromMap("South Asia"), regionGraph.getRegionFromMap("Southeast Asia & Oceania"), 4);
        travelConnections.put(new TravelWayKey("South Asia", "Southeast Asia & Oceania", 3), new TravelingWay_vw(mapPanel, new Point(805, 310), new Point(805, 430), Direction.Vertical, Transport.PLANE));
        regionGraph.addBound(regionGraph.getRegionFromMap("South Asia"), regionGraph.getRegionFromMap("Southeast Asia & Oceania"), 3);

        travelConnections.put(new TravelWayKey("South America", "Antarctica", 2), new TravelingWay_vw(mapPanel, new Point(320, 420), new Point(440, 515), Direction.Diagonal, Transport.SHIP));
        regionGraph.addBound(regionGraph.getRegionFromMap("South America"), regionGraph.getRegionFromMap("Antarctica"), 2);

        // create more connections
        return travelConnections;
    }

    public void gameTimer() {
        // timerLevel
        switch (levelNumber) { // switch change timer time depending of number of level
            case 1:
                timerLevel = 2000;
                break;
            case 2:
                timerLevel = 1000;
                break;
            case 3:
                timerLevel = 500;
                break;
            default:
                timerLevel = 2000;
        }
        // Create a timer to update the elapsed time every second as a separeted thread
        gameTimer = new Timer(timerLevel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedDays++;
                timerLabel.setText("Days passed: " + elapsedDays);
                if (selectedRegion != null) { ////////////////////////////?????????????????????????
                    InfectionSpread();//Update stats, update views,
                }
            }
        });
    }

    public void InfectionSpread() //process map of regions
    {
        double InfectionSpreadSpeed = 1.6;//standard rate (coefficient) of new infections spread 60%
        double baseCureRate = 0.2; //  default cured people rate per day
        double baseDeadRate = 0.0001; // default died rate

        int rateOfCuredPeopleToGetOnePoint = 900;//1001 people cured = 1 point to budget
        int rateOfDiedPeopleToReduceOnePoint = 50; // 50 people died = -1 point from budget
        //Regions loop
        //**** 1 Loop through all regions, check if they are infected and grow infection
        for (Map.Entry<Region_mod, List<Pair>> entry : regionGraph.getGraphMap().entrySet()) {
            InfectedPupulationInRegion = entry.getKey().getInfectedPopulation();
            curedPopulationInRegion = entry.getKey().getCuredPopulation();
            diedPopulationInRegion = entry.getKey().getDiedPopulation();
            double infectedIndexInTheRegion = entry.getKey().getInfectionSpreadIndex();// infection index depends of condition region (hygiene and climate)
            // update infection status and view
            // take money
            if (regions.get(entry.getKey().getRegionName()).get_CBVaccine()){
                InfectionSpreadSpeed = InfectionSpreadSpeed - 0.3; // vaccination reduced spread 0.3 times
            }
            if (regions.get(entry.getKey().getRegionName()).getQuarantine()) {
                InfectionSpreadSpeed = (int) (InfectionSpreadSpeed - 0.25);
            }
            if (regions.get(entry.getKey().getRegionName()).getRemote_work()) {
                InfectionSpreadSpeed = (InfectionSpreadSpeed - 0.2);
            }
            if (regions.get(entry.getKey().getRegionName()).get_Msk_obligation()) {
                InfectionSpreadSpeed = (InfectionSpreadSpeed - 0.1);
            }
            if (InfectedPupulationInRegion > 0) {//process only infected regions
                if (InfectedPupulationInRegion < entry.getKey().getPopulation()) {//don't allow to increase infected more than population
                    InfectedPupulationInRegion = (int) (InfectedPupulationInRegion * InfectionSpreadSpeed * infectedIndexInTheRegion); // added Infected
                    entry.getKey().setInfectedPopulation(InfectedPupulationInRegion);
                    regions.get(entry.getKey().getRegionName()).setInfectedAmount(InfectedPupulationInRegion);//update view
                }
                // return slider index
                double dblSliderHospitalsCapacity = (double) regions.get(entry.getKey().getRegionName()).getHospitalCapacity() / 10;

                if ((curedPopulationInRegion + diedPopulationInRegion) < entry.getKey().getPopulation()) {//don't allow to increase cured+died more than population
                    //calc cured
                    int curedPopulationInThisRound = (int) (InfectedPupulationInRegion * baseCureRate * dblSliderHospitalsCapacity);
                    curedPopulationInRegion = curedPopulationInRegion + curedPopulationInThisRound;
                    //Increase budget for cured persons * rate in this round
                    Budget += (curedPopulationInThisRound / rateOfCuredPeopleToGetOnePoint);

                    entry.getKey().setCuredPopulation(curedPopulationInRegion);
                    regions.get(entry.getKey().getRegionName()).setLabel_curedAmount(curedPopulationInRegion);
                    regions.get(entry.getKey().getRegionName()).setSliderHospital_label(regions.get(entry.getKey().getRegionName()).getHospitalCapacity()); // get number of hospitals for each region

                    //update  died stats
                    int diedPopulationInThisRound = (int) (InfectedPupulationInRegion * baseDeadRate / dblSliderHospitalsCapacity);
                    diedPopulationInRegion = diedPopulationInRegion + diedPopulationInThisRound;
                    Budget -= (diedPopulationInThisRound / rateOfDiedPeopleToReduceOnePoint);

                    // set diedPopulation for correct region
                    entry.getKey().setDiedPopulation(diedPopulationInRegion);
                    regions.get(entry.getKey().getRegionName()).setLabel_diedAmount(diedPopulationInRegion);

                    // count final score FinalScore=(+(CuredCount)−(DeathCount×20)
                    FinalScore += (int) (curedPopulationInRegion - (diedPopulationInRegion * 20));
                }
                InfectedPupulationInRegion = InfectedPupulationInRegion - curedPopulationInRegion - diedPopulationInRegion;

                //Track travelways and spread virus from dirty countries to clean countries
                int ThreadForMovingInfectionToOtherRegions = 10000;// count of sick people before start to spread virus in different regions
                if (InfectedPupulationInRegion >= ThreadForMovingInfectionToOtherRegions) {
                    List <Pair> bounds = regionGraph.getBound(entry.getKey());

                    for (Pair bound : bounds) {
                        //Identify the correct view(line + checkBox + animation) for the bound: try direct and revised order of regions names
                        TravelingWay_vw ViewValueInMap;
                        ViewValueInMap = travelways.get(new TravelWayKey(entry.getKey().getRegionName(), bound.destination_region.getRegionName(), bound.travel_way_index));
                        if (ViewValueInMap == null)
                            ViewValueInMap = travelways.get(new TravelWayKey(bound.destination_region.getRegionName(), entry.getKey().getRegionName(), bound.travel_way_index));
                        //check check box on the traveling way and Infected pupulation
                        if ((ViewValueInMap != null) && (ViewValueInMap.getCheckboxBanTraveling())
                                && (bound.destination_region.getInfectedPopulation() == 0)) {//checkbox is selected & destination
                            // region is not infected -> setup initial infection = travel_way_index
                            bound.destination_region.setInfectedPopulation((int) bound.travel_way_index);
                        }
                    }
                }
            }
        }
        // World stats update
        sumOfInfectedInTheWorld = 0;
        sumOfCuredInTheWorld = 0;
        sumOfDiedInTheWorld = 0;
        sumOfPopulationInTheWorld = 0;
        // *** overall regions in the world
        for (Map.Entry<Region_mod, List<Pair>> entry : regionGraph.getGraphMap().entrySet()) {
            sumOfInfectedInTheWorld += entry.getKey().getInfectedPopulation();
            sumOfCuredInTheWorld += entry.getKey().getCuredPopulation();
            sumOfDiedInTheWorld += entry.getKey().getDiedPopulation();
            sumOfPopulationInTheWorld += entry.getKey().getPopulation();
        }

        //update labels with common results
        worldInfectedLabel.setText("" + sumOfInfectedInTheWorld);
        worldCuredLabel.setText("" + sumOfCuredInTheWorld);
        worldDiedLabel.setText("" + sumOfDiedInTheWorld);
        setBudgetLabel();
        setFinalScoreLabel();

        //check if we still have the budget
        checkOverSpendMoney();

        //Check won or lost according to stats
        if (sumOfInfectedInTheWorld == sumOfPopulationInTheWorld) {
            SwingUtilities.invokeLater(() -> {
                JDialog jDialogLose = new JDialog();
                // Create a JTextPane
                JTextPane textPane = new JTextPane();
                jDialogLose.add(textPane);
                // Create styled text
                SimpleAttributeSet attributes
                        = new SimpleAttributeSet();
                StyleConstants.setBold(attributes, true);
                StyleConstants.setItalic(attributes, true);
                textPane.setCharacterAttributes(attributes,
                        false);

                // Set the text
                textPane.setText("Game OVER. All people in the world are infected");
                showGameOverDialog("GAME OVER. All people in the world are infected");
                interruptGame();
            });
        }
        if (sumOfInfectedInTheWorld == 0) { // We have tp identify the rules of absolute winner
            SwingUtilities.invokeLater(() -> {
                JDialog jDialogLose = new JDialog();
                // Create a JTextPane
                JTextPane textPane = new JTextPane();
                jDialogLose.add(textPane);
                // Step 2: Create styled text
                SimpleAttributeSet attributes
                        = new SimpleAttributeSet();
                StyleConstants.setBold(attributes, true);
                StyleConstants.setItalic(attributes, true);
                textPane.setCharacterAttributes(attributes,
                        false);

                // Step 3: Set the text
                textPane.setText("You WON, No more infection !!!");
                showGameOverDialog("GAME OVER. You WON, No more infection !!!");
                interruptGame();
            });
        }
    }
    public void setBudgetLabel() {
        budgetLabel.setText("Budget: " + Budget);
    }

    public void setFinalScoreLabel () {
        finalScoreLabel.setText("Final score: " + FinalScore);
    }

    private void addCommonListenerToCheckBoxes(JPanel panel) {
        // Create a common ItemListener
        ItemListener CBcommonListener = new ItemListener() { // check box listener
            @Override
            public void itemStateChanged(ItemEvent e) {

                JCheckBox source = (JCheckBox) e.getSource();
                //switch to identify type of CB and reduce budget accordingly
                switch (source.getName()) { // check if source name == name of checkbox
                    case "vaccine":
                        Budget -= 10;
                        FinalScore += 100;
                        break;
                    case "mask":
                        Budget -= 5;
                        FinalScore += 50;
                        break;
                    case "remote":
                        Budget -= 6;
                        FinalScore += 60;
                        break;
                    case "quarantine":
                        Budget -= 8;
                        FinalScore += 80;
                        break;
                    case "travelCB":
                        Budget -= 5;
                        FinalScore += 50;
                        break;
                    default:
                        System.out.println("Unknown checkbox: " + source.getName());
                }
                checkOverSpendMoney();
            }
        };

        ChangeListener SliderChangeListener = new ChangeListener() { // slider listener
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getName().equals("hospital")) {
                    Budget -= 8;
                }
                // System.out.println(source.getName() + "' value: " + source.getValue()); // debug
            }
        };

        // Loop through all components in the panel
        for (Component component : panel.getComponents()) {
            if (component instanceof JCheckBox) { // instanceof operator in Java is used to check if an object is an instance of a specific class
                // Add the common listener to each checkbox
                ((JCheckBox) component).addItemListener(CBcommonListener);
            }
            if (component instanceof JSlider) {
                // Add the listener to each slider
                ((JSlider) component).addChangeListener(SliderChangeListener);
            }
        }
    }

    private void addInterruptKeyBinding(JComponent component) {
        // Define the key combination (Ctrl + Shift + Q)
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);

        // Create an Action for the interrupt functionality
        Action interruptAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();//dispose the game window and return back to main menu
            }
        };

        // Get the InputMap and ActionMap for the component
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW); // locate a particular action when a key is pressed
        ActionMap actionMap = component.getActionMap(); // determine an Action to perform when a key is pressed.

        // Bind the key combination to the action
        inputMap.put(keyStroke, "interruptGame");
        actionMap.put("interruptGame", interruptAction);
    }

    //check Budget
    private void checkOverSpendMoney() {
        // Check if budget is 0 or below
        if (Budget <= 0) {
            setBudgetLabel();
            gameOverLabel.setVisible(true); // Show "Game Over"
            showGameOverDialog("GAME OVER. Your game budget is over, no ways to continue");
            interruptGame();

        }
    }

    private void showGameOverDialog(String sMessage) {
        gameTimer.stop();//stop the game timer (the main loop)
        // Display a JOptionPane with "GAME OVER" message
        JOptionPane.showMessageDialog(this, sMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    private void interruptGame() {
        // Add your game interruption logic here
        // System.out.println("Game interrupted by Ctrl + Shift + Q!"); // debug

        JDialog jDialog = new JDialog(this, "Game Interrupted", true); // true for modal
        jDialog.setSize(200, 100);
        jDialog.setLayout(new GridLayout(3, 2));
         // Add a label and text field for user input
        JLabel nameLabel = new JLabel("Enter your name:");
        JTextField nameField = new JTextField(10);
        jDialog.add(nameLabel);
        jDialog.add(nameField);

        // Create "Save" and "Cancel" buttons
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Add action listeners to the buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = "Top20/score.ser"; // store file name
                HashMap <String, Long> mapScore = new HashMap<>();
                Serialize_mod serialize_mod = new Serialize_mod();
                mapScore = serialize_mod.deserializeMapModel(fileName); // invoke deserialise method. read old value
                                                                        // before write new

                String name = nameField.getText();
                if (!name.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Name: " + name + "\n Score: " + FinalScore,
                            "Saved",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    // Parse the score value from scoreLabel and add it to the listModel
                    // Add the name and score to the map
                    mapScore.put(name, FinalScore);

                    // Serialize the map to a file
                    serialize_mod.serializeMapModel(mapScore, fileName);
                    jDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a name!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispose(); // Close the dialog
            }
        });

        // Add buttons to the dialog
        jDialog.add(saveButton);
        jDialog.add(cancelButton);

        // Set dialog properties
        jDialog.pack(); // Adjust size to fit components
        jDialog.setLocationRelativeTo(this); // Center relative to the main frame
        jDialog.setVisible(true); // Show the dialog

        //Game over
        System.exit(0);
    }
}