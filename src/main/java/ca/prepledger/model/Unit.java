package model;

/*
 * Author: Cailean Bernard
 * Contents: Unit definitions. (mostly) Comprehensive list of US Customary and
 * Metric measurements of both volume and weight. 
 */
public enum Unit {

	/* ----- VOLUME ----- */

	// US Customary (volume)
	TSP, TBSP, FLOZ, CUP, CUPS, PINT, PINTS, QT, QTS, GAL, GALS,
	TEASPOON, TABLESPOON, FLUID_OUNCE, QUART, GALLON,
	TEASPOONS, TABLESPOONS, FLUID_OUNCES, QUARTS, GALLONS,
	
	// Metric (volume)
	ML, CL, L, 	// milliliter, centileter, liter
	MILLILITER, CENTILITER, LITER, LITRE, MILLILITERS, CENTILITERS, LITERS, LITRES,

	/* ----- WEIGHT ----- */

	// US Customary (weight)
	OZ, LB, LBS,
	OUNCE, OUNCES, POUND, POUNDS,

	// Metric (weight)
	MG, G, KG,
	MILLIGRAM, GRAM, KILOGRAM, MILLIGRAMS, GRAMS, KILOGRAMS,
	
	/* ----- OTHER ----- */

	CAN, CANS, PIECES, KNOBS, CLOVES, STICKS, GLUGS, INCHES, DASHES, PINCHES, DROPS, BLOCK,
	PIECE, KNOB, CLOVE, STICK, GLUG, WHOLE, INCH, CM, DASH, PINCH, DROP, BLLOCKS,
	PERCENT, PART, PARTS, NO_UNIT; // for situations where no unit is needed

}
