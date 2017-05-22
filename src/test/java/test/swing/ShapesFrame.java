package test.swing;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;
import org.pushingpixels.trident.swing.SwingRepaintTimeline;

public class ShapesFrame extends JFrame {
    public static final Color COLOR_BLUE = new Color(128, 128, 255);
    public static final Color COLOR_GREEN = new Color(128, 255, 128);

    public class ShapesPanel extends JComponent {
        private List<MyShape> shapes;
        private boolean toAddRectangle;

        private Color topColor;

        private Color bottomColor;

        public ShapesPanel() {
            this.shapes = new ArrayList<MyShape>();
            this.topColor = COLOR_BLUE;
            this.bottomColor = COLOR_GREEN;

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    addShape(e.getPoint());
                }
            });

            // animate the gradient endpoint colors in an infinite timeline
            Timeline colorTimeline = new SwingRepaintTimeline(this);
            colorTimeline.addPropertyToInterpolate("topColor", COLOR_BLUE, COLOR_GREEN);
            colorTimeline.addPropertyToInterpolate("bottomColor", COLOR_GREEN, COLOR_BLUE);
            colorTimeline.setDuration(1000);
            colorTimeline.playLoop(RepeatBehavior.REVERSE);
        }

        public void setTopColor(Color topColor) {
            this.topColor = topColor;
        }

        public void setBottomColor(Color bottomColor) {
            this.bottomColor = bottomColor;
        }

        public void addShape(MyShape shape) {
            synchronized (this.shapes) {
                this.shapes.add(shape);
            }
        }

        public void removeShape(MyShape shape) {
            synchronized (this.shapes) {
                this.shapes.remove(shape);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setPaint(new GradientPaint(0, 0, this.topColor, 0, getHeight(), this.bottomColor));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            synchronized (this.shapes) {
                for (MyShape shape : this.shapes) {
                    shape.paint(g2d);
                }
            }
            g2d.dispose();
        }

        private void addShape(Point point) {
            int x = point.x;
            int y = point.y;

            if (toAddRectangle) {
                final MyShape shape = new MyRectangle(x, y, 0, 0);
                addShape(shape);
                Timeline timelineRectangleFade = new Timeline(shape);
                timelineRectangleFade.addPropertyToInterpolate("x", x, x - 100);
                timelineRectangleFade.addPropertyToInterpolate("y", y, y - 100);
                timelineRectangleFade.addPropertyToInterpolate("width", 0, 200);
                timelineRectangleFade.addPropertyToInterpolate("height", 0, 200);
                timelineRectangleFade.addPropertyToInterpolate("rotation", 0, 180);
                timelineRectangleFade.addPropertyToInterpolate("opacity", 1.0f, 0.0f);
                timelineRectangleFade.addCallback(new TimelineCallbackAdapter() {
                    @Override
                    public void onTimelineStateChanged(TimelineState oldState,
                            TimelineState newState, float durationFraction,
                            float timelinePosition) {
                        if (newState == TimelineState.DONE)
                            removeShape(shape);
                    }
                });
                timelineRectangleFade.setDuration(1000);
                timelineRectangleFade.play();
            } else {
                final MyShape shape = new MyCircle(x, y, 0);
                addShape(shape);
                Timeline timelineCircleFade = new Timeline(shape);
                timelineCircleFade.addPropertyToInterpolate("radius", 0, 100);
                timelineCircleFade.addPropertyToInterpolate("opacity", 1.0f, 0.0f);
                timelineCircleFade.addCallback(new TimelineCallbackAdapter() {
                    @Override
                    public void onTimelineStateChanged(TimelineState oldState,
                            TimelineState newState, float durationFraction,
                            float timelinePosition) {
                        if (newState == TimelineState.DONE)
                            removeShape(shape);
                    }
                });
                timelineCircleFade.setDuration(1000);
                timelineCircleFade.play();
            }
            toAddRectangle = !toAddRectangle;
        }
    }

    public interface MyShape {
        public void paint(Graphics g);
    }

    public class MyRectangle implements MyShape {
        float x;

        float y;

        float width;

        float height;

        float opacity;

        float rotation;

        public MyRectangle(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.opacity = 1.0f;
            this.rotation = 0.0f;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public void setOpacity(float opacity) {
            this.opacity = opacity;
        }

        public void setRotation(float rotation) {
            this.rotation = rotation;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setComposite(AlphaComposite.SrcOver.derive(this.opacity));
            g2d.setColor(COLOR_GREEN);
            float xc = this.x + this.width / 2;
            float yc = this.y + this.height / 2;
            g2d.translate((int) xc, (int) yc);
            g2d.rotate(this.rotation * Math.PI / 180.0);
            g2d.fill(new Rectangle2D.Float(-this.width / 2, -this.height / 2, this.width,
                    this.height));
            g2d.dispose();
        }
    }

    public class MyCircle implements MyShape {
        float x;

        float y;

        float radius;

        float opacity;

        public MyCircle(float x, float y, float radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.opacity = 1.0f;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public void setOpacity(float opacity) {
            this.opacity = opacity;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setComposite(AlphaComposite.SrcOver.derive(this.opacity));
            g2d.setColor(COLOR_GREEN);
            g2d.fill(new Ellipse2D.Float(this.x - this.radius, this.y - this.radius, 2 * radius,
                    2 * radius));
            g2d.dispose();
        }
    }

    public ShapesFrame() {
        super("Swing Shapes");
        ShapesPanel sPanel = new ShapesPanel();
        this.add(sPanel, BorderLayout.CENTER);

        this.setSize(600, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShapesFrame().setVisible(true));
    }
}
