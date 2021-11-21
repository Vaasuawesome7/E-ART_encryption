package metrics;

import utilities.Utils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;

public class BarChart extends JPanel {

    private final double[] values;
    private final String[] labels;
    private final Color[] colors;
    private final String title;

    public BarChart(double[] values, String[] labels, Color[] colors, String title) {
        this.labels = labels;
        this.values = values;
        this.colors = colors;
        this.title = title;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (values == null || values.length == 0) {
            return;
        }

        double minValue = 0;
        double maxValue = 0;
        for (double value : values) {
            if (minValue > value) {
                minValue = value;
            }
            if (maxValue < value) {
                maxValue = value;
            }
        }

        Dimension dim = getSize();
        int panelWidth = dim.width;
        int panelHeight = dim.height;
        int barWidth = panelWidth / values.length;

        Font titleFont = new Font("Book Antiqua", Font.BOLD, 15);
        FontMetrics titleFontMetrics = g.getFontMetrics(titleFont);

        Font labelFont = new Font("Book Antiqua", Font.PLAIN, 14);
        FontMetrics labelFontMetrics = g.getFontMetrics(labelFont);

        int titleWidth = titleFontMetrics.stringWidth(title);
        int stringHeight = titleFontMetrics.getAscent();
        int stringWidth = (panelWidth - titleWidth) / 2;
        g.setFont(titleFont);
        g.drawString(title, stringWidth, stringHeight);

        int top = titleFontMetrics.getHeight();
        int bottom = labelFontMetrics.getHeight();
        if (maxValue == minValue) {
            return;
        }
        double scale = (panelHeight - top - bottom) / (maxValue - minValue);
        stringHeight = panelHeight - labelFontMetrics.getDescent();
        g.setFont(labelFont);
        for (int j = 0; j < values.length; j++) {
            int valueP = j * barWidth + 1;
            int valueQ = top;
            int height = (int) (values[j] * scale);
            if (values[j] >= 0) {
                valueQ += (int) ((maxValue - values[j]) * scale);
            } else {
                valueQ += (int) (maxValue * scale);
                height = -height;
            }

            g.setColor(colors[j % 5]);
            g.fillRect(valueP, valueQ, barWidth - 2, height);
            g.setColor(Color.black);
            g.drawRect(valueP, valueQ, barWidth - 2, height);

            int labelWidth = labelFontMetrics.stringWidth(labels[j]);
            stringWidth = j * barWidth + (barWidth - labelWidth) / 2;
            g.drawString(labels[j], stringWidth, stringHeight);
        }
    }

    private static double[] countCharacters(String c) {
        double[] asciiCount = new double[128];
        for (int i = 0; i < c.length(); i++) {
            if (c.charAt(i) < 128) asciiCount[c.charAt(i)]++;
        }
        return asciiCount;
    }

    private static String[] generateStringArray() {
        String[] array = new String[128];
        for (int i = 0; i < 128; i++) {
            String val = "" + (char) i;
            array[i] = val;
        }
        return array;
    }

    private static void plain(JFrame frame, String[] labels, Color[] colors) throws IOException {
        File plain = new File(Utils.CLIENT_FILES_LOCATION + "data.txt");
        BufferedReader br = new BufferedReader(new FileReader(plain));
        StringBuilder plainTextBuilder = new StringBuilder();
        String str;
        while ((str = br.readLine()) != null) {
            plainTextBuilder.append(str);
        }
        String plainText = plainTextBuilder.toString();
        double[] count = countCharacters(plainText);
        BarChart bc = new BarChart(count, labels, colors, "PLAIN");
        frame.add(bc);
    }

    private static void cipher(JFrame frame, String[] labels, Color[] colors) throws IOException {
        File cipher = new File(Utils.SERVER_FILES_LOCATION + "cipherText.txt");
        BufferedReader br = new BufferedReader(new FileReader(cipher));
        StringBuilder cipherBuilder = new StringBuilder();
        String str;
        while ((str = br.readLine()) != null) {
            cipherBuilder.append(str);
        }
        String cipherText = cipherBuilder.toString();
        double[] count = countCharacters(cipherText);
        BarChart bc = new BarChart(count, labels, colors, "CIPHER");
        frame.add(bc);
    }

    public static void main(String[] args) {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Bar Chart Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 300);

        String[] labels = generateStringArray();

        Color[] colors = new Color[]{
                Color.red,
                Color.orange,
                Color.yellow,
                Color.green,
                Color.blue
        };

        try {
            //plain(frame, labels, colors);
            cipher(frame, labels, colors);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        frame.setVisible(true);
    }
}