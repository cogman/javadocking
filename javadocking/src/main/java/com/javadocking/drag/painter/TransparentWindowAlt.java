package com.javadocking.drag.painter;

import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * This is a window that has the screen as background.
 * The image of the screen is made when {@link #captureScreen()} is called.
 * The window can be repainted by calliong {@link #doRepaint()}.
 *
 * @author Heidi Rakels.
 */
class TransparentWindowAlt extends Window {

	private static BufferCapabilities bufCap;
	private static BufferStrategy strategy;

	static {
		// Determine if page flipping is supported
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		bufCap = gc.getBufferCapabilities();
		boolean page = bufCap.isPageFlipping();

		if (page) {
			System.out.println("Page flipping");
		} else {
			System.out.println("no page flipping");
			// Page flipping is not supported
		}

	}

	//private static BufferCapabilities.FlipContents flipContents;
	int frames = 0;
	/**
	 * The image of the screen.
	 */
	private Image screenImage;
	/**
	 * The painter that paints the content on the window.
	 */
	private RectanglePainter rectanglePainter = new DefaultRectanglePainter();
	// Constructors.

	/**
	 * Constructs a transparent window.
	 *
	 * @param    rectanglePainter    The painter that paints the content on the window.
	 */
	public TransparentWindowAlt(RectanglePainter rectanglePainter, Window owner) {
		super(owner);
		this.rectanglePainter = rectanglePainter;
//		System.out.println("isDisplayable = " + isDisplayable());
//		System.out.println();
//		init(owner);
//
//		System.out.println("isDisplayable = " + isDisplayable());

	}

	// Overwritten methods.

	public void paint(Graphics graphics) {

//		 this component must be displayable before its
		// paint method is called for the first time
		if (strategy == null) {
			init();
		}

		Graphics g = strategy.getDrawGraphics();

//        if (!flipContents.equals(BufferCapabilities.FlipContents.BACKGROUND)) {
//            // Clear background
//            g.setColor(Color.white);
//            g.fillRect(0, 0, getWidth(), getHeight());
//
//       }

		g.drawImage(screenImage, 0, 0, getWidth(), getHeight(), getX(), getY(), getX() + getWidth(), getY() + getHeight(), null);
		rectanglePainter.paintRectangle(g, 0, 0, getWidth(), getHeight());
/*			
		for(i=50;i<500;i++){
			for(j=50;j<500;j++){
		 		//g.drawString(s,i,j);

				//g.setColor(Color.red);
				g.drawLine(i,j,i+15,j+20);
			}
		}
*/
//		g.drawImage(offscreen,0,0,null); 

		// Done drawing
		g.dispose();

		// Flip the back buffer to the screen
		strategy.show();


//		if (windowImage == null) 
//		{
//			windowImage = createImage(getWidth(), getHeight());
//			windowImageGraphics = windowImage.getGraphics();
//		}
//		windowImageGraphics.drawImage(screenImage, 0, 0, getWidth(), getHeight(), getX(), getY(), getX() + getWidth(), getY() + getHeight(), null);
//
//		rectanglePainter.paintRectangle(windowImageGraphics, 0, 0, getWidth(), getHeight());
//		graphics.drawImage(windowImage, 0, 0, null);

	}

	// Public methods.

	/**
	 * Repaints the window.
	 */
	public void doRepaint() {

		Graphics g = getGraphics();
		paint(g);

	}

	/**
	 * Creates an image of the screen.
	 */
	public void captureScreen() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			Robot r = new Robot();
			Rectangle rect = new Rectangle(0, 0, d.width, d.height);
			screenImage = r.createScreenCapture(rect);
		} catch (AWTException awe) {
			System.out.println("Robot exception.");
		}
	}

	private void init() {
		int numBuffers = 2;  // Includes front buffer
		createBufferStrategy(numBuffers);

		// Determine the state of a back buffer after it has been displayed on the screen.
		// This information is used to optimize performance. For example, if your application
		// needs to initialize a back buffer with a background color, there is
		// no need to do so if the flip contents is BACKGROUND.
		strategy = getBufferStrategy();
		bufCap = strategy.getCapabilities();
//        flipContents = bufCap.getFlipContents();
//        if (flipContents.equals(BufferCapabilities.FlipContents.UNDEFINED)) {
//            // The contents is unknown after a flip
//        } else if (flipContents.equals(BufferCapabilities.FlipContents.BACKGROUND)) {
//            // The contents cleared to the component's background color after a flip
//        } else if (flipContents.equals(BufferCapabilities.FlipContents.PRIOR)) {
//            // The contents is the contents of the front buffer just before the flip
//        } else if (flipContents.equals(BufferCapabilities.FlipContents.COPIED)) {
//            // The contents is identical to the contents just pushed to the
//            // front buffer after a flip
//        }
	}
}
