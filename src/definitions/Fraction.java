package definitions;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

public class Fraction {
	
	private int numerator;
	private int denominator;
	
	
	public Fraction(int num, int den) {
		numerator = num;
		denominator = den;
	}
	
	public Fraction(int whole, int num, int den) {
		this.numerator = den * whole + num;
		this.denominator = den;
	}
	
	public Fraction multiply(int multiplier) {
		return new Fraction(this.numerator * multiplier, this.denominator);
	}
	
	public Fraction parseFraction(String fracStr) throws NumberFormatException {		
		String trimmed = fracStr.trim();
		
		// Mixed fraction like 2 1/2
		if (trimmed.matches("\\d+\\s+\\d+/\\d+")) {
			String[] parts = trimmed.split("\\s+");
			int wholeNum = Integer.parseInt(parts[0]);
			String[] fractional = parts[1].split("/");
			int numerator = Integer.parseInt(fractional[0]);
			int denominator = Integer.parseInt(fractional[1]);
			return new Fraction(wholeNum, numerator, denominator);
			// Simple fraction like 3/4
		} else if (trimmed.matches("\\d+/\\d+")) {
			String[] fractional = trimmed.split("/");
			int numerator = Integer.parseInt(fractional[0]);
			int denominator = Integer.parseInt(fractional[1]);
			return new Fraction(numerator, denominator);
		} else if (trimmed.matches("\\d+")) {
			int wholeNum = Integer.parseInt(trimmed);
			return new Fraction(wholeNum, 1);
		} else {
			throw new NumberFormatException("Invalid fractional format.");
		}
	}
	
	public static boolean isFraction(String amt) {
		return (amt.indexOf('/') != -1);
	}
	
	public int getNum() {
		return numerator;
	}
	
	public int getDen() {
		return denominator;
	}

	@Override
	public String toString() {
		int whole = numerator / denominator;
		int remainder = numerator % denominator;
		
		if (whole > 0 && remainder > 0) {
			return whole + " " + remainder + "/" + denominator;
		} else if (whole > 0) {
			return String.valueOf(whole);
		} else {
			return remainder + "/" + denominator;
		}
	}

}
