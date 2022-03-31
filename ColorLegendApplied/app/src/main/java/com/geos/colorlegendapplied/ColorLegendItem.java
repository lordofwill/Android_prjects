package com.geos.colorlegendapplied;

public class ColorLegendItem {
    private String _title;
    private int _color;
    private boolean _toggleSwitch;

    public ColorLegendItem( int color, String title, boolean toggleSwitch) {
        this. _title = title;
        this. _color = color;
        this. _toggleSwitch = toggleSwitch;
    }

    public int getColor() {
        return _color;
    }

    public void setColor(int color) {
        this._color = color;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public boolean getToggleSwitch() {
        return _toggleSwitch;
    }
    public void  setToggleSwitch(boolean toggleSwitch) {
        this._toggleSwitch=toggleSwitch;
    }
}
