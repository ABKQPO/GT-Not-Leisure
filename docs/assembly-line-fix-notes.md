# Assembly Line Fix Notes

This document marks the code changes added to fix GTNL assembly line recipes that were visible in NEI but failed at runtime with the equivalent of "no available recipe/data".

## Modified Files

- `src/main/java/com/science/gtnl/utils/recipes/RecipeBuilder.java`
  - Preserves `itemInputs(Object...)` when forwarding recipes to the native GT recipe builder.
  - This keeps ore-dict alternatives (`mOreDictAlt`) intact for assembly line recipes.

- `src/main/java/com/science/gtnl/utils/recipes/GTNLAssemblyLineRecipeAdder.java`
  - New helper introduced for GTNL researchable assembly line registration.
  - Keeps the original TecTech `TTRecipeAdder` registration.
  - Mirrors the same recipe into standard `GTRecipeConstants.AssemblyLine`.

- `src/main/java/com/science/gtnl/common/recipe/gregtech/AssemblingLineRecipes.java`
  - Switched GTNL researchable assembly line calls from `TTRecipeAdder` to `GTNLAssemblyLineRecipeAdder`.

- `src/main/java/com/science/gtnl/common/recipe/gregtech/CircuitAssemblerConvertRecipes.java`
  - Switched GTNL researchable assembly line calls from `TTRecipeAdder` to `GTNLAssemblyLineRecipeAdder`.

- `src/main/java/com/science/gtnl/mixins/late/Gregtech/MixinAssemblyLineUtils.java`
  - New fallback lookup for data-stick output resolution.
  - When standard GT AssemblyLine lookup returns nothing, it checks `TecTechRecipeMaps.researchableALRecipeList`.

- `src/main/java/com/science/gtnl/mixins/Mixins.java`
  - Registers `Gregtech.MixinAssemblyLineUtils`.

## Why These Changes Were Needed

Two separate issues caused the runtime failure:

1. Many GTNL assembly line recipes were only registered through TecTech research registration.
   - They could appear in NEI.
   - But standard assembly line lookup from data sticks could still fail.

2. GTNL's `RecipeBuilder` dropped ore-dict alternative inputs when forwarding `Object...` item inputs.
   - That caused some mirrored assembly line recipes to lose valid alternative matching data.

## Practical Result

After these changes:

- GTNL assembly line recipes remain visible in NEI.
- Standard GT assembly line entries are also created for GTNL researchable recipes.
- Data sticks can resolve either the standard GT entry or the TecTech fallback entry.
- Ore-dict alternative inputs remain available for matching.
