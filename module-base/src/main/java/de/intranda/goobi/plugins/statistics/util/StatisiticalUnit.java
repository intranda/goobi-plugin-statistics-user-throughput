package de.intranda.goobi.plugins.statistics.util;

public enum StatisiticalUnit {

    volumes("volumes"), pages("pages");
    
    private String label;
    
    private StatisiticalUnit (String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
