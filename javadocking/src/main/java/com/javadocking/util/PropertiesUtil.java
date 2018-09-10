package com.javadocking.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This class contains a collection of static utility methods for creating, retrieving, saving and loading properties.
 *
 * @author Heidi Rakels.
 */
public class PropertiesUtil {

	// Static fields.

	/**
	 * The string representation for the boolean TRUE value.
	 */
	public static final String TRUE = "true";
	/**
	 * The string representation for the boolean FALSE value.
	 */
	public static final String FALSE = "false";
	/**
	 * The char ','.
	 */
	private static final char COMMA = ',';

	// Public static methods.

	private PropertiesUtil() {
	}

	/**
	 * Reads the file with the given name into a java.util.Properties object.
	 *
	 * @param sourceName The name of the source file for the properties.
	 * @return The properties that are retrieved from the source file.
	 * @throws IOException If creating the input stream, reading the property list from the input stream
	 *                     or closing the input stream throws an IOException.
	 */
	@NotNull
	public static Properties loadProperties(@NotNull String sourceName) throws IOException {

		Properties result = new Properties();
		FileInputStream inputStream = new FileInputStream(sourceName);
		result.load(inputStream);
		inputStream.close();
		return result;

	}

	/**
	 * Saves the given java.util.Properties object in a destination file with the given name.
	 *
	 * @param properties      The properties that have to be saved in the destination file.
	 * @param destinationName The name of the destination file for the properties.
	 * @param comment         The saved file will start with the given text as comment.
	 * @throws NullPointerException If the given properties are null.
	 * @throws IOException          If creating the output stream, writing this property list
	 *                              to the output stream or closing the output stream throws an IOException.
	 */
	public static void saveProperties(@Nullable Properties properties, @NotNull String destinationName, String comment) throws IOException {

		// Check that the given properties are not null.
		if (properties == null) {
			throw new NullPointerException("Properties null.");
		}

		FileOutputStream outputStream = new FileOutputStream(destinationName);
		properties.store(outputStream, comment);
		outputStream.close();

	}

	/**
	 * Gets the boolean property with the given name. If the property is not found, the default value is returned.
	 *
	 * @param properties   The given properties where the property is searched.
	 * @param name         The name of the property.
	 * @param defaultValue The default value of the property.
	 * @return The boolean property with the given name.
	 * If the property is not found, the default value is returned.
	 */
	public static boolean getBoolean(Properties properties, String name, boolean defaultValue) {

		// Get the property from the given properties list.
		String stringValue = properties.getProperty(name);

		// Return the default value if the value is invalid.
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return defaultValue;
		}

		// Trim the string value.
		stringValue = stringValue.trim();

