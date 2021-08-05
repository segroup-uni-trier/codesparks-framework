/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class CodeSparksGraphics extends Graphics2D
{
    private final Graphics2D graphics2D;
    private final BufferedImage bi;

    public CodeSparksGraphics(final int width, final int height)
    {
        final GraphicsConfiguration defaultConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        this.bi = UIUtil.createImage(defaultConfiguration, width, height, BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);
        this.graphics2D = (Graphics2D) this.bi.getGraphics();
        final Composite composite = this.graphics2D.getComposite();
        this.graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
        this.graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.graphics2D.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        this.graphics2D.setComposite(composite);
    }

    public BufferedImage getBufferedImage()
    {
        return this.bi;
    }

    public JBLabel getLabel(final int width)
    {
        final BufferedImage subImage = bi.getSubimage(0, 0, width, bi.getHeight());
        final ImageIcon imageIcon = new ImageIcon(subImage);
        return new JBLabel(imageIcon);
    }

    public void fillRectangle(final Rectangle rectangle)
    {
        graphics2D.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public void fillRectangle(final Rectangle rectangle, final Color color)
    {
        final Color currentColor = graphics2D.getColor();
        graphics2D.setColor(color);
        graphics2D.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        graphics2D.setColor(currentColor);
    }

    public void drawRectangle(final Rectangle rectangle)
    {
        graphics2D.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public void drawRectangle(final Rectangle rectangle, final Color color)
    {
        final Color currentColor = graphics2D.getColor();
        graphics2D.setColor(color);
        graphics2D.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        graphics2D.setColor(currentColor);
    }

    public void drawString(final String str, final int x, final int y, final Color color)
    {
        final Color currentColor = graphics2D.getColor();
        graphics2D.setColor(color);
        graphics2D.drawString(str, x, y);
        graphics2D.setColor(currentColor);
    }

    public int stringWidth(final String str)
    {
        return graphics2D.getFontMetrics().stringWidth(str);
    }

    public int fontHeight()
    {
        return graphics2D.getFontMetrics().getHeight();
    }

    public void setDefaultColor()
    {
        graphics2D.setColor(VisConstants.BORDER_COLOR);
    }

    @Override
    public void draw(final Shape s)
    {
        graphics2D.draw(s);
    }

    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs)
    {
        return graphics2D.drawImage(img, xform, obs);
    }

    @Override
    public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y)
    {
        graphics2D.drawImage(img, op, x, y);
    }

    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform)
    {
        graphics2D.drawRenderedImage(img, xform);
    }

    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform)
    {
        graphics2D.drawRenderableImage(img, xform);
    }

    @Override
    public void drawString(final String str, final int x, final int y)
    {
        graphics2D.drawString(str, x, y);
    }

    @Override
    public void drawString(final String str, final float x, final float y)
    {
        graphics2D.drawString(str, x, y);
    }

    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y)
    {
        graphics2D.drawString(iterator, x, y);
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer)
    {
        return graphics2D.drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final ImageObserver observer)
    {
        return graphics2D.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor, final ImageObserver observer)
    {
        return graphics2D.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final Color bgcolor, final ImageObserver observer)
    {
        return graphics2D.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2,
                             final int sy2, final ImageObserver observer)
    {
        return graphics2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2,
                             final int sy2, final Color bgcolor, final ImageObserver observer)
    {
        return graphics2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public void dispose()
    {
        graphics2D.dispose();
    }

    @Override
    public void drawString(final AttributedCharacterIterator iterator, final float x, final float y)
    {
        graphics2D.drawString(iterator, x, y);
    }

    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y)
    {
        graphics2D.drawGlyphVector(g, x, y);
    }

    @Override
    public void fill(final Shape s)
    {
        graphics2D.fill(s);
    }

    @Override
    public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke)
    {
        return graphics2D.hit(rect, s, onStroke);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration()
    {
        return graphics2D.getDeviceConfiguration();
    }

    @Override
    public void setComposite(final Composite comp)
    {
        graphics2D.setComposite(comp);
    }

    @Override
    public void setPaint(final Paint paint)
    {
        graphics2D.setPaint(paint);
    }

    @Override
    public void setStroke(final Stroke s)
    {
        graphics2D.setStroke(s);
    }

    @Override
    public void setRenderingHint(final RenderingHints.Key hintKey, final Object hintValue)
    {
        graphics2D.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(final RenderingHints.Key hintKey)
    {
        return graphics2D.getRenderingHint(hintKey);
    }

    @Override
    public void setRenderingHints(final Map<?, ?> hints)
    {
        graphics2D.setRenderingHints(hints);
    }

    @Override
    public void addRenderingHints(final Map<?, ?> hints)
    {
        graphics2D.addRenderingHints(hints);
    }

    @Override
    public RenderingHints getRenderingHints()
    {
        return graphics2D.getRenderingHints();
    }

    @Override
    public Graphics create()
    {
        return graphics2D.create();
    }

    @Override
    public void translate(final int x, final int y)
    {
        graphics2D.translate(x, y);
    }

    @Override
    public Color getColor()
    {
        return graphics2D.getColor();
    }

    @Override
    public void setColor(final Color c)
    {
        graphics2D.setColor(c);
    }

    @Override
    public void setPaintMode()
    {
        graphics2D.setPaintMode();
    }

    @Override
    public void setXORMode(final Color c1)
    {
        graphics2D.setXORMode(c1);
    }

    @Override
    public Font getFont()
    {
        return graphics2D.getFont();
    }

    @Override
    public void setFont(final Font font)
    {
        graphics2D.setFont(font);
    }

    @Override
    public FontMetrics getFontMetrics(final Font f)
    {
        return graphics2D.getFontMetrics(f);
    }

    @Override
    public Rectangle getClipBounds()
    {
        return graphics2D.getClipBounds();
    }

    @Override
    public void clipRect(final int x, final int y, final int width, final int height)
    {
        graphics2D.clipRect(x, y, width, height);
    }

    @Override
    public void setClip(final int x, final int y, final int width, final int height)
    {
        graphics2D.setClip(x, y, width, height);
    }

    @Override
    public Shape getClip()
    {
        return graphics2D.getClip();
    }

    @Override
    public void setClip(final Shape clip)
    {
        graphics2D.setClip(clip);
    }

    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy)
    {
        graphics2D.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2)
    {
        graphics2D.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height)
    {
        graphics2D.fillRect(x, y, width, height);
    }

    @Override
    public void clearRect(final int x, final int y, final int width, final int height)
    {
        graphics2D.clearRect(x, y, width, height);
    }

    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight)
    {
        graphics2D.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight)
    {
        graphics2D.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawOval(final int x, final int y, final int width, final int height)
    {
        graphics2D.drawOval(x, y, width, height);
    }

    @Override
    public void fillOval(final int x, final int y, final int width, final int height)
    {
        graphics2D.fillOval(x, y, width, height);
    }

    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle)
    {
        graphics2D.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle)
    {
        graphics2D.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints)
    {
        graphics2D.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints)
    {
        graphics2D.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints)
    {
        graphics2D.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void translate(final double tx, final double ty)
    {
        graphics2D.translate(tx, ty);
    }

    @Override
    public void rotate(final double theta)
    {
        graphics2D.rotate(theta);
    }

    @Override
    public void rotate(final double theta, final double x, final double y)
    {
        graphics2D.rotate(theta, x, y);
    }

    @Override
    public void scale(final double sx, final double sy)
    {
        graphics2D.scale(sx, sy);
    }

    @Override
    public void shear(final double shx, final double shy)
    {
        graphics2D.shear(shx, shy);
    }

    @Override
    public void transform(final AffineTransform Tx)
    {
        graphics2D.transform(Tx);
    }

    @Override
    public void setTransform(final AffineTransform Tx)
    {
        graphics2D.setTransform(Tx);
    }

    @Override
    public AffineTransform getTransform()
    {
        return graphics2D.getTransform();
    }

    @Override
    public Paint getPaint()
    {
        return graphics2D.getPaint();
    }

    @Override
    public Composite getComposite()
    {
        return graphics2D.getComposite();
    }

    @Override
    public void setBackground(final Color color)
    {
        graphics2D.setBackground(color);
    }

    @Override
    public Color getBackground()
    {
        return graphics2D.getBackground();
    }

    @Override
    public Stroke getStroke()
    {
        return graphics2D.getStroke();
    }

    @Override
    public void clip(final Shape s)
    {
        graphics2D.clip(s);
    }

    @Override
    public FontRenderContext getFontRenderContext()
    {
        return graphics2D.getFontRenderContext();
    }

}
