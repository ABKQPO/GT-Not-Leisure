# Singularity Iteration Notice

The armor flight animation (the suit flying from its anchor onto the wearer) is
ported from Singularity Iteration by its contributors:

https://github.com/2132937983/Singularity-Iteration
(commit b355c64facb419987698333f72fe5da49872a357)

Singularity Iteration is licensed under the Apache License, Version 2.0. See
`SINGULARITY-ITERATION-APACHE-2.0.txt` for the license text.

## Ported Material

Source code, rewritten from NeoForge 1.21.1 to Forge 1.7.10 and modified
(flight paths, timing, segmented assembly, renderer, client effects; hand-held
pieces removed; camera-aligned first-person flight and from-behind approach
added):

- `com.science.gtnl.common.armorflight.ArmorFlight`
  (from `ArmoryFlight`)
- `com.science.gtnl.common.entity.EntityArmorFlightPiece`
  (from `ArmoryPieceEntity`)
- `com.science.gtnl.common.render.entity.ArmorFlightPieceRender`
  (from `ArmoryPieceRenderer`)
- `com.science.gtnl.client.armorflight.ArmorFlightEffects`,
  `ArmorFlightFX`, `ArmorFlightSound`
  (from `ArmoryClientEffects`)
- `com.science.gtnl.api.armorflight.ArmorFlightPiece`
  (from `ArmoryPiece`)

Sound effects, copied unchanged and renamed:

- `assets/sciencenotleisure/sounds/armory/*.ogg`
  (from `assets/mio_icif/sounds/sfx_armory_*.ogg`)
