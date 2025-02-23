import java.util.Objects;
// DESCRIBE OBJECT REGION
public class Region_mod {
    private double infectionSpreadIndex;
    private String regionName;
    private int population = 0;
    private int infectedPopulation = 0;
    private int curedPopulation = 0;
    private int diedPopulation = 0;
    private boolean isVaccine = false;
    private boolean isMaskObligation = false;
    private boolean isRemoteWork = false;
    private boolean isQuarantine = false;
    private int hospitalCapacity = 0;


    public Region_mod(String regionName){
        this.regionName = regionName;
    }
    public Region_mod(String regionName, int population, int curedPopulation, int infectedPopulation,
                        int diedPopulation, boolean isVaccine, boolean isMaskObligation, boolean isRemoteWork,
                        boolean isQuarantine, int hospitalCapacity, double infectionSpreadIndex) { // constructor

        this.regionName = regionName;
        this.population = population;
        this.curedPopulation = curedPopulation;
        this.infectedPopulation = infectedPopulation;
        this.diedPopulation = diedPopulation;
        this.isVaccine = isVaccine;
        this.isMaskObligation = isMaskObligation;
        this.isRemoteWork = isRemoteWork;
        this.isQuarantine = isQuarantine;
        this.hospitalCapacity = hospitalCapacity;
        this.infectionSpreadIndex = infectionSpreadIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        return regionName.equals((String)obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionName);
    }

    @Override
    public String toString() {
        return "RegionMod {countryName = " + regionName + "}";
    }

    public String getRegionName() {
        return regionName;
    }

    public void setCountryName(String countryName) {
        this.regionName = countryName;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getCuredPopulation() {
        return curedPopulation;
    }

    public void setCuredPopulation(int curedPopulation) {
        this.curedPopulation = curedPopulation;
    }

    public void setDiedPopulation(int diedPopulation) {this.diedPopulation = diedPopulation;}
    public int getDiedPopulation () { return diedPopulation;}

    public int getInfectedPopulation() {
        return infectedPopulation;
    }

    public void setInfectedPopulation(int infectedPopulation) {
        this.infectedPopulation = infectedPopulation;
    }
    public void setInfectionSpreadIndex (double infIndex) {
        infectionSpreadIndex = infIndex;
    }
    public double getInfectionSpreadIndex () {
        return infectionSpreadIndex;
    }
}
