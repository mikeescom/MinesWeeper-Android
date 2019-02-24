package com.mikeescom.minesweeper.data;

import android.view.View;

public class FieldObject {
    private View squareView;
    private int squareImageToShow = 0;
    private boolean covered = true;
    private boolean flagged = false;

    public FieldObject (View squareView, int squareImageToShow) {
        this.squareView = squareView;
        this.squareImageToShow = squareImageToShow;
    }

    public View getSquareView() {
        return squareView;
    }

    public void setSquareView(View squareView) {
        this.squareView = squareView;
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

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }
}
