package de.intranda.goobi.plugins.statistics.util;

public enum Interval {


//%a  Abbreviated weekday name (Sun..Sat)
//%b  Abbreviated month name (Jan..Dec)
//%c  Month, numeric (0..12)
//%D  Day of the month with English suffix (0th, 1st, 2nd, 3rd, …)
//%d  Day of the month, numeric (00..31)
//%e  Day of the month, numeric (0..31)
//%M  Month name (January..December)
//%m  Month, numeric (00..12)
//%U  Week (00..53), where Sunday is the first day of the week; WEEK() mode 0
//%u  Week (00..53), where Monday is the first day of the week; WEEK() mode 1
//%V  Week (01..53), where Sunday is the first day of the week; WEEK() mode 2; used with %X
//%v  Week (01..53), where Monday is the first day of the week; WEEK() mode 3; used with %x
//%W  Weekday name (Sunday..Saturday)
//%w  Day of the week (0=Sunday..6=Saturday)
//%Y  Year, numeric, four digits
    
    
    YEAR("years", "%Y"),
    MONTH("months", "%Y-%m"),
    WEEK("weeks", "%Y/%v"),
    DAY("days", "%Y-%m-%d");

    private Interval(String label, String statement) {
        this.label = label;
        this.sqlStatement = statement;

    }

    private String label;
    private String sqlStatement;

    public String getLabel() {
        return label;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }
}
