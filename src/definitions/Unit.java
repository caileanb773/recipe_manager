package definitions;

/*
 * Author: Cailean Bernard
 * Contents: 
 */

public enum Unit {

	/* ----- VOLUME ----- */

	// US Customary
	TSP, TBSP, FLOZ, CUP, PINT, QT, GAL, // teaspoon, tablespoon, fluid ounce
	TEASPOON, TABLESPOON, FLUID_OUNCE, QUART, GALLON,
	
	// Metric
	ML, CL, L, 	// milliliter, centileter, liter
	MILLILITER, CENTILITER, LITER, LITRE,

	/* ----- WEIGHT ----- */

	// US Customary
	OZ, LB,	// ounce, pound
	OUNCE, POUND,

	// Metric
	MG, G, KG, // milligram, gram, kilogram
	MILLIGRAM, GRAM, KILOGRAM,

	/* ----- OTHER ----- */

	PIECE, KNOB, CLOVE, STICK, GLUG, WHOLE, INCH, CM, DASH, PINCH, DROP, PERCENT,
	NO_UNIT; // for situations where no unit is needed

}
