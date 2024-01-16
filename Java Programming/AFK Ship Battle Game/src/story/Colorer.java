package story;

import java.awt.*;

public class Colorer {


    static public String Colorize(String str, Colors color) {
        return color.getCode() + str + Colors.RESET.getCode();
    }


    static private final Colors[] COLORS_ORDER = new Colors[]{
            Colors.RED,
            Colors.RED_BOLD,
            Colors.RED_BRIGHT,
            Colors.RED_BOLD_BRIGHT,
            Colors.YELLOW,
            Colors.YELLOW_BOLD,
            Colors.YELLOW_BRIGHT,
            Colors.YELLOW_BOLD_BRIGHT,
            Colors.GREEN,
            Colors.GREEN_BOLD,
            Colors.GREEN_BRIGHT,
            Colors.GREEN_BOLD_BRIGHT
    };

    static public Colors getColorByPercent(double percent) {
        if (percent > 1 || percent < 0) {
            percent = 1;
        }
        return COLORS_ORDER[(int) (Math.round(percent * (COLORS_ORDER.length - 1)))];


    }


    public enum Colors {
        // Reset
        RESET("\033[0m"),  // Text Reset

        // Regular Colors
        BLACK("\033[0;30m"),   // BLACK
        RED("\033[0;31m"),     // RED
        GREEN("\033[0;32m"),   // GREEN
        YELLOW("\033[0;33m"),  // YELLOW
        BLUE("\033[0;34m"),    // BLUE
        PURPLE("\033[0;35m"),  // PURPLE
        CYAN("\033[0;36m"),    // CYAN
        WHITE("\033[0;37m"),   // WHITE
        // Bold

        BLACK_BOLD("\033[1;30m"),  // BLACK
        RED_BOLD("\033[1;31m"),    // RED
        GREEN_BOLD("\033[1;32m"),  // GREEN
        YELLOW_BOLD("\033[1;33m"), // YELLOW
        BLUE_BOLD("\033[1;34m"),   // BLUE
        PURPLE_BOLD("\033[1;35m"), // PURPLE
        CYAN_BOLD("\033[1;36m"),   // CYAN
        WHITE_BOLD("\033[1;37m"),  // WHITE

        // Underline
        BLACK_UNDERLINED("\033[4;30m"),  // BLACK
        RED_UNDERLINED("\033[4;31m"),    // RED
        GREEN_UNDERLINED("\033[4;32m"),  // GREEN
        YELLOW_UNDERLINED("\033[4;33m"), // YELLOW
        BLUE_UNDERLINED("\033[4;34m"),   // BLUE
        PURPLE_UNDERLINED("\033[4;35m"), // PURPLE
        CYAN_UNDERLINED("\033[4;36m"),   // CYAN
        WHITE_UNDERLINED("\033[4;37m"),  // WHITE

        // High Intensity
        BLACK_BRIGHT("\033[0;90m"),  // BLACK
        RED_BRIGHT("\033[0;91m"),    // RED
        GREEN_BRIGHT("\033[0;92m"),  // GREEN
        YELLOW_BRIGHT("\033[0;93m"), // YELLOW
        BLUE_BRIGHT("\033[0;94m"),   // BLUE
        PURPLE_BRIGHT("\033[0;95m"), // PURPLE
        CYAN_BRIGHT("\033[0;96m"),   // CYAN
        WHITE_BRIGHT("\033[0;97m"),  // WHITE

        // Bold High Intensity
        BLACK_BOLD_BRIGHT("\033[1;90m"), // BLACK
        RED_BOLD_BRIGHT("\033[1;91m"),   // RED
        GREEN_BOLD_BRIGHT("\033[1;92m"), // GREEN
        YELLOW_BOLD_BRIGHT("\033[1;93m"),// YELLOW
        BLUE_BOLD_BRIGHT("\033[1;94m"),  // BLUE
        PURPLE_BOLD_BRIGHT("\033[1;95m"),// PURPLE
        CYAN_BOLD_BRIGHT("\033[1;96m"),  // CYAN
        WHITE_BOLD_BRIGHT("\033[1;97m");// WHITE
        private String ansiCode;

        public String getCode() {
            return ansiCode;
        }

        Colors(String ansi) {
            ansiCode = ansi;
        }
    }


}