		// Return true if the string starts with 'true'.
		return stringValue.startsWith(TRUE);

	}

	/**
	 * Adds the boolean property with the given name and value to the given properties.
	 *
	 * @param properties The given properties to which the property is added.
	 * @param name       The name of the property.
	 * @param value      The default value of the property.
	 */
	public static void setBoolean(Properties properties, String name, boolean value) {
		properties.put(name, (Boolean.valueOf(value)).toString());
	}

	/**
	 * Gets the string property with the given name. If the property is not found, the default value is returned.
	 *
	 * @param properties   The given properties where the property is searched.
	 * @param name         The name of the property.
	 * @param defaultValue The default value of the property.
	 * @return The string property with the given name.
	 * If the property is not found, the default value is returned.
	 */
	public static String getString(Properties properties, String name, String defaultValue) {

		// Get the property from the given properties list.
		String stringValue = properties.getProperty(name);

		// Return the default value if the value is invalid.
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return defaultValue;
		}

		return stringValue;

	}

	/**
	 * Adds the string property with the given name and value to the given properties.
	 *
	 * @param properties The given properties to which the property is added.
	 * @param name       The name of the property.
	 * @param value      The default value of the property.
	 */
	public static void setString(@NotNull Properties properties, String name, @Nullable String value) {

		if (value != null) {
			properties.put(name, value);
		}

	}

	/**
	 * Gets the integer property with the given name. If the property is not found, the default value is returned.
	 *
	 * @param properties   The given properties where the property is searched.
	 * @param name         The name of the property.
	 * @param defaultValue The default value of the property.
	 * @return The integer property with the given name.
	 * If the property is not found, the default value is returned.
	 */
	public static int getInteger(Properties properties, String name, int defaultValue) {

		// Get the property from the given properties list.
		String stringValue = properties.getProperty(name);

		// Return the default value if the value is invalid.
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return defaultValue;
		}

		try {
			// Trim the string value.
			stringValue = stringValue.trim();

			// Try to parse the string to an integer.
			return Integer.parseInt(stringValue);
		} catch (NumberFormatException numberFormatException) {
			return defaultValue;
		}

	}

	/**
	 * Adds the integer property with the given name and value to the given properties.
	 *
	 * @param properties The given properties to which the property is added.
	 * @param name       The name of the property.
	 * @param value      The default value of the property.
	 */
	public static void setInteger(Properties properties, String name, int value) {
		properties.put(name, Integer.toString(value));
	}

	/**
	 * Gets the double property with the given name. If the property is not found, the default value is returned.
	 *
	 * @param properties   The given properties where the property is searched.
	 * @param name         The name of the property.
	 * @param defaultValue The default value of the property.
	 * @return The double property with the given name.
	 * If the property is not found, the default value is returned.
	 */
	public static double getDouble(Properties properties, String name, double defaultValue) {

		// Get the property from the given properties list.
		String stringValue = properties.getProperty(name);

		// Return the default value if the value is invalid.
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return defaultValue;
		}

		try {
			// Trim the string value.
			stringValue = stringValue.trim();

			// Try to parse the string to an double.
			return Double.parseDouble(stringValue);
		} catch (NumberFormatException numberFormatException) {
			return defaultValue;
		}

	}

	/**
	 * Adds the double property with the given name and value to the given properties.
	 *
	 * @param properties The given properties to which the property is added.
	 * @param name       The name of the property.
	 * @param value      The default value of the property.
	 */
	public static void setDouble(Properties properties, String name, double value) {
		properties.put(name, (new Double(value)).toString());
	}

	/**
	 * Gets the color property with the given name. If the property is not found, the default value is returned.
	 *
	 * @param properties   The given properties where the property is searched.
	 * @param name         The name of the property.
	 * @param defaultValue The default value of the property.
	 * @return The color property with the given name.
	 * If the property is not found, the default value is returned.
	 */
	public static Color getColor(Properties properties, String name, Color defaultValue) {

		// Get the property from the given properties list.
		String stringValue = properties.getProperty(name);

		// Return the default value if the value is invalid.
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return defaultValue;
		}

		try {
			// Trim the string value.
			stringValue = stringValue.trim();

			// Get red, green and blue strings.
			StringTokenizer tokenizer = new StringTokenizer(stringValue, "[{(,/)}]");
			String red = tokenizer.nextToken();
			String green = tokenizer.nextToken();
			String blue = tokenizer.nextToken();

			// Parse the strings to integers.
			int redValue = Integer.parseInt(red);
			int greenValue = Integer.parseInt(green);
			int blueValue = Integer.parseInt(blue);

			// Do we have an alfa?
			if (tokenizer.hasMoreTokens()) {
				// Get the alfa string and parse it to an integer value.
				String alfa = tokenizer.nextToken();
				int alfaValue = Integer.parseInt(alfa);

				// Try to make the color with an alfa.
				return new Color(redValue, greenValue, blueValue, alfaValue);
			}

			// Try to make the color without alfa.
			return new Color(redValue, greenValue, blueValue);
		} catch (@NotNull NoSuchElementException | IllegalArgumentException noSuchElementException) {
			// Exception in the StringTokenizer.
			return defaultValue;
		}

	}

	/**
	 * Adds the color property with the given name and value to the given properties.
	 *
	 * @param properties The given properties to which the property is added.
	 * @param name       The name of the property.
	 * @param value      The default value of the property.
	 */
	public static void setColor(@NotNull Properties properties, String name, @Nullable Color value) {

		// Check if the color is not null.
		if (value != null) {
			// Create the string for the color.
			String colorString = "(" +
					value.getRed() + COMMA +
					value.getGreen() + COMMA +
					value.getBlue() + COMMA +
					value.getAlpha() +
					")";

			// Add the property.
			properties.put(name, colorString);
		}

	}

	/**
	 * Gets the string array property with the given name. If the property is not found, the default value is returned.
	 *
	 * @param properties   The given properties where the property is searched.
	 * @param name         The name of the property.
	 * @param defaultValue The default value of the property.
	 * @return The string array property with the given name.
	 * If the property is not found, the default value is returned.
	 */
	public static String[] getStringArray(Properties properties, String name, String[] defaultValue) {

		// Get the property from the given properties list.
		String stringValue = properties.getProperty(name);

		// Return the default value if the value is invalid.
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return defaultValue;
		}


		// Trim the string value.
		stringValue = stringValue.trim();

		// Get the strings in a list.
		StringTokenizer tokenizer = new StringTokenizer(stringValue, "(,)");
		List stringList = new ArrayList();
		while (tokenizer.hasMoreElements()) {
			stringList.add(tokenizer.nextElement());
		}

		// Create an array of strings.
		String[] stringArray = new String[stringList.size()];
		for (int index = 0; index < stringArray.length; index++) {
			stringArray[index] = (String) stringList.get(index);
		}
		return stringArray;

	}

	/**
	 * Adds the string array property with the given name and value to the given properties.
	 *
	 * @param properties The given properties to which the property is added.
	 * @param name       The name of the property.
	 * @param value      The default value of the property.
	 */
	public static void setStringArray(@NotNull Properties properties, String name, @Nullable String[] value) {

		// Check if the array is not null or if the size is 0.
		if ((value != null) && (value.length > 0)) {
			// Create the string for the string array.
			StringBuilder totalString = new StringBuilder(value[0]);
			for (int index = 1; index < value.length; index++) {
				// Separate the strings with a comma.
				totalString.append(COMMA);

				// Add the next string.
				totalString.append(value[index]);
			}

			// Add the property and its value.
			properties.put(name, totalString.toString());
		}

	}

	/**
	 * Gets the integer array property with the given name. If the property is not found, the default value is returned.
	 *
	 * @param properties   The given properties where the property is searched.
	 * @param name         The name of the property.
	 * @param defaultValue The default value of the property.
	 * @return The integer array property with the given name.
	 * If the property is not found, the default value is returned.
	 */
	public static int[] getIntegerArray(Properties properties, String name, int[] defaultValue) {

		// Get the property from the given properties list.
		String stringValue = properties.getProperty(name);

		// Return the default value if the value is invalid.
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return defaultValue;
		}

		// Trim the string value.
		stringValue = stringValue.trim();

		// Get the strings of the integers in a list.
		StringTokenizer tokenizer = new StringTokenizer(stringValue, "[{(,/)}]");
		List intList = new ArrayList();
		while (tokenizer.hasMoreElements()) {
			intList.add(tokenizer.nextElement());
		}

		// Create an array of integers.
		int[] intArray = new int[intList.size()];
		for (int index = 0; index < intArray.length; index++) {
			try {
				intArray[index] = Integer.parseInt((String) intList.get(index));
			} catch (NumberFormatException numberFormatException) {
				return defaultValue;
			}
		}

		return intArray;

	}

	/**
	 * Adds the integer array property with the given name and value to the
	 * given properties.
	 *
	 * @param properties The given properties to which the property is added.
	 * @param name       The name of the property.
	 * @param value      The default value of the property.
	 */
	public static void setIntegerArray(@NotNull Properties properties, String name, @Nullable int[] value) {

		// Check if the array is not null or if the size is 0.
		if ((value != null) && (value.length > 0)) {
			// Create the string for the integer array.
			StringBuilder totalString = new StringBuilder();
			totalString.append(value[0]);
			for (int index = 1; index < value.length; index++) {
				// Separate the strings with a comma.
				totalString.append(COMMA);

				// Add the next integer.
				totalString.append(value[index]);
			}

			// Add the property and its value.
			properties.put(name, totalString.toString());
		}

	}

	/**
	 * Gets the double array property with the given name. If the property is not found, the default value is returned.
	 *
	 * @param properties   The given properties where the property is searched.
	 * @param name         The name of the property.
	 * @param defaultValue The default value of the property.
	 * @return The double array property with the given name.
	 * If the property is not found, the default value is returned.
	 */
	public static double[] getDoubleArray(Properties properties, String name, double[] defaultValue) {

		// Get the property from the given properties list.
		String stringValue = properties.getProperty(name);

		// Return the default value if the value is invalid.
		if ((stringValue == null) || (stringValue.length() == 0)) {
			return defaultValue;
		}

		// Trim the string value.
		stringValue = stringValue.trim();

		// Get the strings of the doubles in a list.
		StringTokenizer tokenizer = new StringTokenizer(stringValue, "[{(,/)}]");
		List doubleList = new ArrayList();
		while (tokenizer.hasMoreElements()) {
			doubleList.add(tokenizer.nextElement());
		}

		// Create an array of doubles.
		double[] doubleArray = new double[doubleList.size()];
		for (int index = 0; index < doubleArray.length; index++) {
			try {
				doubleArray[index] = Integer.parseInt((String) doubleList.get(index));
			} catch (NumberFormatException numberFormatException) {
				return defaultValue;
			}
		}
		return doubleArray;

	}

