package elysium.mandelbrot;

import elysium.visualization.BlackAndWhitePallet;
import elysium.visualization.Canvas;
import elysium.visualization.Pallet;
import elysium.visualization.SwingCanvas;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.IntFunction;

/**
 * Created by jdh on 4/25/15.
 */
public class Mandelbrot {
    private final MathContext mathContext;

    private BigDecimal minReal;
    private BigDecimal maxReal;
    private BigDecimal minImaginary;
    private BigDecimal maxImaginary;

    private int maxIterations;

    public static void main(String[] args) {
        Pallet pallet = new BlackAndWhitePallet();

        int width = 500;
        int height = (int) (2.0 * width / 3.5);

        int precision = 128;
        Mandelbrot mandelbrot = new Mandelbrot(precision);

//        Canvas canvas = new SwingCanvas(width, height, pallet);
//        mandelbrot.draw(canvas);

        Canvas canvas = new Canvas() {
            @Override
            public void drawPoint(int x, int y, int palletColor) {

            }

            @Override
            public void update() {

            }

            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }
        };

        int burnin = 10;
        for (int i = 0;i < burnin;++i) {
            mandelbrot.draw(canvas);
        }

        int iterations = 10;
        long start = System.currentTimeMillis();
        for (int i = 0;i < iterations;++i) {
            mandelbrot.draw(canvas);
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("Executed [" + iterations + "] in [" + time + "] ms");
    }

    public Mandelbrot(int precision) {
        switch (precision) {
            case 32:
                mathContext = MathContext.DECIMAL32;
                break;
            case 64:
                mathContext = MathContext.DECIMAL64;
                break;
            case 128:
                mathContext = MathContext.DECIMAL128;
                break;
            default:
                mathContext = new MathContext(precision);
        }

        minReal = new BigDecimal(-2.5, mathContext);
        maxReal = new BigDecimal(1.0, mathContext);
        minImaginary = new BigDecimal(-1.0, mathContext);
        maxImaginary = new BigDecimal(1.0, mathContext);

        maxIterations = 30;
    }

    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        IntFunction<BigDecimal> xFunction = createTransform(mathContext, minReal, maxReal, width);
        IntFunction<BigDecimal> yFunction = createTransform(mathContext, minImaginary, maxImaginary, height);

        int y;
        int x;
        int n;
        BigDecimal cImaginary;
        BigDecimal cReal;
        BigDecimal zReal;
        BigDecimal zImaginary;
        boolean inside;
        BigDecimal zReal2;
        BigDecimal zImaginary2;

        BigDecimal two = new BigDecimal(2.0, mathContext);

        for (y = 0;y < height;++y) {
            cImaginary = yFunction.apply(y);
            for (x = 0;x < width;++x) {
                cReal = xFunction.apply(x);
                zReal = cReal;
                zImaginary = cImaginary;
                inside = true;

                for (n = 0;n < maxIterations;++n) {
                    zReal2 = zReal.pow(2, mathContext);
                    zImaginary2 = zImaginary.pow(2, mathContext);
                    if (zReal2.add(zImaginary2, mathContext).intValue() > 4) {
                        inside = false;
                        break;
                    }
                    zImaginary = two.multiply(zReal, mathContext).multiply(zImaginary, mathContext).add(cImaginary, mathContext);
                    zReal = zReal2.subtract(zImaginary2, mathContext).add(cReal, mathContext);
                }
                if (inside || n == maxIterations) {
                    canvas.drawPoint(x, y, 0);
                } else {
                    canvas.drawPoint(x, y, 1);
                }
            }
        }
        canvas.update();
    }

    private static IntFunction<BigDecimal> createTransform(MathContext mathContext, BigDecimal min, BigDecimal max, int size) {
        BigDecimal factor = max.subtract(min, mathContext).divide(new BigDecimal(size, mathContext), mathContext);
        return (x) -> new BigDecimal(x, mathContext).multiply(factor, mathContext).add(min, mathContext);
    }
}
