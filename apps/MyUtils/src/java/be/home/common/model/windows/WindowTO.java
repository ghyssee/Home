package be.home.common.model.windows;

import com.sun.jna.platform.win32.WinDef;

import java.awt.*;

public class WindowTO {
    WinDef.HWND handle;
    String title;

    Rectangle bounds;

    public WinDef.HWND getHandle() {
        return handle;
    }

    public void setHandle(WinDef.HWND handle) {
        this.handle = handle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
