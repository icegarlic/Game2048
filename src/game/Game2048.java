package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Game2048 extends JPanel {

    enum State {
        start, won, running, over
    }

    // 随机tiles 的颜色
    final Color[] colorTable = {
            new Color(0x701710), new Color(0xFF7472), new Color(0xfff4d3),
            new Color(0xFF84A1), new Color(0xe7b08e), new Color(0xe7bf8e),
            new Color(0xffc4c3), new Color(0xE7948e), new Color(0xbe7e56),
            new Color(0xbe5e56), new Color(0x9c3931), new Color(0x701710)};
    final static int target = 2048;

    static int highest;
    static int score;

    private Color gridColor = new Color(0xBBADA0);
    private Color emptyColor = new Color(0xCDC1B9);
    private Color startColor = new Color(0xFFECBB);

    private Random random = new Random();

    private Tile[][] tiles;
    private int side = 4;
    private State gamestate = State.start;
    private boolean checkingAvailableMoves;

    public Game2048() {
        // 窗口大小
        setPreferredSize(new Dimension(900, 700));
        // 窗口颜色
        setBackground(new Color(0xFAF8EF));
        // 字体大小
        setFont(new Font("HUAWEI", Font.BOLD, 48));
        setFocusable(true);

        // 添加鼠标监听器
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startGame();
                repaint();
            }
        });

        // 添加键盘监听器
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP;
                        moveUP();
                        break;
                    case KeyEvent.VK_DOWN:
                        moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRight();
                        break;
                }
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawGird(graphics2D);
    }

    void startGame() {
        if (gamestate != State.running) {
            score = 0;
            highest = 0;
            gamestate = State.running;
            tiles = new Tile[side][side];
            addRandomTile();
            addRandomTile();
        }
    }

    void drawGird(Graphics2D graphics2D) {
        graphics2D.setColor(gridColor);
    }

}
