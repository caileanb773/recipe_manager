# Macromise Recipe manager

![Login Screen](resources/img/screenshot_login.png)
![Recipe Management Screen](resources/img/screenshot_recipescreen_dark.png)

# What this app is for

At its' core, Macromise [macro-MEEZ] is a simple recipe manager that allows multiple users to access the same database of recipes. Administrators can use the desktop application to manage a master list of recipes, while users on the (upcoming) mobile application who are connected to the same restaurant database can suggest new recipes or edits to existing ones. The administrator can then either approve or reject these changes.

In this way, multiple members of staff can have the shared responsibility of creating new recipes and maintaining the accuracy of old ones, instead of the executive and sous chef being solely responsible for recipe upkeep.

# Usage

## Installation (planned)

Macromise will be available via installer which includes JRE dependencies, or through a lightweight .jar for advanced users who might already have a JRE installed.

For the installer, run the MacromiseInstaller.exe and select a directory. The installer will install the JRE libraries it depends on in addition to the application itself. The installer will optionally create a desktop shortcut/start menu directory if desired.

For the standalone .jar, ensure you have the correct JRE installed and extract the .zip wherever you would like the application to go. You will have to create desktop shortcuts/start menu directories manually, if desired.

## Getting Started

Begin by creating an account through the desktop application. Recipes are tied to your account, and each account has a database tied to it. 

Once logged in, the list of existing recipes will be displayed on the left, while information on any selected recipe will be displayed on the right. 

Buttons for creating, removing, and editing recipes are situated on the bottom left of the screen. 

Recipes can be filtered by name or by their associated tags in the "filter" section above the recipe selection list. Recipes may also be scaled with the scale spinner above the recipe description section.

### Adding New Recipes

To add a new recipe, click the "Add" button below the recipe selection pane. A new dialog will be created that has input areas for title, ingredients, instructions, and tags.

Give the recipe a title, a list of ingredients, and some instructions. You may opt to include tags for your recipes, but they are optional. Helpful uses for tags allow you and your staff to search for recipes by the section(s) that they are associated with; for example: *Garde Manger, Saucier, Entremetier.*

Recipe ingredients must be input in a particular fashion to allow the application to properly parse ingredients and allow for scaling. The proper way to add an ingredient is:

```
[amount] [unit] [ingredient name]

1 1/2 cup flour         // Fractional amounts are fine
0.5 cup sugar           // As are decimal amounts
1 stick butter          // Special units exist for common ingredients (clove, stick, knob)
salt to taste           // ERROR; Program will reject this recipe as there is no unit
```

As you can see from the example above, it may seem like the application is overly particular. However, the only cases where the recipe will be rejected is if one or more ingredients are in the incorrect format, with the most common cause for rejection being **salt**, or **salt, to taste** being at the bottom of most recipes.

In the vast majority of professional kitchens, seasoning with salt is an unwritten step in every recipe, is highly subjective, and is reliant on many factors including the cook's palate. If there is no prescribed "amount" of salt for the associated recipe, just add "season with salt" to the instructions.

# Features

## Recipe Scaling

Recipes can be scaled up incrementally for batching out recipes.

## Themes

Choose from either light mode (default) or dark mode to reduce eye strain.

## Detach Recipes

When viewing recipes, you may view them in a separate window by clicking the recipe and then clicking "detach recipe". This is useful for comparing two recipes, or for when it is necessary to reference an old recipe when creating a new one.

## Auto-Backup

In the options menu item, you can tick the *auto backup* checkbox. This feature will automatically export all recipes stored in the database to a .rcp file on your hard drive; when active, this feature will automatically save all recipes locally if internet were to momentarily be unavailable.

## Import/Export

Recipes can be manually exported to and imported from files in the proprietary .rcp format. This is helpful for migrating recipes from one database to another, or merging the contents of multiple databases into one.

## Languages

Macromise is available in English and French. Additional languages may be added in the future.

# Upcoming Features

## Mobile Application

The mobile application will allow staff who are invited to your restaurant group to view, suggest edits to, and suggest creation of new recipes.

A single Macromise Administrator account will not only have its associated recipes, but also a list of staff who have these permissions.

An incoming recipe creation/edit request from a staff will take the form of a notification in the Administrator's notification center. These requests can be quickly viewed and accepted or rejected with a single click. Upon accepting a change, the database will update itself to reflect the changes.

## Persistent Database

Always-on database that allows your recipes to be accessible when you might not be. Staff can still access the database and perform other functions of the mobile application at any time.

## User management

The Administrator will have the ability to send invites to new staff who will then enter their information and register for an account. Staff who no longer work at the establishment can have their permissions revoked.

## Themes / Accessibility

Multiple UI colour themes, including themes for colour-blindness. Planned accessibility features include adjustable font sizes, text-to-speech, voice-to-text, and others.

# FAQ

## I can't add a new recipe.

The most likely case is that one or more ingredients are not in the correct format. Ensure each ingredient is in the format:

**[amount] [unit] [name]**

If your ingredient *does not need a unit*, for example "1 orange, juiced" adopt this notation:

```
1 whole orange, juiced
5 whole cinnamon sticks
```

## What ingredient amounts are supported?

Ingredient amounts can be written in fractional notation:
```
1/2		// Typical fraction notation commonly seen in recipes
2 3/4 	// Mixed fractions are also fine
8/4		// Improper fractions will be reduced to lowest terms, i.e. 2 in this case
```

Or decimal notation:
```
1.0
3.5
0.25
```

In integer (whole number) form:
```
1
10
45
```

## What recipe "Units" are supported?

The following is the list of supported units:

N.B., units are expected to be written in as one of the following. If you enter an ingredient and the unit is not present in this list, or there is a typo, the ingredient is considered invalid.

```
// Common Recipe Measurement Units
TSP, TBSP, FLOZ, CUP, CUPS, PINT, PINTS, QT, QTS, GAL, GALS,TEASPOON, TABLESPOON, FLUID_OUNCE, QUART, GALLON, TEASPOONS, TABLESPOONS, FLUID_OUNCES, QUARTS, GALLONS

// Metric
ML, CL, L, MILLILITER, CENTILITER, LITER, LITRE, MILLILITERS, CENTILITERS, LITERS, LITRES

// UNITS OF WEIGHT

// US Customary
OZ, LB, LBS, OUNCE, OUNCES, POUND, POUNDS

// Metric
MG, G, KG, MILLIGRAM, GRAM, KILOGRAM, MILLIGRAMS, GRAMS, KILOGRAMS

/* ----- OTHER ----- */

PIECES, KNOBS, CLOVES, STICKS, GLUGS, INCHES, DASHES, PINCHES, DROPS, BLOCK, PIECE, KNOB, CLOVE, STICK, GLUG, WHOLE, INCH, CM, DASH, PINCH, DROP, BLOCKS, PERCENT, NO_UNIT; // for situations where no unit is needed
```

### Bugs

## Known Bugs

• Cancelling a recipe import will make the application hang, and will still import the recipes. For now, cancelling an import is disabled.

## Submitting a Bug Report

My contact information can be found at github.com/caileanb773. Please include something like "Bug Report" in your email, along with a description of *what you were doing* when the bug happened, and a description of the bug's effects.

### Credit

Cailean Bernard, July 14 2025 - 