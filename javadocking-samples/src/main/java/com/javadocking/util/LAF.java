package com.javadocking.util;

import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

/**
 * A look and feel for an application.
 *
 * @author Heidi Rakels.
 */
public class LAF {

	public static final String THEME_GOLD = "gold";
	public static final String THEME_OCEAN = "blue";
	public static final String THEME_DEAULT = "default";

	// Fields.

	private String className;
	private String title;
	private boolean supported = false;
	private boolean selected = false;
	private LookAndFeel laf;
	private String theme;

	// Constructors.

	public LAF(String title, String className, String themeString) {
		this.title = title;
		this.className = className;
		this.theme = themeString;

		// Is this look and feel supported?
		try {
			Class clazz = Class.forName(className);
			laf = (LookAndFeel) (clazz.newInstance());
			supported = laf.isSupportedLookAndFeel();
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	// Getters / Setters.

	public static void setTheme(LookAndFeel laf, @NotNull String themeString) {
		if (laf instanceof MetalLookAndFeel) {
			MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
		}
		if (laf instanceof NimRODLookAndFeel) {
			if (themeString.equals(THEME_OCEAN)) {
				NimRODTheme nimrodTheme = new NimRODTheme();
				nimrodTheme.setPrimary1(new Color(92, 170, 255));
				nimrodTheme.setPrimary2(new Color(123, 191, 255));
				nimrodTheme.setPrimary3(new Color(154, 212, 255));

				nimrodTheme.setSecondary1(new Color(220, 220, 220));
				nimrodTheme.setSecondary2(new Color(230, 230, 230));
				nimrodTheme.setSecondary3(new Color(240, 240, 240));
				nimrodTheme.setWhite(new Color(255, 255, 255));
				nimrodTheme.setBlack(Color.black);
				nimrodTheme.setMenuOpacity(195);
				nimrodTheme.setFrameOpacity(180);

				NimRODLookAndFeel.setCurrentTheme(nimrodTheme);
			} else if (themeString.equals(THEME_GOLD)) {
				NimRODTheme nimrodTheme = new NimRODTheme();
				nimrodTheme.setPrimary1(new Color(235, 174, 0));
				nimrodTheme.setPrimary2(new Color(245, 194, 0));
				nimrodTheme.setPrimary3(new Color(255, 214, 0));

				nimrodTheme.setSecondary1(new Color(220, 220, 220));
				nimrodTheme.setSecondary2(new Color(230, 230, 230));
				nimrodTheme.setSecondary3(new Color(240, 240, 240));
				nimrodTheme.setWhite(new Color(255, 255, 255));
				nimrodTheme.setBlack(Color.black);
				nimrodTheme.setMenuOpacity(195);
				nimrodTheme.setFrameOpacity(180);

				NimRODLookAndFeel.setCurrentTheme(nimrodTheme);
			}
		}

	}

	public String getClassName() {
		return className;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSupported() {
		return supported;
	}

	public String getTitle() {
		return title;
	}

	public LookAndFeel getLaf() {
		return laf;
	}

	public String getTheme() {
		return theme;
	}
}
