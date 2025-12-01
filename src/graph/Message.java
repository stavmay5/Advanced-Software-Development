package graph;

import java.util.Date;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;
    //private final Double value;

    // Const str
    public Message(String str) {
        if (str == null) {
            throw new IllegalArgumentException("testString is null");
        }
        this.data = str.getBytes();
        this.asText = str;
        this.asDouble = parseDoubleSafely(str);
        this.date = new Date();
    }

    // Const bytes
    public Message(byte[] bytes) {
        this(new String(bytes));
    }

    // Const double
    public Message(double d) {
        this(Double.toString(d));
    }

    private static double parseDoubleSafely(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
