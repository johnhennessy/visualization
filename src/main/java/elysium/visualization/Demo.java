package elysium.visualization;

import java.util.function.IntToDoubleFunction;

/**
 * Created by jdh on 4/24/15.
 */
public class Demo {
    private static IntToDoubleFunction transform(double min, double max, int screen) {
        double factor = (max - min) / screen;
        return (x) -> min + (double) x * factor;
    }

    public static void main(String[] args) throws Exception {
        int maxIterations = 50;

        int width = 1000;
        int height = (int) (2.0 * width / 3.5);
        Pallet pallet = new ColorPallet(maxIterations);
//        Pallet pallet = new BlackAndWhitePallet();
        Canvas canvas = new SwingCanvas(width, height, pallet);

        IntToDoubleFunction xFunction = transform(-2.5, 1.0, width);
        IntToDoubleFunction yFunction = transform(-1.0, 1.0, height);

        int y;
        int x;
        int n;
        double cImaginary;
        double cReal;
        double zReal;
        double zImaginary;
        boolean inside;
        double zReal2;
        double zImaginary2;

        int iterationsPerColor = (int) Math.ceil((double) (pallet.getNumberColors() - 1) / (double) maxIterations);
        System.out.println("Iterations per color [" + iterationsPerColor + "]");
        int color;
        double colorScale = ((double) pallet.getNumberColors() - 1.0) / (double) maxIterations;

        for (y = 0;y < height;++y) {
            cImaginary = yFunction.applyAsDouble(y);
            for (x = 0;x < width;++x) {
                cReal = xFunction.applyAsDouble(x);
                zReal = cReal;
                zImaginary = cImaginary;
                inside = true;

                for (n = 0;n < maxIterations;++n) {
                    zReal2 = zReal * zReal;
                    zImaginary2 = zImaginary * zImaginary;
                    if (zReal2 + zImaginary2 > 4) {
                        inside = false;
                        break;
                    }
                    zImaginary = 2.0 * zReal * zImaginary + cImaginary;
                    zReal = zReal2 - zImaginary2 + cReal;
                }
                if (inside || n == maxIterations) {
//                    System.out.println("(" + x + "," + y + ") inside");
                    canvas.drawPoint(x, y, 0);
                } else {
                    color = (int) Math.abs((((double) n + 1.0) * colorScale));

//                    System.out.println("(" + x + "," + y + ") outside [" + n + "] [" + color + "]");
                    canvas.drawPoint(x, y, color);
                }
            }
        }
        canvas.update();
    }
}