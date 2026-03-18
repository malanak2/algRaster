# algRaster - How to Use

Simple Java Swing raster drawing app with multiple drawing/editing modes.

## What you can do

- Draw free lines
- Draw circles
- Draw polygons (finish with Enter)
- Draw rectangle/square-like shapes
- Change color, stroke width, and fill mode
- Edit and resize existing shapes
- Erase shapes or clear the whole canvas

## Requirements

- Java JDK 17+ (JDK 21 also works)
- Linux/macOS/Windows with a GUI environment (Swing window)

## Build and run

This project currently uses plain `javac`/`java` (no Maven/Gradle wrapper in repo).

```bash
javac -d out $(find src -name '*.java')
java -cp out App
```

## UI overview

The top bar is the control panel. Drawing starts below it.

Mode buttons (left to right):

- `Line` mode
- `Square` mode
- `Edit` mode
- `Circle` mode
- `Polygon` mode
- Color palette
- Fill toggle (uses current preview icon)
- Width toggle (small -> medium -> big)
- Erase/Clear mode

## Controls

- `Mouse drag` in **Line** mode: preview + draw line on release
- `Shift` while drawing line: snaps to horizontal/vertical/diagonal
- `Ctrl` while drawing line: dotted line mode
- `Mouse drag` in **Circle** mode: set circle radius
- `Mouse drag` in **Square** mode:
  - normal drag creates axis-aligned rectangle-like shape
  - with `Shift` creates centered square scaling from start point
- `Click` in **Polygon** mode: adds polygon point
- `Enter` in **Polygon** mode: finalizes polygon (needs at least 3 points)
- `Edit` mode:
  - drag inside shape to move it
  - drag border pixel to resize it
- `Erase` mode:
  - click object to remove it
  - click Erase button again to clear all objects


## Notes / known behavior

- You cannot draw on top toolbar area (it is reserved for UI interaction).
- Polygon preview uses current mouse position while moving.
- Width/fill updates apply to newly created objects; existing objects keep their stored properties unless edited.
- Some operations rely on pixel-accurate border hit testing, so selecting very thin borders can be sensitive.

![heheheha](https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fi.kym-cdn.com%2Fentries%2Ficons%2Ffacebook%2F000%2F000%2F091%2FTrollFace.jpg&f=1&nofb=1&ipt=f52b3a045e9c20433896e27d01543650c59a12413b7226581f1beee4d95f22f7)