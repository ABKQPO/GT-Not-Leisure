{white:{bold:{italic:Rise, my servants, and sing to welcome my arrival.}}}
Deals damage to a large area of entities and generates power, entities that take damage too many times will be {red:directly killed}
If a certain type of entity takes damage too many times, it will skip that entity!
Entities with health below a certain value will be {red:directly killed}
And the owner of the machine will take {red:5 * number of entities killed }magic damage and receive several debuffs
{gray:{hr}}
This machine has a {red:Qliphoth Counter}, with a maximum of {green:3}, it decreases by one every time the machine runs, and will emit a redstone signal when maintenance is needed
The redstone signal strength will be interpreted as a {aqua:4-bit binary number} from {green:0} to {green:15}
The lowest bit is marked as bit 4, and the highest bit is bit 1
{gray:{hr}}
{yellow:Bit 1: }{red:{italic:Detected work instruction system failure}}
If this bit is {green:1}, the lowest bit is marked as bit 1, and the highest bit is bit 4
The output signal is not adjusted by bit 1!
{gray:{hr}}
{yellow:Bit 2: }{gold:{italic:Detected Qliphoth energy collapse}}
If this bit is {green:1}, the corresponding suppression fluid should be input
To determine which fluid to insert, interpret bits 1 and 3 as a 2-bit binary number
{green:0: 10000L Molten Neutron / 1: 7500L Molten Infinity / 2: 5000L Space-Time / 3: 2500L Magnetic Matter}
{gray:{hr}}
{yellow:Bit 3: }{italic:Tick... Tock...}
If this bit is {green:1}, {green:5000000L} of purified water should be input
Using higher levels of purified water can increase the probability during judgment!
{yellow:Output multiplier: }{green:Level 5: 2% / Level 6: 3% / Level 7: 4% / Level 8: 5%}
{gray:{hr}}
{yellow:Bit 4: }{red:{italic:Blood mist spreads, corpses lie everywhere.}}
If this bit is {green:1}, {green:100000L} of blood and {green:1} flawless core should be input
{gray:{hr}}
{yellow:No bit: }{yellow:{italic:Pure self}}
{yellow:Tree of Light}
{red:Machine energy overload, requires 10000L of expanded space fluid}
{gray:{hr}}
{red:After maintenance failure, the check result will immediately become }{red:{italic:Poor!}}
After successful maintenance, {green:35} probability checks will be performed, {italic:each check interval is 2 seconds}
If the number of checks cannot be completed during the current run, the remaining checks will immediately be negative
Base probability is {green:45%}, placing higher-level field generators in the host can increase the probability
{italic:As the cumulative number of checks increases, the judgment time and probability will increase}
{red:{italic:Every 5 entities killed will immediately reduce the Qliphoth Counter by 1}}
{red:{italic:If the machine runs without dealing damage to any entity, the Qliphoth Counter will immediately reduce by 1}}
Check result is {green:{italic:Excellent (26~35)}} will produce a large amount of power and fully heal the player and apply buffs, then increase the Qliphoth Counter by {green:1}
If {yellow:{italic:Good (6~15)}} it will produce less power and heal the player by {green:5 HP}, with a {green:50%} chance to increase the Qliphoth Counter by 1
If {red:{italic:Poor (0~15)}} it will apply debuffs to the player, spawn several monsters, and shut down
{gray:{hr}}
{red:Every 10 maintenance will increase the Qliphoth Meltdown Level by 1}, maximum level is {green:10}, each level reduces the success rate of probability checks by {red:1%}
Whenever the Qliphoth Meltdown Level increases, the machine will enter {red:Qliphoth Energy Meltdown State}, requiring immediate maintenance, time limit {green:1 minute}
If no maintenance is performed during the {red:Qliphoth Energy Meltdown State}, the Qliphoth Counter will immediately drop to {red:0} after the countdown ends
As the Qliphoth Meltdown Level increases, it will generate {italic:Dawn, Noon, Dusk, Midnight} trials
The types of trials include {green:Green}, {gold:Amber}, {light_purple:Violet}, {red:Blood}, {blue:Indigo}
If {red:Qliphoth Meltdown Level reaches 10}, all trial types will become {italic:Pale}, difficulty and rewards will significantly increase
Completing trials will generate a portion of the machine's EU per second multiplied by the total time spent:
{yellow:Dawn: 10%}
{yellow:Noon: 15%}
{yellow:Dusk: 20%}
{yellow:Midnight: 25%}
{gray:{hr}}
{red:When the Qliphoth Counter is 0, it will trigger a massive explosion causing 500 billion pollution}
{red:Generates a large number of monsters, 11 Ender Dragons, and 1 Wither. Cannot be restarted until no Wither or Ender Dragons are detected within the range.}
{red:Players within the range will receive 500 magic damage + 5000 physical damage.}
{gray:{hr}}
Generates {green:2147483647 * Total damage dealt to all entities * Entity type * 500} EU per second during operation. Requires a Force Field Generator in the host to complete structure check.
Fixed operation time of {green:500 seconds}.
{black:{italic:{strikethrough:Install the Final Spiral to ignore the above mechanisms.}}}
{red:{italic:Note: EU cache will be generated inside the machine during operation. Destruction will immediately cause an explosion!}}
