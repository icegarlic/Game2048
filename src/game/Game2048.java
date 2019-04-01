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
                    case KeyEvent.VK_UP:
                        moveUp();
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
        graphics2D.fillRoundRect(200, 100, 499, 499, 15, 15);

        if (gamestate == State.running) {
            for (int r = 0; r < side; r++) {
                for (int c = 0; c < side; c++) {
                    if (tiles[r][c] == null) {
                        graphics2D.setColor(emptyColor);
                        graphics2D.fillRoundRect(215 + c * 121, 155 + r * 121, 106, 106, 7, 7);
                    } else {
                        drawTile(graphics2D, r, c);
                    }
                }
            }
        } else {
            graphics2D.setColor(startColor);
            graphics2D.fillRoundRect(215, 115, 469, 469, 7, 7);

            graphics2D.setColor(gridColor.darker());
            graphics2D.setFont(new Font("HUAWEI", Font.BOLD, 128));
            graphics2D.drawString("2048", 310, 270);

            graphics2D.setFont(new Font("HUAWEI", Font.BOLD, 20));

            if (gamestate == State.won) {
                graphics2D.drawString("you made it", 390, 350);
            } else if (gamestate == State.over) {
                graphics2D.drawString("game over", 400, 350);
            }

            graphics2D.setColor(gridColor);
            graphics2D.drawString("click to start a new game", 330, 470);
            graphics2D.drawString("use arrow keys to move tiles", 310, 530);
        }
    }

    void drawTile(Graphics2D graphics2D, int r, int c) {
        int value = tiles[r][c].getValue();

        graphics2D.setColor(colorTable[(int) (Math.log(value) / Math.log(2)) + 1]);
        graphics2D.fillRoundRect(215 + c * 121,115 + r * 121,106,106,7,7);
        String s = String.valueOf(value);

        graphics2D.setColor(value < 128 ? colorTable[0] : colorTable[1]);

        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int asc = fontMetrics.getAscent();
        int dec = fontMetrics.getDescent();

        int x = 215 + c * 121 + (106 - fontMetrics.stringWidth(s)) / 2;
        int y = 115 + r * 121 + (asc + (106 - (asc + dec)) / 2);
        graphics2D.drawString(s, x, y);
    }

    private void addRandomTile() {
        int pos = random.nextInt(side * side);
        int row, col;
        do {
            pos = (pos + 1) % (side * side);
            row = pos / side;
            col = pos % side;
        } while (tiles[row][col] != null);

        int val = random.nextInt(10) == 0 ? 4 : 2;
        tiles[row][col] = new Tile(val);
    }

    private boolean move(int countDownFrom, int yIncr, int xIncr) {
        boolean moved = false;

        for (int i = 0; i < side * side; i++) {
            int j = Math.abs(countDownFrom - i);

            int r = j / side;
            int c = j % side;

            if (tiles[r][c] == null)
                continue;
            int nextR = r + yIncr;
            int nextC = c + xIncr;

            while (nextR >= 0 && nextR < side && nextC >= 0 && nextC < side) {
                Tile next = tiles[nextR][nextC];
                Tile curr = tiles[r][c];

                if (next == null) {
                    if (checkingAvailableMoves)
                        return true;
                    tiles[nextR][nextC] = curr;
                    tiles[r][c] = null;
                    r = nextR;
                    c = nextC;
                    nextR += yIncr;
                    nextC += xIncr;
                    moved = true;
                } else if (next.canMergeWith(curr)) {

                    if (checkingAvailableMoves)
                        return true;

                    int value = next.mergeWith(curr);
                    if (value > highest)
                        highest = value;
                    score += value;
                    tiles[r][c] = null;
                    moved = true;
                    break;
                } else
                    break;
            }
        }
        if (moved) {
            if (highest < target) {
                clearMerged();
                addRandomTile();
                if (!movesAvailable()) {
                    gamestate = State.over;
                }
            } else if (highest == target)
                gamestate = State.won;
        }
        return moved;
    }

    boolean moveUp() {
        return move(0, -1, 0);
    }

    boolean moveDown() {
        return move(side * side - 1, 1, 0);
    }

    boolean moveLeft() {
        return move(0, 0, -1);
    }

    boolean moveRight() {
        return move(side * side - 1, 0, 1);
    }

    void clearMerged() {
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile != null)
                    tile.setMerged(false);
    }

    boolean movesAvailable() {
        checkingAvailableMoves = true;
        boolean hasMoves = moveUp() || moveDown() || moveLeft() || moveRight();
        checkingAvailableMoves = false;
        return hasMoves;
    }

}
