Automatically breeds bees to meet your bee needs
Input princesses or drones to record their species in the machine's archive
The GUI shows which bees can be bred from your current species archive
Base success chance follows Forestry mutation chances
Requires the original mutation foundations in input buses; checks every 10 seconds without consuming them
Missing foundations pause breeding without disabling the machine; supplying them resumes it automatically

Starts with {var:parallels} parallel, {var:attemptSeconds} seconds per attempt and {var:baseEu} EU/t per parallel
Requires LuV; each higher voltage tier doubles the parallel limit
Each overclock uses 4 times the EU/t per parallel and halves breeding time
Starting from HV glass, each higher glass tier doubles EU/t and adds {var:glassChance}% success chance
Runs fewer parallels when power is limited; waits if there is not enough power for one
Each input princess produces one target princess and one target drone upon completion
