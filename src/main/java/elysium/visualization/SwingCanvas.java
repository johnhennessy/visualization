package elysium.visualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/**
 * Created by jdh on 4/24/15.
 */
public class SwingCanvas extends JFrame implements Canvas {
    private int width;
    private int height;
    private Pallet pallet;
    private BufferedImage bufferedImage;
    private Graphics graphics;
    private int[] rgbArray;

    public SwingCanvas(int width, int height, Pallet pallet, MouseListener mouseListener, KeyListener keyListener) {
        this.width = width;
        this.height = height;
        this.pallet = pallet;

        super.setUndecorated(true);
        super.setSize(width, height);
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = bufferedImage.getGraphics();
        rgbArray = new int[width * height];

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(bufferedImage, 0, 0, this);
            }
        };
        super.getContentPane().add(panel);

//        Thread t = new Thread(() -> {
////            Graphics graphics = bufferedImage.getGraphics();
//
//            for (;;) {
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    break;
//                }
//
//                repaint();
//            }
//        });
//        t.start();

        super.setVisible(true);

        addMouseListener(mouseListener);
        addKeyListener(keyListener);
    }

    public void drawPoint(int x, int y, int palletColor) {
        rgbArray[(y * width) + x] = pallet.getRGB(palletColor);

    }

    @Override
    public void update() {
        bufferedImage.setRGB(0, 0, width, height, rgbArray, 0, width);
        repaint();
    }

}
