# Large Bee Breeder validation

The port targets GTNL `dev-290`, starting at `6d650c6bde0a82b15ed5d0e91ff95cbe4b745519`.
It extends `MultiMachineBase` and `GTNLMultiBlockBaseGui`; structure descriptions use
`GTNLMultiblockTooltipBuilder`. Machine ID `21229` is allocated in `GTNLMachineID`.

Run the normal checks from the GTNL checkout:

```powershell
./gradlew.bat spotlessApply compileJava processResources spotlessCheck
git diff --check
```

The optional Windows client fixture uses a separate build directory, save and muted client:

```powershell
$env:JAVA_TOOL_OPTIONS = '-Djdk.net.unixdomain.tmpdir=C:/Windows/Temp'
./gradlew.bat runClient17 --init-script scripts/bee-breeder-client-qa.init.gradle --no-configuration-cache --no-daemon --max-workers=2 --console=plain
```

The fixture checks actual Forestry routes, missing parents, reachable species, assembler
registration, input consumption, repeated scans, power failure, pending princess outputs,
NBT recovery and completion through GTNL's active tick, including delivery of one princess
and one drone to a real output bus. It also opens the native GUI and checks all four pages,
search, draft selection, invalid targets, NEI ghost drops and server-side confirmation.
Results and screenshots are written to `build/bee-breeder-client-qa`.

The fixture uses real controllers and an input bus but does not construct the entire
multiblock or test long-term operation of a fully formed structure.

Verified on 2026-10-27: the client fixture reports `PASS`. The fixture adds Railcraft only
to the test runtime because GT's firestone integration needs it during startup. It accepts
both the vanilla main menu and GalaxySpace's replacement menu.
