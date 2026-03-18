import models.*;
import models.Button;
import models.Point;
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
import java.util.List;

public class App {
    /**
     * Determines what interacting with the applicataion does
     */
    private enum DrawType {
        Normal,
        Poly,
        Circle,
        Square,
        Erase,
        Edit
    }

    /**
     * Determines the width of the lines drawn
     */
    public enum Width {
        small,
        medium,
        big
    }
    private final JPanel panel;
    private final Raster raster;

    /**
     * "Common" bounding boxes
     */
    private static class Bounding_boxes {
        public static ArrayList<models.Point> BG = new ArrayList<>(List.of(
                new Point(0, 0),
                new Point(799, 0),
                new Point(799, 100),
                new Point(0, 100)
        ));
        public static ArrayList<models.Point> BUTTON = new ArrayList<>(List.of(
                new Point(0, 0),
                new Point(80, 0),
                new Point(80, 80),
                new Point(0, 80)
        ));
        public static ArrayList<models.Point> SQUARE = new ArrayList<>(List.of(
                new Point(-1, -1),
                new Point(1, -1),
                new Point(1, 1),
                new Point(-1, 1)
        ));
    }

    private final Rasterizer rasterizer;
    private MouseAdapter mouseAdapter;
    private KeyListener keyListener;

    /**
     * Helper point 1 - generally used when initiating an action
     */
    private models.Point helperP1;
    /**
     * Helper point 2 - generally used for progress in an action and ending it
     */
    private models.Point helperP2;

    /**
     * Finished lines to be drawn
     */
    private final ArrayList<Line> finished_lines = new ArrayList<>();
    /**
     * Finished shapes minus circles to be drawn
     */
    private final ArrayList<models.Button> finished_shapes = new ArrayList<>();
    /**
     * Finished circles to be drawn
     */
    private final ArrayList<models.Circle> finished_circles = new ArrayList<>();

