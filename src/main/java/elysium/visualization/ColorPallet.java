package elysium.visualization;

import java.awt.*;

/**
 * Created by jdh on 4/24/15.
 */
public class ColorPallet implements Pallet {
    private final int[] rgb;
    private final Color[] colors;

    public ColorPallet(int numberColors) {
        colors = new Color[numberColors];
        int multiplier = 255 / numberColors;
        for (int i = 0;i < numberColors;++i) {
            colors[i] = new Color(i * multiplier, i * multiplier, i * multiplier);
        }

        rgb = new int[colors.length];
        for (int i = 0;i < colors.length;++i) {
            rgb[i] = colors[i].getRGB();
        }


//        numberColors = 5;
//        rgb = new int[numberColors];
//        colors = new Color[numberColors];
//
//        rgb[0] = 255 + (255 << 8) + (255 << 16);
//        colors[0] = new Color(rgb[0]);
//
//        int increment = 255 / numberColors;
//
//        int value;
//        for (int i = 1;i < numberColors;++i) {
//            value = i * increment;
//            rgb[i] = value + (value << 8) + (value << 16);
//            colors[i] = new Color(rgb[i]);
//        }
    }

    @Override
    public int getNumberColors() {
        return rgb.length;
    }

    @Override
    public int getRGB(int palletColor) {
        return rgb[palletColor];
    }

    @Override
    public Color getColor(int palletColor) {
        return colors[palletColor];
    }


}