//	/**
//	 * Tests the static methods of this class.
//	 * 
//	 * @param 	arguments
//	 */
//	public static void main(String[] arguments)
//	{
//		// Test 1.
//		System.out.println("TEST 1");
//		Properties prop = new Properties();
//		prop.put("mycolor", "(0,0,0)");
//		prop.put("booltrue", "true");
//		prop.put("boolfalse", "false");
//		prop.put("mydouble", "0.1");
//		prop.put("myinteger", "1");
//		prop.put("myintegerarray", "1,2,3");
//		prop.put("mystringarray", "Ik,ben,blij");
//		
//		int[] myintegerarraydefault = {1000,2000};
//		String[] mystringarraydefault = {"Hallo","Haai"};
//		Color mycolor = getColor(prop, "mycolor", null);
//		boolean booltrue = getBoolean(prop, "booltrue", false);
//		boolean boolfalse = getBoolean(prop, "boolfalse", true);
//		double mydouble = getDouble(prop, "mydouble", 100000);
//		int myinteger = getInteger(prop, "myinteger", 100000);
//		int[] myintegerarray = getIntegerArray(prop, "myintegerarray", myintegerarraydefault);
//		String[] mystringarray = getStringArray(prop, "mystringarray", mystringarraydefault);
//		
//		System.out.println("mycolor " + mycolor.getRed() + " " + mycolor.getGreen() + " " + mycolor.getBlue());
//		System.out.println("booltrue " + booltrue);
//		System.out.println("boolfalse " + boolfalse);
//		System.out.println("mydouble " + mydouble);
//		System.out.println("myinteger " + myinteger);
//		System.out.println("myinteger array 0  " + myintegerarray[0]);
//		System.out.println("myinteger array 1  " + myintegerarray[1]);
//		System.out.println("myinteger array 2  " + myintegerarray[2]);
//		
//		System.out.println("TEST 2");
//		Color mynewcolor = new Color(0,0,0);
//		boolean boolnewtrue = true;
//		boolean boolnewfalse = false;
//		double mynewdouble = 0.1;
//		int mynewinteger = 1;
//		int[] mynewintegerarray = {1,2,3};
//		String[] mynewstringarray = {"Ik","ben","blij"};
//		System.out.println(mynewcolor.getRed()==mycolor.getRed());
//		System.out.println(mynewcolor.getGreen()==mycolor.getGreen());
//		System.out.println(boolnewtrue==booltrue);
//		System.out.println(boolnewfalse==boolfalse);
//		System.out.println(mynewdouble==mydouble);
//		System.out.println(mynewinteger==myinteger);
//		System.out.println(mynewintegerarray[0]==myintegerarray[0]);
//		System.out.println(mynewstringarray[0].equalsIgnoreCase(mystringarray[0]));
//		System.out.println(mystringarray[0]);
//
//	}

	// Private constructor.

	/**
	 * Adds the double array property with the given name and value to the given properties.
	 *
	 * @param properties The given properties to which the property is added.
	 * @param name       The name of the property.
	 * @param value      The default value of the property.
	 */
	public static void setDoubleArray(@NotNull Properties properties, String name, @Nullable double[] value) {

		// Check if the array is not null or if the size is 0.
		if ((value != null) && (value.length > 0)) {
			// Create the string for the double array.
			StringBuilder totalString = new StringBuilder("" + value[0]);
			for (int index = 1; index < value.length; index++) {
				// Separate the strings with a comma.
				totalString.append(COMMA);

				// Add the next double.
				totalString.append(value[index]);
			}

			// Add the property and its value.
			properties.put(name, totalString.toString());
		}

	}
}
