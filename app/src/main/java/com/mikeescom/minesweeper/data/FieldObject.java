package com.mikeescom.minesweeper.data;

import android.view.View;

public class FieldObject {
    private View squareView;
    private Coordinates coordinates;
    private int squareImageToShow = 0;
    private boolean covered = true;

    public FieldObject (View squareView, Coordinates coordinates, int squareImageToShow) {
        this.squareView = squareView;
        this.coordinates = coordinates;
        this.squareImageToShow = squareImageToShow;
    }

    public View getSquareView() {
        return squareView;
    }

    public void setSquareView(View squareView) {
        this.squareView = squareView;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public int getSquareImageToShow() {
        return squareImageToShow;
    }

    public void setSquareImageToShow(int squareImageToShow) {
        this.squareImageToShow = squareImageToShow;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }
}
