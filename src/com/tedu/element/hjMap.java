package com.tedu.element;

import javax.swing.*;
import java.awt.*;

public class hjMap extends ElementObj{

    @Override
    public void showElement(Graphics g) {
        g.drawImage(this.getIcon().getImage(),
                this.getX(), this.getY(),
                this.getW(), this.getH(), null);
    }
    @Override
    public ElementObj createElement(String str){
        this.setX(0);
        this.setY(0);
        this.setW(1000);
        this.setH(300);
        this.setIcon(new ImageIcon(str));
        return this;
    }
}
