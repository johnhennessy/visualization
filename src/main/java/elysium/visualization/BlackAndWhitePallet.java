package elysium.visualization;

import java.awt.*;

/**
 * Created by jdh on 4/24/15.
 */
public class BlackAndWhitePallet implements Pallet {
    private static final int[] RGB;
    private static final Color[] COLORS;

    static {
        COLORS = new Color[] {Color.BLACK, Color.WHITE};
        RGB = new int[2];
        for (int i = 0;i < COLORS.length;++i) {
            RGB[i] = COLORS[i].getRGB();
        }
    }

    @Override
    public int getNumberColors() {
        return 2;
    }

    @Override
    public int getRGB(int palletColor) {
        return RGB[palletColor];
    }

    @Override
    public Color getColor(int palletColor) {
        return COLORS[palletColor];
    }


}
