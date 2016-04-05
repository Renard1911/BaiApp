package org.bienvenidoainternet.baiparser;

import android.content.res.TypedArray;
import android.graphics.Color;

/**
 * Created by Renard on 16-03-2016.
 */
public class ThemeManager {
    private MainActivity ac;
    private int currentThemeId;
    public ThemeManager(MainActivity ac){
        this.ac = ac;
        this.currentThemeId = ac.getCurrentThemeId();
    }

    public int getSageColor(){
        TypedArray a = ac.getTheme().obtainStyledAttributes(currentThemeId, new int[]{R.attr.sageColor});
        return a.getColor(0, Color.CYAN);
    }

    public int getMarginColor(){
        TypedArray a = ac.getTheme().obtainStyledAttributes(currentThemeId, new int[]{R.attr.marginColor});
        return a.getColor(0, Color.CYAN);
    }

    public void updateThemeId(int id){
        this.currentThemeId = id;
    }

    public int getNameColor() {
        TypedArray a = ac.getTheme().obtainStyledAttributes(currentThemeId, new int[]{R.attr.nameColor});
        return a.getColor(0, Color.CYAN);
    }

    public int getTripcodeColor() {
        TypedArray a = ac.getTheme().obtainStyledAttributes(currentThemeId, new int[]{R.attr.tripcodeColor});
        return a.getColor(0, Color.CYAN);
    }

    public int getPrimaryColor(){
        TypedArray a = ac.getTheme().obtainStyledAttributes(currentThemeId, new int[]{R.attr.colorPrimary});
        return a.getColor(0, Color.CYAN);
    }
    public int getPrimaryDarkColor(){
        TypedArray a = ac.getTheme().obtainStyledAttributes(currentThemeId, new int[]{R.attr.colorPrimaryDark});
        return a.getColor(0, Color.CYAN);
    }

    public boolean isDarkTheme(){
        TypedArray a = ac.getTheme().obtainStyledAttributes(currentThemeId, new int[]{R.attr.isDarkTheme});
        return a.getBoolean(0, false);
    }

}
