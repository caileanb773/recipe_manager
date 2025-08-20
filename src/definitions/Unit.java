package definitions;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

public enum Unit {

	/* ----- VOLUME ----- */

	// US Customary
	TSP, TBSP, FLOZ, CUP, CUPS, PINT, PINTS, QT, QTS, GAL, GALS,
	TEASPOON, TABLESPOON, FLUID_OUNCE, QUART, GALLON,
	TEASPOONS, TABLESPOONS, FLUID_OUNCES, QUARTS, GALLONS,
	
	// Metric
	ML, CL, L, 	// milliliter, centileter, liter
	MILLILITER, CENTILITER, LITER, LITRE, MILLILITERS, CENTILITERS, LITERS, LITRES,

	/* ----- WEIGHT ----- */

	// US Customary
	OZ, LB, LBS,
	OUNCE, OUNCES, POUND, POUNDS,

	// Metric
	MG, G, KG,
	MILLIGRAM, GRAM, KILOGRAM, MILLIGRAMS, GRAMS, KILOGRAMS,
	
	/* ----- OTHER ----- */

	PIECES, KNOBS, CLOVES, STICKS, GLUGS, INCHES, DASHES, PINCHES, DROPS,
	PIECE, KNOB, CLOVE, STICK, GLUG, WHOLE, INCH, CM, DASH, PINCH, DROP, PERCENT,
	NO_UNIT; // for situations where no unit is needed

}
