package ch04;

import java.util.Arrays;

public class UserInterfaceConfig {

    private String titleText;
    private String[] titleFonts;
    private int[] titleFontSizes;

    public String getTitleText() {
        return titleText;
    }

    public String[] getTitleFonts() {
        return titleFonts;
    }

    public int[] getTitleFontSizes() {
        return titleFontSizes;
    }

    @Override
    public String toString() {
        return "UserInterfaceConfig{" +
                "titleText='" + titleText + '\'' +
                ", titleFonts=" + Arrays.toString(titleFonts) +
                ", titleFontSizes=" + Arrays.toString(titleFontSizes) +
                '}';
    }
}