    /**
     * If the dotted mode is to be used
     */
    private boolean dottedMode = false;
    /**
     * If shift mode is active - used for straight lines and square
     */
    private boolean shiftMode = false;
    /**
     * DrawMode - holds the user interaction state of the application
     */
    private DrawType DrawMode = DrawType.Normal;
    /**
     * Value used when drawing a new polygon
     */
    private models.Button currentPoly = null;
    /**
     * Value used when drawing a new circel
     */
    private models.Circle currentCircle = null;
    /**
     * Whether to fill in the shape
     */
    private boolean fillMode = false;
    /**
     * Color to use when drawing shapes
     */
    private Color currentColor = Color.RED;
    /**
     * Used for holding the mouse position when drawing new polygon
     */
    private models.Point mousePos;
    /**
     * Holds the state of the lines to be drawn
     */
    private Width currwidth = Width.medium;
    // Old code
//    private final models.Shape triangle = new models.Shape(new ArrayList(List.of(new models.Point(1,0), new Point(0, 1), new Point(1, 1))), new Color(255, 255, 255), true, 1, new models.Point(50,50));
    /**
     * Used to unhighlight the button when switching modes
     */
    private int currentbtn = 0;
    /**
     * Holds the ui, when changing it needs to change a lot
     */
    private final ArrayList<models.Shape> ui_shapes = new ArrayList<>(List.of(
            // BG
            new models.Shape(Bounding_boxes.BG, Color.GRAY, true, 1, new models.Point(0, 0), currwidth.ordinal()+1),
            // Mode_LINE
            new models.Button(Bounding_boxes.BUTTON, Color.WHITE, Color.LIGHT_GRAY, true, 1, new models.Point(0, 10), () -> {
                DrawMode = DrawType.Normal;
            }),
            new models.Shape(new ArrayList<>(List.of(new models.Point(10, 20), new models.Point(70, 80))), Color.BLACK, true, 1, new models.Point(0, 0), currwidth.ordinal()+1),
            // Mode_SQUARE
            new models.Button(Bounding_boxes.BUTTON, Color.WHITE, Color.LIGHT_GRAY, true, 1, new models.Point(90, 10), () -> {
                DrawMode = DrawType.Square;
            }),
            new models.Shape(new ArrayList<>(List.of(new models.Point(100, 20), new models.Point(160, 20), new models.Point(160, 80), new models.Point(100, 80))), Color.BLACK, false, 1, new models.Point(0, 0), currwidth.ordinal()+1),
            // Mode_Edit
            new models.Button(Bounding_boxes.BUTTON, Color.WHITE, Color.LIGHT_GRAY, true, 1, new models.Point(180, 10), () -> {
                DrawMode = DrawType.Edit;
            }),
            new models.Shape(new ArrayList<>(List.of(
                    new models.Point(215, 15), // Top
                    new models.Point(250, 60), // Top
                    new models.Point(230, 60), // Top
                    new models.Point(230, 80), // Top
                    new models.Point(210, 80), // Top
                    new models.Point(220, 60), // Top
                    new models.Point(205, 60) // Top
            )), Color.BLACK, false, 1, new models.Point(0, 0), currwidth.ordinal()+1),
            // Mode_CIRCLE
            new models.Button(Bounding_boxes.BUTTON, Color.WHITE, Color.LIGHT_GRAY, true, 1, new models.Point(270, 10), () -> {
                DrawMode = DrawType.Circle;
            }),
            // Mode_POLY
            new models.Button(Bounding_boxes.BUTTON, Color.WHITE, Color.LIGHT_GRAY, true, 1, new models.Point(360, 10), () -> {
                DrawMode = DrawType.Poly;
            }),
            new models.Shape(new ArrayList<>(List.of(
                    new models.Point(370, 20),
                    new models.Point(390, 63),
                    new models.Point(418, 32),
                    new models.Point(420, 65)
            )), Color.BLACK, false, 1, new models.Point(0, 0), currwidth.ordinal()+1),
            // Colors
            new models.Button(Bounding_boxes.BUTTON, Color.RED, true, (float)0.5, new models.Point(450, 10), () -> {
                SetColor(Color.RED);
            }),
            new models.Button(Bounding_boxes.BUTTON, Color.GREEN, true, (float)0.5, new models.Point(500, 10), () -> {
                SetColor(Color.GREEN);
            }),
            new models.Button(Bounding_boxes.BUTTON, Color.BLUE, true, (float)0.5, new models.Point(550, 10), () -> {
                SetColor(Color.BLUE);
            }),
            new models.Button(Bounding_boxes.BUTTON, Color.WHITE, true, (float)0.5, new models.Point(600, 10), () -> {
                SetColor(Color.WHITE);
            }),
            new models.Button(Bounding_boxes.BUTTON, Color.BLACK, true, (float)0.5, new models.Point(450, 60), () -> {
                SetColor(Color.BLACK);
            }),
            new models.Button(Bounding_boxes.BUTTON, Color.YELLOW, true, (float)0.5, new models.Point(500, 60), () -> {
                SetColor(Color.YELLOW);
            }),
            new models.Button(Bounding_boxes.BUTTON, Color.ORANGE, true, (float)0.5, new models.Point(550, 60), () -> {
                SetColor(Color.ORANGE);
            }),
            new models.Button(Bounding_boxes.BUTTON, Color.MAGENTA, true, (float)0.5, new models.Point(600, 60), () -> {
                SetColor(Color.MAGENTA);
            }),
            // Current Color display -- no func
            new models.Button(Bounding_boxes.BUTTON, Color.RED, true, (float)0.5, new models.Point(650, 10), () -> {
                fillMode = !fillMode;
                applyFillMode();
            }),
            // Tloustka - heheha
            new models.Button(Bounding_boxes.BUTTON, Color.WHITE, true, (float)0.35, new models.Point(650, 60), this::changeWidth),
            // Clear
            new models.Button(Bounding_boxes.BUTTON, Color.WHITE, Color.LIGHT_GRAY, true, 1, new models.Point(700, 10), () -> {
                if (DrawMode == DrawType.Erase) {
                    clear(Color.BLACK.getRGB());
                }
                DrawMode = DrawType.Erase;
            }),
            new models.Shape(new ArrayList<>(List.of(
                    new Point(710, 20), // TL
                    new Point(750, 20), // TR T
                    new Point(770, 30), // TR R
                    new Point(770, 40), // TR R
                    new Point(710, 40), // TR R
                    new Point(710, 50), // TR R
                    new Point(770, 50), // TR R
                    new Point(770, 30), // TR R
                    new Point(770, 80), // BR
                    new Point(710, 80)  // BL
            )), Color.BLACK, false, (float)1, new models.Point(0, 0), currwidth.ordinal()+1)
        )
    );
    /**
     * Since I made the circle a separate class :trollface:, it needs to be outside of ui_elements
     */
    private models.Circle uiCircle = new Circle(new models.Point(310, 50), 30, Color.BLACK, false, currwidth.ordinal()+1, () -> {

    });

