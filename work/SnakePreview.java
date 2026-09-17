import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SnakePreview {
    public static void main(String[] args) throws Exception {
        int cell = 24, cols = 30, rows = 24, top = 52;
        int width = cols * cell, height = top + rows * cell;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(15, 23, 42));
        g.fillRect(0, 0, width, height);
        g.setPaint(new GradientPaint(0, 0, new Color(30, 41, 59), width, 0, new Color(15, 23, 42)));
        g.fillRect(0, 0, width, top);
        g.setColor(new Color(226, 232, 240));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SNAKE", 18, 33);
        g.setColor(new Color(148, 163, 184));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("Arrows / WASD  •  P or Space: pause", 478, 31);
        g.setColor(new Color(74, 222, 128));
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Score: 7", 332, 31);

        g.setColor(new Color(30, 41, 59));
        g.fillRect(0, top, width, rows * cell);
        g.setColor(new Color(51, 65, 85));
        g.setStroke(new BasicStroke(1f));
        for (int x = 0; x <= cols; x++) g.drawLine(x * cell, top, x * cell, height);
        for (int y = 0; y <= rows; y++) g.drawLine(0, top + y * cell, width, top + y * cell);

        Point food = new Point(21, 8);
        g.setColor(new Color(251, 113, 133, 55));
        g.fillOval(food.x * cell + 2, top + food.y * cell + 2, cell - 4, cell - 4);
        g.setColor(new Color(251, 113, 133));
        g.fillOval(food.x * cell + 5, top + food.y * cell + 5, cell - 10, cell - 10);

        List<Point> snake = Arrays.asList(
                new Point(15, 12), new Point(14, 12), new Point(13, 12),
                new Point(12, 12), new Point(11, 12), new Point(11, 11),
                new Point(11, 10), new Point(12, 10));
        for (int i = snake.size() - 1; i >= 0; i--) {
            Point p = snake.get(i);
            int padding = i == 0 ? 2 : 3;
            int x = p.x * cell + padding, y = top + p.y * cell + padding;
            int size = cell - padding * 2;
            g.setColor(i == 0 ? new Color(74, 222, 128) : new Color(34, 197, 94));
            g.fillRoundRect(x, y, size, size, 8, 8);
            if (i == 0) {
                g.setColor(new Color(20, 83, 45));
                g.fillOval(x + size - 8, y + 5, 4, 4);
                g.fillOval(x + size - 8, y + size - 9, 4, 4);
            }
        }
        g.dispose();

        ImageIO.write(image, "png", new File("C:/Users/student/Documents/Codex/2026-09-17/build-x20/outputs/SnakeGamePreview.png"));
    }
}
