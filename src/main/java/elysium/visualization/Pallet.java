package elysium.visualization;

import java.awt.*;

/**
 * Created by jdh on 4/24/15.
 */
public interface Pallet {
    int getNumberColors();

    int getRGB(int palletColor);

    Color getColor(int palletColor);
}
