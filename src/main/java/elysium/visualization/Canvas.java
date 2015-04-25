package elysium.visualization;

/**
 * Created by jdh on 4/24/15.
 */
public interface Canvas {
    void drawPoint(int x, int y, int palletColor);
    void update();
    int getWidth();
    int getHeight();
}
