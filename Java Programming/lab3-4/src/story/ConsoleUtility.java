package story;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUtility {

    static StringBuilder leftBuilder;
    static StringBuilder midBuilder;
    static StringBuilder rightBuilder;

    static {
        leftBuilder = new StringBuilder();
        midBuilder = new StringBuilder();
        rightBuilder = new StringBuilder();
    }

    static public void Clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static public void appendToLeft(String str) {
        leftBuilder.append(str);
    }

    static public void appendToMid(String str) {
        midBuilder.append(str);
    }

    static public void appendToRight(String str) {
        rightBuilder.append(str);
    }

    static final private Pattern pattern = Pattern.compile("\033");
    static final int ANSI_LEN_IN_COLOR = 11;

    static public void PrintBuilders() {
        leftBuilder.append(" \n");
        midBuilder.append(" \n");
        rightBuilder.append(" \n");
        String[] leftLines = leftBuilder.toString().split("\n");
        String[] midLines = midBuilder.toString().split("\n");
        String[] rightLines = rightBuilder.toString().split("\n");

        leftBuilder.delete(0, leftBuilder.length());
        midBuilder.delete(0, midBuilder.length());
        rightBuilder.delete(0, rightBuilder.length());

        int l = 0, m = 0, r = 0;


        //Боже, прости меня за костыли!
        Matcher matcher;

        while (l < leftLines.length - 1 || m < midLines.length - 1 || r < rightLines.length - 1) {
            matcher = pattern.matcher(midLines[m]);
            int ansiLenMid = (int) matcher.results().count() / 2 * ANSI_LEN_IN_COLOR;

            matcher = pattern.matcher(leftLines[l]);
            int ansiLenLeft = (int) matcher.results().count() / 2 * ANSI_LEN_IN_COLOR;

            matcher = pattern.matcher(rightLines[r]);
            int ansiLenRight = (int) matcher.results().count() / 2 * ANSI_LEN_IN_COLOR;


            System.out.printf("%" + (55 + ansiLenLeft) + "s | %-" + (55 + ansiLenMid) + "s | %-" + (55 + ansiLenRight) + "s|\n", leftLines[l], midLines[m], rightLines[r]);

            l = l < leftLines.length - 1 ? l + 1 : l;
            m = m < midLines.length - 1 ? m + 1 : m;
            r = r < rightLines.length - 1 ? r + 1 : r;
        }

    }

}