    /**
     * Visual of the fill mode
     */
    private void applyFillMode() {
        uiCircle.isFilled = fillMode;
        // 2 4 6 9
        ui_shapes.get(2).isFilled = fillMode;
        ui_shapes.get(4).isFilled = fillMode;
        // Now is mouse, no need for it to be filled in
//        ui_shapes.get(6).isFilled = fillMode;
        ui_shapes.get(9).isFilled = fillMode;
    }

    /**
     * Again, ui stuff
     */
    private void changeWidth() {
        if (currwidth == Width.small) {
            currwidth = Width.medium;
            ui_shapes.get(ui_shapes.size()-3).SetSize(0.35f);
        }
        else if (currwidth == Width.medium){
            currwidth = Width.big;
            ui_shapes.get(ui_shapes.size()-3).SetSize(0.5f);
        }
        else if (currwidth == Width.big) {
            currwidth = Width.small;
            ui_shapes.get(ui_shapes.size()-3).SetSize(0.2f);
        }
        ui_shapes.get(2).SetWidth(currwidth.ordinal()+1);
        ui_shapes.get(4).SetWidth(currwidth.ordinal()+1);
        ui_shapes.get(6).SetWidth(currwidth.ordinal()+1);
        ui_shapes.get(9).SetWidth(currwidth.ordinal()+1);
        uiCircle.SetWidth(currwidth.ordinal()+1);
        Redraw();
    }

    /**
     * Used for holding the currently drawn shape
     */
    private models.Button currentShape = null;

    /**
     * Changes the color including the ui
     * @param c The color to change to
     */
    private void SetColor(Color c) {
        this.currentColor = c;
        ((models.Button)ui_shapes.get(ui_shapes.size()-4)).SetColor(this.currentColor);
        ((models.Button)ui_shapes.get(ui_shapes.size()-4)).setCol2(this.currentColor);
        Redraw();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    /**
     * On clear - resets the app essentially
     * @param color Background color
     */
    public void clear(int color) {
        raster.setClearColor(color);
        finished_lines.clear();
        finished_shapes.clear();
        finished_circles.clear();
        currentShape = null;
        currentPoly = null;
        helperP1 = null;
        helperP2 = null;
        Redraw();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0xaaaaaa);
        ((models.Button)ui_shapes.get(1)).Toggle();
        currentbtn = 1;
        panel.repaint();
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("Delta : " + this.getClass().getName());
        /*
         * No.
         */
        frame.setResizable(false);
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

        rasterizer = new TrivialRasterizer(raster, Color.CYAN, 2);
    }

    /**
     * Helper func to draw the ui
     */
    private void DrawUI() {
        //bg
        for (models.Shape s : ui_shapes) {
            for (Line l : s.GetLines()) {
                rasterizer.rasterize(l);
            }
            rasterizer.rasterize(s);
        }
        rasterizer.rasterize(uiCircle);
    }

