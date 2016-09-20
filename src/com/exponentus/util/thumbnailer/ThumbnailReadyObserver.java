package com.exponentus.util.thumbnailer;

import java.awt.*;
import java.awt.image.ImageObserver;

class ThumbnailReadyObserver implements ImageObserver {
    private Thread toNotify;

    public volatile boolean ready = false;

    public ThumbnailReadyObserver(Thread toNotify) {
        this.toNotify = toNotify;
        ready = false;
    }

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if ((infoflags & ImageObserver.ALLBITS) > 0) {
            ready = true;
            toNotify.notify();
            return true;
        }
        return false;
    }
}
