import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A small, self-contained Snake game built with Java Swing.
 *
 * Controls:
 *   Arrow keys or WASD - move
 *   P or Space         - pause/resume
 *   R                  - restart after game over
 */
public class SnakeGame extends JFrame {
    public SnakeGame() {
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new GamePanel());
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame game = new SnakeGame();
            game.setAlwaysOnTop(true);
            game.setVisible(true);
            game.toFront();
            game.requestFocus();
        });
    }

    private static final class GamePanel extends JPanel
            implements ActionListener, KeyListener {
        private static final int CELL_SIZE = 24;
        private static final int COLUMNS = 30;
        private static final int ROWS = 24;
        private static final int TOP_BAR_HEIGHT = 52;
        private static final int TICK_MS = 115;

        private static final Color BACKGROUND = new Color(15, 23, 42);
        private static final Color BOARD = new Color(30, 41, 59);
        private static final Color GRID = new Color(51, 65, 85);
        private static final Color SNAKE_HEAD = new Color(74, 222, 128);
        private static final Color SNAKE_BODY = new Color(34, 197, 94);
        private static final Color FOOD = new Color(251, 113, 133);
        private static final Color TEXT = new Color(226, 232, 240);
        private static final Color MUTED_TEXT = new Color(148, 163, 184);

        private final List<Point> snake = new ArrayList<>();
        private final Random random = new Random();
        private final Timer timer = new Timer(TICK_MS, this);

        private Point food;
        private Direction direction;
        private Direction nextDirection;
        private int score;
        private boolean paused;
        private boolean gameOver;

        GamePanel() {
            setPreferredSize(new Dimension(COLUMNS * CELL_SIZE,
                    TOP_BAR_HEIGHT + ROWS * CELL_SIZE));
            setFocusable(true);
            addKeyListener(this);
            startNewGame();
        }

        private void startNewGame() {
            snake.clear();
            int startX = COLUMNS / 2;
            int startY = ROWS / 2;
            snake.add(new Point(startX, startY));
            snake.add(new Point(startX - 1, startY));
            snake.add(new Point(startX - 2, startY));
            direction = Direction.RIGHT;
            nextDirection = Direction.RIGHT;
            score = 0;
            paused = false;
            gameOver = false;
            placeFood();
            timer.start();
            requestFocusInWindow();
            repaint();
        }

        private void placeFood() {
            do {
                food = new Point(random.nextInt(COLUMNS), random.nextInt(ROWS));
            } while (snake.contains(food));
        }

        private void updateGame() {
            if (paused || gameOver) {
                return;
            }

            direction = nextDirection;
            Point head = snake.get(0);
            Point newHead = new Point(head.x + direction.dx, head.y + direction.dy);

            boolean ateFood = newHead.equals(food);
            boolean hitsWall = newHead.x < 0 || newHead.x >= COLUMNS
                    || newHead.y < 0 || newHead.y >= ROWS;

            // If the snake is not growing, its tail moves away this tick.
            // That means moving into the old tail square is allowed.
            int bodyEnd = ateFood ? snake.size() : snake.size() - 1;
            boolean hitsSelf = false;
            for (int i = 0; i < bodyEnd; i++) {
                if (snake.get(i).equals(newHead)) {
                    hitsSelf = true;
                    break;
                }
            }

            if (hitsWall || hitsSelf) {
                gameOver = true;
                timer.stop();
                repaint();
                return;
            }

            snake.add(0, newHead);
            if (ateFood) {
                score++;
                placeFood();
            } else {
                snake.remove(snake.size() - 1);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(BACKGROUND);
            g.fillRect(0, 0, getWidth(), getHeight());
            drawTopBar(g);
            drawBoard(g);

            if (paused || gameOver) {
                drawOverlay(g);
            }
            g.dispose();
        }

        private void drawTopBar(Graphics2D g) {
            g.setPaint(new GradientPaint(0, 0, new Color(30, 41, 59),
                    getWidth(), 0, new Color(15, 23, 42)));
            g.fillRect(0, 0, getWidth(), TOP_BAR_HEIGHT);

            g.setColor(TEXT);
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            g.drawString("SNAKE", 18, 33);

            g.setColor(MUTED_TEXT);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            String controls = "Arrows / WASD  •  P or Space: pause";
            FontMetrics metrics = g.getFontMetrics();
            g.drawString(controls, getWidth() - metrics.stringWidth(controls) - 18, 31);

            g.setColor(SNAKE_HEAD);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            String scoreText = "Score: " + score;
            g.drawString(scoreText, getWidth() / 2 - g.getFontMetrics().stringWidth(scoreText) / 2, 31);
        }

        private void drawBoard(Graphics2D g) {
            int boardY = TOP_BAR_HEIGHT;
            g.setColor(BOARD);
            g.fillRect(0, boardY, COLUMNS * CELL_SIZE, ROWS * CELL_SIZE);

            g.setColor(GRID);
            g.setStroke(new BasicStroke(1f));
            for (int x = 0; x <= COLUMNS; x++) {
                int pixelX = x * CELL_SIZE;
                g.drawLine(pixelX, boardY, pixelX, boardY + ROWS * CELL_SIZE);
            }
            for (int y = 0; y <= ROWS; y++) {
                int pixelY = boardY + y * CELL_SIZE;
                g.drawLine(0, pixelY, COLUMNS * CELL_SIZE, pixelY);
            }

            drawFood(g, boardY);
            for (int i = snake.size() - 1; i >= 0; i--) {
                drawSnakeSegment(g, snake.get(i), boardY, i == 0);
            }
        }

        private void drawFood(Graphics2D g, int boardY) {
            int x = food.x * CELL_SIZE;
            int y = boardY + food.y * CELL_SIZE;
            g.setColor(new Color(251, 113, 133, 55));
            g.fillOval(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
            g.setColor(FOOD);
            g.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
        }

        private void drawSnakeSegment(Graphics2D g, Point segment, int boardY,
                                      boolean head) {
            int padding = head ? 2 : 3;
            int x = segment.x * CELL_SIZE + padding;
            int y = boardY + segment.y * CELL_SIZE + padding;
            int size = CELL_SIZE - padding * 2;
            g.setColor(head ? SNAKE_HEAD : SNAKE_BODY);
            g.fillRoundRect(x, y, size, size, 8, 8);

            if (head) {
                g.setColor(new Color(20, 83, 45));
                drawEyes(g, x, y, size);
            }
        }

        private void drawEyes(Graphics2D g, int x, int y, int size) {
            int eyeSize = 4;
            int firstX;
            int secondX;
            int firstY;
            int secondY;

            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                firstX = x + size / 2 - eyeSize / 2;
                secondX = firstX;
                firstY = y + 5;
                secondY = y + size - 9;
                if (direction == Direction.LEFT) {
                    firstX = x + 4;
                    secondX = firstX;
                } else {
                    firstX = x + size - 8;
                    secondX = firstX;
                }
            } else {
                firstY = y + size / 2 - eyeSize / 2;
                secondY = firstY;
                firstX = x + 5;
                secondX = x + size - 9;
                if (direction == Direction.UP) {
                    firstY = y + 4;
                    secondY = firstY;
                } else {
                    firstY = y + size - 8;
                    secondY = firstY;
                }
            }

            g.fillOval(firstX, firstY, eyeSize, eyeSize);
            g.fillOval(secondX, secondY, eyeSize, eyeSize);
        }

        private void drawOverlay(Graphics2D g) {
            int boardY = TOP_BAR_HEIGHT;
            int boardHeight = ROWS * CELL_SIZE;
            g.setColor(new Color(2, 6, 23, 175));
            g.fillRect(0, boardY, getWidth(), boardHeight);

            String title = gameOver ? "GAME OVER" : "PAUSED";
            String subtitle = gameOver
                    ? "Press R to play again"
                    : "Press P or Space to resume";

            g.setColor(TEXT);
            g.setFont(new Font("SansSerif", Font.BOLD, 38));
            drawCentered(g, title, boardY + boardHeight / 2 - 8);

            g.setColor(MUTED_TEXT);
            g.setFont(new Font("SansSerif", Font.PLAIN, 17));
            drawCentered(g, subtitle, boardY + boardHeight / 2 + 27);
        }

        private void drawCentered(Graphics2D g, String text, int y) {
            FontMetrics metrics = g.getFontMetrics();
            g.drawString(text, (getWidth() - metrics.stringWidth(text)) / 2, y);
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            updateGame();
        }

        @Override
        public void keyPressed(KeyEvent event) {
            int key = event.getKeyCode();
            if (key == KeyEvent.VK_R && gameOver) {
                startNewGame();
                return;
            }
            if (key == KeyEvent.VK_P || key == KeyEvent.VK_SPACE) {
                if (!gameOver) {
                    paused = !paused;
                    repaint();
                }
                return;
            }

            Direction requested = Direction.fromKey(key);
            if (requested != null && requested != direction.opposite()) {
                nextDirection = requested;
            }
        }

        @Override
        public void keyTyped(KeyEvent event) {
            // Not used.
        }

        @Override
        public void keyReleased(KeyEvent event) {
            // Not used.
        }
    }

    private enum Direction {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        Direction opposite() {
            switch (this) {
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
                case LEFT:
                    return RIGHT;
                default:
                    return LEFT;
            }
        }

        static Direction fromKey(int key) {
            switch (key) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    return UP;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    return DOWN;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    return LEFT;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    return RIGHT;
                default:
                    return null;
            }
        }
    }
}
