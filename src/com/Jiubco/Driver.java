package com.Jiubco;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.Random;

public class Driver implements MouseMotionListener {


    private static final int WIDTH = 1600, HEIGHT = 900;

    private int lineResolution = 2100;
    private int lineResolutionEntity = 2;
    private int FOV = 400;

    private int mouseX = 0, mouseY = 0;
    private int x = 200, y = 400;

    private static String title = "RayCasting";
    private static Canvas canvas;
    private static LinkedList<Line2D.Float> lines;
    private static Random random = new Random(100);

    public Driver() {
        lines = buildLines();
        JFrame frame = new JFrame();
        frame.setSize(1600, 900);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
        frame.setTitle(title);
        frame.add(canvas = new Canvas());
        canvas.addMouseMotionListener(this);

        new Thread(r).start();
    }

    public static void main(String[] args) {
        new Driver();
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            while (true) {
                tick();
                render();
            }
        }
    };

    public static LinkedList<Line2D.Float> buildLines() {
        int numLines = 10;
        LinkedList<Line2D.Float> lines = new LinkedList<Line2D.Float>();
        for (int i = 0; i < numLines; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            lines.add(new Line2D.Float(x1, y1, x2, y2));
        }
        return lines;
    }

    public void tick() {
    }

    public void render() {
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        //Begin


        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        g.setColor(Color.DARK_GRAY);
        for (Line2D.Float l : lines) {
            g.drawLine((int) l.x1, (int) l.y1, (int) l.x2, (int) l.y2);
        }

        g.setColor(Color.RED);
        LinkedList<Line2D.Float> rays = calcRays(lines, mouseX, mouseY, lineResolution, FOV);
        for (Line2D.Float l : rays) {
            g.drawLine((int) l.x1, (int) l.y1, (int) l.x2, (int) l.y2);
        }

        //Stop
        g.dispose();
        bs.show();
    }

    private LinkedList<Line2D.Float> calcRays(LinkedList<Line2D.Float> lines, int x, int y, int resolution, int maxDist) {
        LinkedList<Line2D.Float> rays = new LinkedList<Line2D.Float>();
        for (int i = 0; i < resolution; i++) {
            double dir = (Math.PI * 2) * ((double) i / resolution);
            float minDist = maxDist;
            for (Line2D.Float l : lines) {
                float dist = getRayCast(x, y, x + (float) Math.cos(dir) * maxDist, y + (float) Math.sin(dir) * maxDist, l.x1, l.y1, l.x2, l.y2);
                if (dist < minDist && dist > 0) {
                    minDist = dist;
                }
            }
            rays.add(new Line2D.Float(x, y, x + (float) Math.cos(dir) * minDist, y + (float) Math.sin(dir) * minDist));
        }
        return rays;
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public static float getRayCast(float p0_x, float p0_y, float p1_x, float p1_y, float p2_x, float p2_y, float p3_x, float p3_y) {
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = p1_x - p0_x;
        s1_y = p1_y - p0_y;
        s2_x = p3_x - p2_x;
        s2_y = p3_y - p2_y;

        float s, t;
        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            float x = p0_x + (t * s1_x);
            float y = p0_y + (t * s1_y);

            return dist(p0_x, p0_y, x, y);
        }

        return -1; // No collision
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
