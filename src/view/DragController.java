package view;

import java.awt.Image;

public class DragController {

    public boolean dragging = false;
    public int shipType = -1;
    public int shipLength = 0;
    public boolean horizontal = true;

    public Image imageH;
    public Image imageV;

    //grid snapping location
    public int ghostRow = -1;
    public int ghostCol = -1;

    public void startDrag(int type, int length, boolean horizontal,
                          Image imageH, Image imageV) {
        this.dragging = true;
        this.shipType = type;
        this.shipLength = length;
        this.horizontal = horizontal;
        this.imageH = imageH;
        this.imageV = imageV;
    }

    public void stopDrag() {
        dragging = false;
        shipType = -1;
        shipLength = 0;
        imageH = null;
        imageV = null;
        ghostRow = -1;
        ghostCol = -1;
    }
}
