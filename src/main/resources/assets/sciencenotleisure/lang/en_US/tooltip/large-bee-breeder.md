{gold:Automatically breeds bees to meet your bee needs}
Put in princesses or drones to record their species
Choose a target in the GUI and the machine breeds it automatically; the GUI also shows which bees you can breed
Success chance follows Forestry's original mutation chance
Put the required foundation blocks in an {white:input bus}; they are {green:not consumed}
Foundations are checked every 10 seconds; breeding pauses if any are missing and resumes when supplied
Species are grouped into HV, EV, IV, LuV, ZPM, UV and UHV; the GUI shows the voltage each needs
You need the required voltage even for recorded species; species without a configured tier require UHV

Starts at {yellow:HV}: {white:{var:parallels}} parallel, {white:{var:attemptSeconds} seconds} per attempt, {white:{var:baseEu} EU/t} per parallel
Each higher voltage tier {gold:doubles the parallel limit}, uses {red:4 times} the EU/t per parallel and {green:halves} the time
Put {light_purple:royal jelly} in an input bus: each parallel uses {white:2} per attempt for {light_purple:one extra lossy overclock}
This uses {red:4 times} the EU/t again and {green:halves} the time again; not enough jelly or power means normal speed
Starting from {yellow:HV} glass, each higher glass tier {red:doubles} EU/t and adds {gold:{var:glassChance}%} success chance
If power is short, fewer parallels run; if it cannot power one, the machine waits
Each princess you put in gives one target princess and one target drone when finished
