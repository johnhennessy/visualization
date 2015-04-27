package elysium.mandelbrot;

import elysium.visualization.BlackAndWhitePallet;
import elysium.visualization.Canvas;
import elysium.visualization.Pallet;
import elysium.visualization.SwingCanvas;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.IntFunction;

/**
 * Created by jdh on 4/25/15.
 */
public class Mandelbrot {
    private Canvas canvas;

    private final MathContext mathContext;

    private BigDecimal minReal;
    private BigDecimal maxReal;
    private BigDecimal minImaginary;
    private BigDecimal maxImaginary;

    private int maxIterations;

    private ExecutorService executorService;
    private int processors;

    public static void main(String[] args) {
        Pallet pallet = new BlackAndWhitePallet();

        int width = 500;
        int height = (int) (2.0 * width / 3.5);

        int precision = 128;
        Mandelbrot mandelbrot = new Mandelbrot(precision);

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (e.getButton()) {
                    case 1:
                        mandelbrot.zoomIn(e.getX(), e.getY());
                        break;
                    default:
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                switch (c) {
                    case '+':
                        mandelbrot.increaseIterations();
                        break;
                    case '-':
                        mandelbrot.decreaseIterations();
                        break;
                    default:
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };

        Canvas canvas = new SwingCanvas(width, height, pallet, mouseListener, keyListener);
        mandelbrot.setCanvas(canvas);
        mandelbrot.draw();

//        Canvas canvas = new Canvas() {
//            @Override
//            public void drawPoint(int x, int y, int palletColor) {
//
//            }
//
//            @Override
//            public void update() {
//
//            }
//
//            @Override
//            public int getWidth() {
//                return width;
//            }
//
//            @Override
//            public int getHeight() {
//                return height;
//            }
//        };
//
//        int burnin = 10;
//        for (int i = 0;i < burnin;++i) {
//            mandelbrot.draw(canvas);
//        }
//
//        int iterations = 10;
//        long start = System.currentTimeMillis();
//        for (int i = 0;i < iterations;++i) {
//            mandelbrot.draw(canvas);
//        }
//        long time = System.currentTimeMillis() - start;
//        System.out.println("Executed [" + iterations + "] in [" + time + "] ms");
    }

    private void zoomIn(int x, int y) {
        BigDecimal newRealRange = maxReal.subtract(minReal, mathContext).multiply(new BigDecimal(0.75, mathContext), mathContext).divide(new BigDecimal(2, mathContext), mathContext);
        BigDecimal newImaginaryRange = maxImaginary.subtract(minImaginary, mathContext).multiply(new BigDecimal(0.75, mathContext), mathContext).divide(new BigDecimal(2, mathContext), mathContext);

        IntFunction<BigDecimal> xFunction = createTransform(mathContext, minReal, maxReal, canvas.getWidth());
        IntFunction<BigDecimal> yFunction = createTransform(mathContext, minImaginary, maxImaginary, canvas.getHeight());

        BigDecimal newRealCenter = xFunction.apply(x);
        BigDecimal newImaginaryCenter = yFunction.apply(y);

        minReal = newRealCenter.subtract(newRealRange, mathContext);
        maxReal = newRealCenter.add(newRealRange, mathContext);
        minImaginary = newImaginaryCenter.subtract(newImaginaryRange, mathContext);
        maxImaginary = newImaginaryCenter.add(newImaginaryRange, mathContext);

        draw();
    }

    private void increaseIterations() {
        maxIterations += 5;
        System.out.println("Max iterations increased to [" + maxIterations + "]");
        draw();
    }

    private void decreaseIterations() {
        if (maxIterations > 1) {
            --maxIterations;
            System.out.println("Max iterations decreased to [" + maxIterations + "]");
            draw();
        }
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

        maxIterations = 1;

        processors = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(processors);
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    private Callable<Void> draw(IntFunction<BigDecimal> xFunction, IntFunction<BigDecimal> yFunction, int height, int pixelStart, int pixelEnd) {
        return () -> {
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
                for (x = pixelStart;x < pixelEnd;++x) {
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
            return null;
        };
    }

    public void draw() {
        System.out.print("Drawing... ");
        long start = System.currentTimeMillis();
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        IntFunction<BigDecimal> xFunction = createTransform(mathContext, minReal, maxReal, width);
        IntFunction<BigDecimal> yFunction = createTransform(mathContext, minImaginary, maxImaginary, height);

        int incrementPerThread = 1 + width / processors;

        int pixelStart = 0;
        int pixelEnd;
        List<Future<Void>> futureList = new LinkedList<>();
        while (pixelStart < width) {
            pixelEnd = Math.min(pixelStart + incrementPerThread, width);
            Future<Void> future = executorService.submit(draw(xFunction, yFunction, height, pixelStart, pixelEnd));
            futureList.add(future);
            pixelStart = pixelEnd;
        }

        futureList.forEach((f) -> {
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        long calculateEndTime = System.currentTimeMillis();
        canvas.update();
        long drawEndTime = System.currentTimeMillis();

        long calculateTime = calculateEndTime - start;
        long drawTime = drawEndTime - calculateEndTime;

        System.out.println("Complete! Calculation [" + calculateTime + " ms], drawing [" + drawTime + " ms]");

    }

    private static IntFunction<BigDecimal> createTransform(MathContext mathContext, BigDecimal min, BigDecimal max, int size) {
        BigDecimal factor = max.subtract(min, mathContext).divide(new BigDecimal(size, mathContext), mathContext);
        return (x) -> new BigDecimal(x, mathContext).multiply(factor, mathContext).add(min, mathContext);
    }
}
