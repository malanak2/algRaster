import models.Line;
import models.Polygon;
import rasterizers.Rasterizer;
import rasterizers.TrivialRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;

public class App {

    private final JPanel panel;
    private final Raster raster;

    private final Rasterizer rasterizer;
    private MouseAdapter mouseAdapter;
    private KeyListener keyListener;

    private models.Point helperP1;
    private models.Point helperP2;

    private ArrayList<Line> finished_lines = new ArrayList<>();
    private ArrayList<Polygon> finished_polygons = new ArrayList<>();

    private boolean dottedMode = false;
    private boolean shiftMode = false;
    private int DrawMode = 0;
    private models.Polygon currentPoly = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        panel.repaint();
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("Delta : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();
        createAdapter();
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
        createListener();
        panel.addKeyListener(keyListener);

        rasterizer = new TrivialRasterizer(raster, Color.CYAN);
    }

    private void createListener() {
        keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                if (e.isControlDown()) {
                   dottedMode = true;
                }
                if (c == 'c') {
                    finished_lines = new ArrayList<>();
                    finished_polygons = new ArrayList<>();
                    raster.clear();
                    panel.repaint();
                }
                if (c == '1') {
                    if (DrawMode != 1) {
                        DrawMode = 1;
                        currentPoly = new models.Polygon(Color.WHITE);
                    }
                }
                if (e.getKeyCode() == 10) {
                    if (DrawMode == 1) {
                        if (currentPoly.GetPoints().size() > 2) {
                            currentPoly.Finish();
                            finished_polygons.add(currentPoly);
                            currentPoly = null;
                            DrawMode = 0;
                            raster.clear();
                            for (Line l : finished_lines) {
                                rasterizer.rasterize(l);
                            }
                            for (Polygon p : finished_polygons) {
                                for (Line l : p.GetLines()) {
                                    rasterizer.rasterize(l);
                                }
                            }
                            panel.repaint();
                        }
                    }
                }
                if (e.isShiftDown()) {
                    shiftMode = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!e.isControlDown()) {
                    dottedMode = false;
                }
                if (!e.isShiftDown()) {
                    shiftMode = false;
                }
            }
        };
    }

    private void createAdapter() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (DrawMode == 0) {
                    helperP1 = new models.Point(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                helperP2 = new models.Point(e.getX(), e.getY());
                if (DrawMode == 0) {
                    if (shiftMode) {
                        if (Math.abs(helperP1.getX() - helperP2.getX()) < Math.abs(helperP1.getY() - helperP2.getY())) {
                            helperP2.setX(helperP1.getX());
                        } else {
                            helperP2.setY(helperP1.getY());
                        }
                    }
                    Line l = new Line(helperP1, helperP2, Color.WHITE, dottedMode);
                    rasterizer.rasterize(l);
                    finished_lines.add(l);
                    panel.repaint();
                }
                if (DrawMode == 1) {
                    currentPoly.AddPoint(helperP2);
                }
                helperP1 = null;
                helperP2 = null;

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (DrawMode == 1) {
                    models.Point c = new models.Point(e.getX(), e.getY());
                    ArrayList<models.Point> ps = currentPoly.GetPoints();
                    if (ps.size() < 2) {
                        return;
                    }
                    raster.clear();
                    for (Line l : finished_lines) {
                        rasterizer.rasterize(l);
                    }
                    for (Polygon p : finished_polygons) {
                        for (Line l : p.GetLines()) {
                            rasterizer.rasterize(l);
                        }
                    }
                    for (Line l : currentPoly.GetLines()) {
                        rasterizer.rasterize(l);
                    }
                    models.Line lA = new models.Line(ps.get(0), c, currentPoly.GetColor(), true);
                    models.Line lB = new models.Line(ps.get(ps.size() - 1), c, currentPoly.GetColor(), true);
                    rasterizer.rasterize(lA);
                    rasterizer.rasterize(lB);
                    panel.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (DrawMode == 0) {
                    raster.clear();
                    for (Line l : finished_lines) {
                        rasterizer.rasterize(l);
                    }
                    for (Polygon p : finished_polygons) {
                        for (Line l : p.GetLines()) {
                            rasterizer.rasterize(l);
                        }
                    }
                    helperP2 = new models.Point(e.getX(), e.getY());
                    if (shiftMode) {
                        if (Math.abs(helperP1.getX() - helperP2.getX()) < Math.abs(helperP1.getY() - helperP2.getY())) {
                            helperP2.setX(helperP1.getX());
                        } else {
                            helperP2.setY(helperP1.getY());
                        }
                    }
                    rasterizer.rasterize(new Line(helperP1, helperP2, Color.WHITE, dottedMode));
                    panel.repaint();
                }
            }
        };
    }

}
