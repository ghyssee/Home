package be.home.common.model.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.win32.StdCallLibrary;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class WindowAligner {

    // JNA Interface voor DWM (Desktop Window Manager) om de schaduwranden te negeren
    public interface Dwmapi extends StdCallLibrary {
        Dwmapi INSTANCE = Native.load("dwmapi", Dwmapi.class);
        // 9 staat voor DWMWA_EXTENDED_FRAME_BOUNDS
        int DwmGetWindowAttribute(HWND hwnd, int dwAttribute, RECT pvAttribute, int cbAttribute);
    }

    public static Rectangle getRobotCompatibleBounds(HWND hwnd) {
        RECT rect = new RECT();

        // 1. Haal de ECHTE visuele fysieke coördinaten op (zonder onzichtbare Windows 11 randen)
        int dwmResult = Dwmapi.INSTANCE.DwmGetWindowAttribute(hwnd, 9, rect, rect.size());

        int physicalX = rect.left;
        int physicalY = rect.top;
        int physicalWidth = rect.right - rect.left;
        int physicalHeight = rect.bottom - rect.top;

        // 2. Haal de Windows 11 schaalfactor op via Java GraphicsConfiguration
        GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        AffineTransform transform = config.getDefaultTransform();
        double scaleX = transform.getScaleX();
        double scaleY = transform.getScaleY();

        // 3. Reken de fysieke pixels om naar de virtuele coördinaten die Java Robot begrijpt
        int javaX = (int) Math.round(physicalX / scaleX);
        int javaY = (int) Math.round(physicalY / scaleY);
        int javaWidth = (int) Math.round(physicalWidth / scaleX);
        int javaHeight = (int) Math.round(physicalHeight / scaleY);

        return new Rectangle(javaX, javaY, javaWidth, javaHeight);
    }
}
