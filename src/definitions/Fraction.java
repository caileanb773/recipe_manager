package definitions;

import java.math.BigDecimal;
import java.math.BigInteger;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

public class Fraction {
	
	private int numerator;
	private int denominator;
	
	
	public Fraction(int num, int den) {
		if (den == 0) {
			System.err.println("Denominator set to 0 in new fraction.");
		}
		numerator = num;
		denominator = den;
		simplify();
	}
	
	public Fraction(int whole, int num, int den) {
		this.numerator = den * whole + num;
		this.denominator = den;
	}
	
	public Fraction(BigDecimal whole, int num, int den) {
		this.numerator = (whole.intValue()) * den + num;
		this.denominator = den;
	}
	
	// Constructor for BigDecimal
    public Fraction(BigDecimal decimal) {
        if (decimal == null) {
            throw new IllegalArgumentException("Input BigDecimal cannot be null");
        }

        // Handle zero
        if (decimal.compareTo(BigDecimal.ZERO) == 0) {
            this.numerator = 0;
            this.denominator = 1;
            return;
        }

        // Cap scale at 3 for recipes (e.g., 1.333 is fine, 1.275453 is not)
        int maxScale = 3;
        int scale = decimal.scale();
        if (scale > maxScale) {
            throw new IllegalArgumentException("Decimal precision too high for recipes: scale " + scale + " exceeds maximum " + maxScale);
        }

        // Handle negative scale (e.g., 2300 with scale -1)
        if (scale < 0) {
            decimal = decimal.setScale(0);
            scale = 0;
        }

        // Determine sign and work with absolute value
        boolean isNegative = decimal.compareTo(BigDecimal.ZERO) < 0;
        BigDecimal absDecimal = decimal.abs();

        // Convert to fraction using scale
        BigInteger numerator;
        BigInteger denominator = BigInteger.TEN.pow(scale); // 10^scale as denominator
        numerator = absDecimal.movePointRight(scale).toBigInteger(); // Multiply by 10^scale

        // Apply sign
        if (isNegative) {
            numerator = numerator.negate();
        }

        // Simplify fraction
        BigInteger gcd = numerator.gcd(denominator);
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);

        // Ensure denominator is positive
        if (denominator.signum() < 0) {
            numerator = numerator.negate();
            denominator = denominator.negate();
        }

        // Check for int overflow
        if (numerator.bitLength() > 31 || denominator.bitLength() > 31) {
            throw new ArithmeticException("Numerator or denominator exceeds int range after simplification");
        }

        this.numerator = numerator.intValue();
        this.denominator = denominator.intValue();
    }

    // Simplify the fraction using GCD
    private void simplify() {
        int gcd = gcd(Math.abs(numerator), Math.abs(denominator));
        numerator = numerator / gcd;
        denominator = denominator / gcd;
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
    }

    // Compute GCD using Euclidean algorithm
    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
	
	public Fraction multiply(int multiplier) {
		return new Fraction(this.numerator * multiplier, this.denominator);
	}
	
	public static Fraction parseFraction(String fracStr) throws NumberFormatException {		
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
	
	public static boolean isDecimal(String amt) {
		return (amt.indexOf('.') != -1);
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
