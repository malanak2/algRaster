# algRaster Technical Documentation

## 1. Project architecture

The application is a Java Swing raster editor with direct pixel rendering.

- Entry point: `App.main`
- Main orchestrator: `App`
- Data model: classes in `src/models`
- Rendering layer: `src/rasterizers`
- Pixel buffer abstraction: `src/rasters`

The app follows a simple loop:

1. User input updates transient or finished model objects.
2. `App.Redraw()` clears raster and re-rasterizes everything.
3. Swing panel repaints the backing image.

## 2. Core modules

### 2.1 `App` (`src/App.java`)

Responsibilities:

- Creates Swing window and canvas (`JFrame`, `JPanel`)
- Owns global interaction state:
  - mode (`DrawType`)
  - line style (`dottedMode`, `currwidth`)
  - geometry helpers (`helperP1`, `helperP2`)
  - edit state (`moveShape`, `resizeMode`, `origin`, `origin_mouse`)
- Stores drawable objects:
  - `finished_lines`
  - `finished_shapes` (buttons/shapes/polygons)
  - `finished_circles`
- Builds toolbar UI as model objects (`ui_shapes`, `uiCircle`)
- Handles all keyboard and mouse events
- Triggers redraw pipeline

### 2.2 Models (`src/models`)

- `Point`
  - integer coordinates
  - geometry helpers (`distanceTo`, `Add`, `Sub`)
  - equality/hash for point-set checks
- `Line` implements `IChangeOrigin`
  - two endpoints + color + dotted + width
  - stores rasterized border points for hit detection
- `Polygon` implements `IChangeOrigin`
  - generic point-based polygon
  - supports fill, border width, origin, size
  - computes inside pixels by scanline fill
- `Shape` extends `Polygon`
  - transformed shape from base point template and `size`
  - used for UI and square-like primitives
- `Button` extends `Shape`
  - adds `onClick` callback and dual color toggle
  - reused for toolbar buttons and editable polygon-like entities
- `Circle` implements `IChangeOrigin`
  - origin + radius + fill + width + callback
  - computes inside points for fill and hit tests
- `Function`
  - small callback interface (`OnClick`)
- `IChangeOrigin`
  - abstraction for move/resize recalculation in edit mode

### 2.3 Rasterizers (`src/rasterizers`)

- `Rasterizer` interface:
  - `rasterize(Line)`
  - `rasterize(Polygon)`
  - `rasterize(Circle)`
- `TrivialRasterizer` implementation:
  - line drawing via slope-intercept stepping
  - circle drawing via midpoint-style symmetry plotting
  - optional fill by iterating precomputed inside points
  - stroke thickness by drawing a `width x width` block around each plotted pixel

### 2.4 Raster backend (`src/rasters`)

- `Raster` interface abstracts pixel operations
- `RasterBufferedImage` stores pixels in a `BufferedImage`
  - `clear()` fills image with clear color
  - `setPixel()` writes individual pixel
  - `repaint()` draws buffer to Swing graphics context

## 3. Rendering pipeline

Primary redraw path in `App.Redraw()`:

1. `raster.clear()`
2. Re-rasterize finished lines and cache line border points
3. Re-rasterize finished shapes:
   - rasterize each border line
   - rasterize fill (if enabled)
4. Render in-progress objects (`currentPoly`, `currentShape`, `currentCircle`)
5. Re-rasterize finished circles and cache border points
6. Render toolbar UI (`DrawUI()`)
7. `panel.repaint()`

Because redraw is full-frame and immediate, model state is the single source of truth.

## 4. Input and interaction model

### 4.1 Modes (`DrawType`)

- `Normal`: line drawing
- `Poly`: polygon point capture
- `Circle`: circle drawing
- `Square`: shape drawing (rectangle or square-like)
- `Erase`: delete entities / full clear on repeated erase button press
- `Edit`: move or resize existing entities

### 4.2 Keyboard

- `Ctrl` held -> dotted line mode enabled
- `Shift` held -> snapping for line/square interactions
- `Enter` in polygon mode -> finalize current polygon if 3+ points

### 4.3 Mouse flow

- `mousePressed`: initializes drawing or selects editable object
- `mouseDragged`: updates in-progress geometry or edit transform
- `mouseReleased`: commits object to finished list or finalizes edit
- `mouseMoved` in polygon mode: updates dynamic preview link to cursor

## 5. Object editing and hit detection

Hit detection uses cached point lists:

- lines: `Line.pointsBorder`
- polygons/shapes: `GetAllInsidePoints()` and `pointsBorder`
- circles: `getInsidePoints()` and `pointsBorder`

In edit mode:

- click inside object -> move mode
- click border point -> resize mode

Selection priority currently checks circles, then shapes, then lines.

## 6. UI implementation details

Toolbar is not a separate Swing widget set. It is drawn using the same model/rasterization system as canvas content.

- `Bounding_boxes` contains reusable point templates for UI geometry
- `ui_shapes` stores clickable button/shape visuals
- each button executes a lambda callback updating app state

This design keeps rendering uniform but tightly couples UI and drawing logic to `App`.

## 7. Known technical caveats

- `Point.Add` and `Point.Sub` mutate the argument point object and return it; this can introduce surprising side effects in move logic.
- `Line.SetSize`/`GetSize` are placeholders, so generic resize semantics are inconsistent across shape types.
- Edit-hit testing is pixel-list based and can be expensive for large filled shapes.
- Several field and method names are non-standard Java style (`SetColor`, `GetOrigin`, etc.), which may affect maintainability.

## 8. Extension points

Potential next improvements:

1. Introduce controller/service layer to split `App` responsibilities.
2. Replace full-frame rerasterization with dirty-region redraw.
3. Add persistent scene serialization (save/load).
4. Implement robust geometric hit-testing instead of full inside-point lists.
5. Add unit tests for rasterizers and geometry transforms.