    /**
     * Redraws the frame from scratch.
     */
    private void Redraw() {
        raster.clear();
        // Draw lines
        for (Line l : finished_lines) {
            if (l == null) continue;
            l.pointsBorder.clear();
            l.pointsBorder = rasterizer.rasterize(l);
        }
//        for (Polygon p : finished_polygons) {
//            for (Line l : p.GetLines()) {
//                rasterizer.rasterize(l);
//                points.add(l.getPointA());
//                points.add(l.getPointB());
//            }
//
//            rasterizer.rasterize(p);
//        }
        // Draw shapes
        for (models.Button s : finished_shapes) {
            if (s == null) continue;
            s.pointsBorder.clear();
            for (Line l : s.GetLines()) {
                s.pointsBorder.addAll(rasterizer.rasterize(l));
            }
            rasterizer.rasterize(s);
        }
        // If there is a valid currPoly to display, do so
        if (currentPoly != null && currentPoly.GetLines() != null) {
            for (Line l : currentPoly.GetLines()) {
                rasterizer.rasterize(l);
            }
            ArrayList<models.Point> ps = currentPoly.GetPoints();
            if (ps.size() >= 2) {
                models.Line lA = new models.Line(ps.get(0), mousePos, currentPoly.GetColor(), true, currwidth.ordinal()+1);
                models.Line lB = new models.Line(ps.get(ps.size() - 1), mousePos, currentPoly.GetColor(), true, currwidth.ordinal()+1);
                rasterizer.rasterize(lA);
                rasterizer.rasterize(lB);
            }
        }
        // Shape -//-
        if (currentShape != null) {
            for (Line l : currentShape.GetLines()) {
                rasterizer.rasterize(l);
            }
            rasterizer.rasterize(currentShape);
        }
        for (Circle c : finished_circles) {
            if (c == null) continue;
            c.pointsBorder.clear();
            c.pointsBorder = rasterizer.rasterize(c);
        }
        if (currentCircle != null) {
            rasterizer.rasterize(currentCircle);
        }
        DrawUI();
        panel.repaint();
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
                // Old controls
//                if (c == 'c') {
//                    clear(Color.BLACK.getRGB());
//                }
//                if (c == '0') {
//                    DrawMode = DrawType.Normal;
//                }
//                if (c == '1') {
//                    if (DrawMode != DrawType.Poly) {
//                        DrawMode = DrawType.Poly;
//                        currentPoly = new models.Polygon(Color.WHITE, fillMode);
//                    }
//                }
//                if (c == '2') {
//                    if (DrawMode != DrawType.Tri) {
//                        DrawMode = DrawType.Tri;
//                    }
//                }
                // confirm poly
                if (e.getKeyCode() == 10) {
                    if (DrawMode == DrawType.Poly) {
                        if (currentPoly.GetPoints().size() > 2) {
                            currentPoly.Finish();
                            finished_shapes.add(currentPoly);
                            currentPoly = null;
                            Redraw();
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

    /**
     * The shape currently being moved
     */
    private IChangeOrigin moveShape;
    /**
     * The original point of the shape = used for scaling, rotation
     */
    private Point origin = null;
    /**
     * Used for calculating scale
     */
    private Point origin_mouse = null;
    /**
     * Whether the user managed to hit the 1 pixel button
     */
    private boolean resizeMode = false;

    /**
     * Used for a callback
     * @param index The index
     */
    private void MarkMovingObjectShape(int index) {
        if (moveShape != null) return;
        moveShape = finished_shapes.get(index);
        origin = moveShape.GetOrigin();
    }
    /**
     * Used for a callback
     * @param index The index
     */
    private void MarkMovingObjectPolygon(int index) {
        if (moveShape != null) return;
        moveShape = finished_shapes.get(index);
        origin = moveShape.GetOrigin();
    }
    /**
     * Used for a callback
     * @param index The index
     */
    private void MarkMovingObjectCircle(int index) {
        if (moveShape != null) return;
        moveShape = finished_circles.get(index);
        origin = moveShape.GetOrigin();
    }
    private void createAdapter() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                models.Point p = new models.Point(e.getX(), e.getY());
                // handle edit and erase modes
                if (DrawMode == DrawType.Edit || DrawMode == DrawType.Erase) {
                    // Go through all the thingies
                    boolean fin = false;
                    for (Circle c : finished_circles) {
                        if (c == null) continue;
                        if (!fin) {
                            if (c.getInsidePoints().contains(p)) {
                                fin = true;
                                c.onClick.OnClick();
                                resizeMode = false;
                            }
                            if (DrawMode == DrawType.Edit) {
                                if (c.pointsBorder.contains(p)) {
                                    c.onClick.OnClick();
                                    fin = true;
                                    resizeMode = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!fin) {
                        for (Button c : finished_shapes) {
                            if (c == null) continue;
                            if (!fin) {
                                if (c.GetAllInsidePoints().contains(p)) {
                                    fin = true;
                                    c.onClick.OnClick();
                                    resizeMode = false;
                                }
                                if (DrawMode == DrawType.Edit) {
                                    if (c.pointsBorder.contains(p)) {
                                        c.onClick.OnClick();
                                        fin = true;
                                        resizeMode = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!fin) {
                            for (Line l : finished_lines) {
                                if (l == null) continue;
                                if (l.pointsBorder.contains(p)) {
                                    if (DrawMode == DrawType.Erase) {
                                        finished_lines.set(finished_lines.indexOf(l), null);
                                        break;
                                    }
                                    fin = true;
                                    resizeMode = false;
                                    moveShape = l;
                                    origin_mouse = p;
                                    origin = l.origin;
                                }
                            }
                        }
                    }
                    if (fin && DrawMode != DrawType.Erase) {
                        origin_mouse = p;
                    }
                }
                /*
                 * Do not progress in case of ui
                 */
                if (ui_shapes.getFirst().GetAllInsidePoints().contains(p)) {
                    return;
                }
                if (DrawMode == DrawType.Normal) {
                    helperP1 = p;
                }
                if (DrawMode == DrawType.Circle) {
                    helperP1 = p;
                    int i = finished_circles.size();
                    currentCircle = new models.Circle(p, 1, currentColor, fillMode, currwidth.ordinal()+1, () -> {
                        if (DrawMode == DrawType.Erase) {
                            finished_circles.set(i, null);
                        } else {
                            MarkMovingObjectCircle(i);
                        }
                    });
                }
                if (DrawMode == DrawType.Square) {
                    helperP1 = p;
                    int i = finished_shapes.size();
                    currentShape = new Button(Bounding_boxes.SQUARE, currentColor, fillMode, 1, p, currwidth.ordinal()+1, () -> {
                        if (DrawMode == DrawType.Erase) {
                            finished_shapes.set(i, null);
                        } else {
                            MarkMovingObjectPolygon(i);
                        }
                    });
                }
//                if (DrawMode == DrawType.Tri) {
//                    System.out.println("Dragging tri begin");
//                    currentShape = triangle.clone();
//                    currentShape.SetOrigin(new models.Point(e.getX(), e.getY()));
//                    Redraw();
//                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                helperP2 = new models.Point(e.getX(), e.getY());
                if (ui_shapes.getFirst().GetAllInsidePoints().contains(helperP2)) {
                    //Handle ui logic
                    for (models.Shape s : ui_shapes) {
                        try {
                            models.Button b = (models.Button) s;
                            if (b.IsInBounds(helperP2)) {
                                b.onClick.OnClick();
                                int ind =  ui_shapes.indexOf(b);
                                if (ind == currentbtn) continue;
                                ((models.Button)ui_shapes.get(currentbtn)).Toggle();
                                currentbtn =ind;
                                b.Toggle();
                            }
                        } catch (Exception er) {
                            continue;
                        }

                    }
                    Redraw();
                    return;
                }
                if (DrawMode == DrawType.Normal) {
                    if (DrawMode == DrawType.Normal) {
                        if (shiftMode) {
                            int dx = helperP2.getX() - helperP1.getX();
                            int dy = helperP2.getY() - helperP1.getY();
                            int absDx = Math.abs(dx);
                            int absDy = Math.abs(dy);

                            // Calculate distances to the three possible snap states:
                            // 1. Horizontal: distance to travel is absDy
                            // 2. Vertical: distance to travel is absDx
                            // 3. Diagonal: distance to travel is |absDx - absDy|

                            int distToDiagonal = Math.abs(absDx - absDy);

                            if (distToDiagonal < absDx && distToDiagonal < absDy) {
                                // SNAP TO DIAGONAL: Force |dx| to equal |dy|
                                // We use the larger of the two to determine the length
                                int size = Math.max(absDx, absDy);
                                helperP2.setX(helperP1.getX() + (int)Math.signum(dx) * size);
                                helperP2.setY(helperP1.getY() + (int)Math.signum(dy) * size);
                            } else if (absDx < absDy) {
                                // SNAP TO VERTICAL
                                helperP2.setX(helperP1.getX());
                            } else {
                                // SNAP TO HORIZONTAL
                                helperP2.setY(helperP1.getY());
                            }
                        }
                        Line l = new Line(helperP1, helperP2, currentColor, dottedMode, currwidth.ordinal() + 1);
                        finished_lines.add(l);
                    }
                }

                if (DrawMode == DrawType.Poly) {
                    int i = finished_shapes.size();
                    if (currentPoly == null) currentPoly = new Button(new ArrayList<>(),currentColor, fillMode, 1, new Point(0,0), currwidth.ordinal()+1, () -> {
                        if (DrawMode == DrawType.Erase) {
                            finished_shapes.set(i, null);
                        } else {
                            MarkMovingObjectPolygon(i);
                        }
                    });
                    currentPoly.AddPoint(helperP2);
                }
                if (DrawMode == DrawType.Circle) {
                    finished_circles.add(currentCircle);
                    currentCircle = null;
                }
//                if (DrawMode == DrawType.Tri) {
//                    finished_shapes.add(currentShape);
//                    currentShape = null;
//                }
                if (DrawMode == DrawType.Square) {
                    finished_shapes.add(currentShape);
                    currentShape = null;
                }
                if (DrawMode == DrawType.Edit && moveShape != null) {
                    moveShape.calculateInsidePoints();
                    moveShape = null;
                    origin_mouse = null;
                    origin = null;
                }
                Redraw();
                helperP1 = null;
                helperP2 = null;

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (DrawMode == DrawType.Poly) {
                    mousePos = new models.Point(e.getX(), e.getY());
                    Redraw();
                }

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                helperP2 = new models.Point(e.getX(), e.getY());
                if (ui_shapes.getFirst().GetAllInsidePoints().contains(helperP2)) {
                    return;
                }
                if (DrawMode == DrawType.Normal) {
                        if (shiftMode) {
                            int dx = helperP2.getX() - helperP1.getX();
                            int dy = helperP2.getY() - helperP1.getY();
                            int absDx = Math.abs(dx);
                            int absDy = Math.abs(dy);

                            int distToDiagonal = Math.abs(absDx - absDy);

                            if (distToDiagonal < absDx && distToDiagonal < absDy) {
                                int size = Math.max(absDx, absDy);
                                helperP2.setX(helperP1.getX() + (int)Math.signum(dx) * size);
                                helperP2.setY(helperP1.getY() + (int)Math.signum(dy) * size);
                            } else if (absDx < absDy) {
                                helperP2.setX(helperP1.getX());
                            } else {
                                helperP2.setY(helperP1.getY());
                            }
                        }
                    Redraw();
                    rasterizer.rasterize(new Line(helperP1, helperP2, currentColor, dottedMode, currwidth.ordinal()+1));
                    panel.repaint();
                }

                if (DrawMode == DrawType.Circle) {
                    double dist = helperP1.distanceTo(helperP2) ;
                    currentCircle.setRadius((float)dist);
                    Redraw();
                }
                if (DrawMode == DrawType.Square) {
                    if (shiftMode) {
                        double dist = helperP1.distanceTo(helperP2);
                        int i = finished_shapes.size();
                        currentShape = new Button(Bounding_boxes.SQUARE, currentColor, fillMode, 1, helperP1, currwidth.ordinal()+1, () -> {
                            if (DrawMode == DrawType.Erase) {
                                finished_shapes.set(i, null);
                            } else {
                                MarkMovingObjectShape(i);
                            }
                        });
                        currentShape.SetSize((float)dist);
                    } else {
                        int i = finished_shapes.size();
                        currentShape = new Button(new ArrayList<>(List.of(helperP1, new Point(helperP1.getX(), helperP2.getY()), helperP2, new Point(helperP2.getX(), helperP1.getY()))), currentColor, fillMode, 1, new Point(0,0), currwidth.ordinal()+1, () -> {
                            if (DrawMode == DrawType.Erase) {
                                finished_shapes.set(i, null);
                            } else {
                                MarkMovingObjectShape(i);
                            }
                        });
                    }
                    Redraw();
                }
                if (DrawMode == DrawType.Edit && moveShape != null) {
                    if (resizeMode) {
                        moveShape.SetSize((int) Math.round(moveShape.GetOrigin().distanceTo(helperP2)));
                    } else {
                        Point pos = new models.Point(e.getX(), e.getY());
                        moveShape.SetOrigin(origin.Add(origin_mouse.Sub(pos)));
                    }
                    Redraw();
                }
            }
        };
    }
}