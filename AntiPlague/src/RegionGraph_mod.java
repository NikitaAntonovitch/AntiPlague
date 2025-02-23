import java.util.*;

public class RegionGraph_mod {
    private Map<Region_mod, List<Pair>> graphmap;
    public RegionGraph_mod() {
        // Create a graphmap with weights
        this.graphmap = new HashMap<>();
    }
    // Add regions (nodes)
    public void addRegion(Region_mod region_obj) {
        graphmap.put(region_obj, new ArrayList<>());
    }
    // Add bounds (nodes) with weights
    public void addBound(Region_mod region1, Region_mod region2, double travel_way_coefficient) { // add bounds into graphmap
        try {
            graphmap.get(region1).add(new Pair(region2, travel_way_coefficient));
            graphmap.get(region2).add(new Pair(region1,travel_way_coefficient));
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public List<Pair> getBound (Region_mod regionMod) {
        // Map <Region_mod, List<Pair>> regionBounds = new HashMap<>();
        if (graphmap.containsKey(regionMod)) {
            return graphmap.get(regionMod); // all bounds for regionMod country;
        }
        return null;
    }

    public Region_mod getRegionFromMap (String countryName) {
        for (Map.Entry<Region_mod, List<Pair>> entry : graphmap.entrySet()) {
            if (entry.getKey().equals(countryName)) {
                return entry.getKey(); // key of map from object
            }
        }
        return null;
    }

    public Map<Region_mod, List<Pair>> getGraphMap()
    {
        return graphmap;
    }
}

class Pair {
    Region_mod destination_region;
    double travel_way_index;

    Pair(Region_mod destination_region, double travel_way_index) {
        this.destination_region = destination_region;
        this.travel_way_index = travel_way_index;
    }
}
