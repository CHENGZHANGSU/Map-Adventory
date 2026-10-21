# StackQueue Valley — FIT2099 Assignment 2

A turn-based Java farming and exploration game built on the FIT2099 engine.
This README covers REQ1–REQ5, the current implementation, design documents and verification steps.

## Contribution Log

[View the team contribution log](https://docs.google.com/spreadsheets/d/1DYbucwDD6urK_fF4c7StrryQ5tFL4avNMSaVEDTSRsc/edit?usp=sharing)

The shared spreadsheet records task allocation, individual contributions, progress and completion dates.

## Build and Run

- Install **JDK 21 or newer** and make `java` and `javac` available on your PATH.
- Open this project in your Java IDE, mark `src` as the source directory and run `game.Application`.
- Alternatively, run these commands in PowerShell from the project root:

```powershell
$sourceFiles = @(Get-ChildItem ./src -Recurse -Filter '*.java' | ForEach-Object FullName)
javac -encoding UTF-8 -d out/classes @sourceFiles
if ($LASTEXITCODE -eq 0) { java -cp out/classes game.Application }
```

**Current build status (9 October 2026): blocked.** `TerritorialState` calls
`new ChaseAction(creature, map.locationOf(target))`, but `ChaseAction` currently
only defines a one-argument constructor. The team must align these APIs before
the complete game or automated tests can run. The instructions below describe
the checked-in features; they are not a claim that all integration checks pass.

Select actions using the keys displayed by the console menu. Available actions
depend on nearby actors, terrain, inventory, tools and stamina.

## World and Starting Setup

The game contains a farm and a mine, connected by ladders and teleportation circles.
The farmer (`@`) starts on the farm with 100 HP, 20 maximum stamina, 10,000 coins,
a hoe, watering can, pickaxe, diamond and milk. Stamina recovers by one each turn.
The farm also contains crops, a preserves jar, farm animals, a wandering merchant
and a SporeBeast. Hostile creatures inhabit the mine.

Coordinates below use `(x, y)`, starting at `(0, 0)` in the top-left corner.

| Location | Initial features |
| --- | --- |
| Both maps: `(0, 0)` and `(9, 6)` | Linked ladders |
| Both maps: `(9, 0)` and `(0, 6)` | Linked teleportation circles |
| Farm: `(4, 1)` | Alien device |
| Farm: `(8, 3)` | Preserves jar |
| Mine: `(4, 2)` and `(5, 2)` | Rocks |
| Mine: `(4, 4)` | Copper node, surrounded by puddles |
| Mine: `(7, 2)` and `(7, 4)` | Geodes |

## Requirements Overview

| Requirement | Features | Main implementation areas |
| --- | --- | --- |
| REQ1 | Watering, harvesting and consuming crops | `game.terrains`, `game.products.crops`, `game.effects` |
| REQ2 | Travel, mining, breakable objects and environmental hazards | Teleport/break actions, terrain and mineral products |
| REQ3 | Buying, selling and processing produce | Merchant, trade actions and processing facilities |
| REQ4 | Combat, hostile creatures and weapon coatings | `game.animals`, `game.products.weapons` |
| REQ5 | SporeBeast states, foraging, threats and escape | `game.actors.SporeBeast`, `game.states`, `game.creatures` |

## REQ1 — Crops

`Crop` shares harvest validation and stamina handling. `WaterableCrop` adds
watering checks and a watering counter. `WaterAction`, `HarvestAction` and
`ConsumeAction` delegate to capability interfaces rather than selecting crop
types themselves. A carried watering tool and sufficient stamina are required.

| Crop | Water cost | Watering effect | Harvest cost | Harvest effect |
| --- | --- | --- | --- | --- |
| Strawberry | 1 SP | Drops two strawberries onto randomly selected adjacent tiles; each may be rotten | 1 SP | Adds two strawberries to inventory; destruction chance decreases with watering |
| Cauliflower | 2 SP | 50% chance to turn adjacent non-crop terrain into puddles | 3 SP | Requires five waterings; removes the crop, with a 50% chance of receiving a cauliflower |
| Melon | 1 SP | 50% chance to increase maximum stamina by one | 5 SP | Removes the crop; either gives a melon or explodes for `max(0, 20 - wateringCount)` damage to actors on and around the crop |
| Parsnip | 1 SP | Heals the farmer by 20 HP, with an increasing chance to destroy the plant | 1 SP | Gives a parsnip and removes the crop; 50% chance to replant on an eligible neighbouring dirt tile |

Tomatoes retain their existing harvest behaviour: spending 2 SP gives two tomatoes,
increases maximum stamina by one and replaces the crop with dirt.

Consumption uses the existing product effects:

- Fresh strawberries restore 1 HP per turn for two turns; rotten strawberries cause 1 damage per turn for three turns.
- Cauliflower restores 25 HP and has a 50% chance to provide a replacement cauliflower.
- Melon increases maximum HP by 10 without automatically filling the additional health capacity.
- Parsnip restores 1 HP. Tomato restores 1 SP and increases maximum HP by 2.

`TimedHealthEffect` handles strawberry effects over later turns. `Statistics`
provides maximum-statistic changes without accidentally refilling the current value.

**Try it:** approach a crop with the watering can, select watering or harvesting,
and consume the harvested produce from inventory. For cauliflower, compare an
attempt before five waterings with an attempt after the fifth watering.

## REQ2 — Exploration, Mining and Hazards

Travel actions delegate through `Teleportable`:

- **Ladders:** move between linked map locations, with a 50% chance of taking 25 fall damage.
- **Teleportation circles:** move between linked locations and create fire around the arrival tile.
- **Alien device:** offers configured destinations, with a 50% chance of malfunctioning and sending the user to a valid location on the current map.

Breaking requires a carried tool implementing `Breaker`, such as the pickaxe.
`BreakAction` delegates to `Breakable` objects:

| Object | Breaking result |
| --- | --- |
| Rock | Becomes dirt and drops a stone; a 50% shockwave may move the breaker to a valid tile next to the rock |
| Copper node | Becomes dirt, drops copper ore and charges adjacent chargeable terrain |
| Geode | Is removed; either yields a diamond or explodes, dealing 25 damage and igniting adjacent tiles |

Charged puddles deal 20 damage per tick to an actor standing on them. Fire applies
a timed burning effect and disappears after its configured lifespan. Circle fire
and geode fire use different lifespans and burn strengths.

**Try it:** use a ladder to enter the mine, break a rock and the copper node,
then inspect the dropped items and charged puddles. Break a geode to observe one
of its random outcomes.

## REQ3 — Trading and Processing

The merchant wanders around the farm. When nearby, the farmer can buy goods and
sell supported inventory items using `BuyAction` and `SellAction`. `Tradable`,
`Buyable` and `Sellable` separate transaction handling from item-specific effects.

| Transaction | Base price | Additional behaviour |
| --- | --- | --- |
| Buy parsnip crop | 250 coins | Plants near the merchant when space is available and drops three corresponding products around the planted crop |
| Buy tomato crop | 500 coins | Same planting/drop mechanism; 50% chance of receiving the purchase price back |
| Buy sword | 3,000 coins | 50% chance that the merchant takes payment but supplies no sword |
| Sell egg / milk | 40 / 60 coins | 50% chance of receiving no payment |
| Sell stone | 10 coins | Creates a rock on a randomly selected tile next to the merchant |
| Sell diamond | 5,000 coins | 50% chance that the merchant also removes another inventory item |

The preserves jar processes products implementing `Processable`. `ProcessAction`
replaces the inventory product with a `PreservedProduce` wrapper. Processing uses
a Decorator structure: `ProcessedProduce` holds the preceding product, while each
preserved layer defines an additional 1 HP healing effect. Repeated wrapping is
supported by the class design; nested consumption still needs integration checks
as noted under verification.

**Try it:** approach the merchant to inspect purchase and sale options. Bring a
processable product next to the farm's preserves jar and choose the processing action.

## REQ4 — Combat and Weapon Coatings

`AttackAction` delegates attacks to a `Weapon`. The farmer's intrinsic attack deals
5 damage with an 80% hit rate. A sword deals 20 base damage with an 85% hit rate.
Hostile creatures attack an adjacent farmer and otherwise use their animal behaviour.

| Enemy | HP | Intrinsic attack | Special reaction |
| --- | --- | --- | --- |
| Bat | 50 | 35 damage, 90% hit rate | Its attack-reaction hook drops a fresh strawberry on an adjacent tile |
| Slime | 150 | 15 damage, 50% hit rate | Its attack-reaction hook has a 50% chance to spawn another slime on an unoccupied adjacent tile |
| Cave ghost | 250 | 25 damage, 75% hit rate | Its death-reaction hook deals 50 damage to adjacent actors |

Swords implement `Coatable` and hold one `WeaponCoating`. Applying another coating
replaces the previous coating. Sources expose `CoatWeaponAction`:

| Coating | Source | Effect |
| --- | --- | --- |
| Melon | Carried melon, consumed when coating | Deals 15 explosion damage to actors adjacent to the target, potentially including the attacker |
| Fire | Environmental fire | Applies 5 burn damage per turn for three turns |
| Charged | Charged terrain | Deals an additional 30 damage to the target |

In the current implementation, coating effects run even when the base sword attack
misses. The sword invokes hostile reaction hooks after its attack; these hooks
should also be checked against the intrinsic-attack path during integration.

**Try it:** acquire a sword, apply a coating using an available source and attack
a nearby hostile creature. Compare normal damage, coating effects and enemy reactions.

## REQ5 — SporeBeast

The SporeBeast (`?`) starts on the first available dirt tile of the farm in
`CamouflagedState`. It uses the State pattern through `CreatureState` and
`BaseStates`. `SporeBeast.playTurn` evaluates at most one transition before selecting
an action; `onEnter` runs when the state changes.

| State / responsibility | Behaviour and current transitions |
| --- | --- |
| A — Camouflaged | Waits; one adjacent threat leads to Territorial, multiple adjacent threats reset the quiet counter, and ten undisturbed turns lead to Foraging |
| B — Foraging | Eats, picks up or searches for food; multiple threats within radius 2 lead to Fleeing, one leads to Territorial, and no threats together with no available food leads to Camouflaged |
| C — Territorial | Contains chase and attack logic; one adjacent threat with HP below 50 leads to Fleeing, while multiple adjacent threats lead to Camouflaged. Chase integration and quiet-turn handling remain unresolved |
| D — Fleeing | Selects an escape step; no threats within radius 2 lead to Camouflaged, while a sole adjacent threat leads to Territorial |

### B: Foraging

Each turn chooses one action in this order:

1. Consume one eligible carried food item.
2. Otherwise, pick up one eligible food item on the current tile.
3. Otherwise, move one step toward the nearest reachable ground food.
4. Wait when no eligible food is reachable.

Pickup, movement and consumption take separate turns. `EdibleFoodFilter` accepts
portable `ConsumableProduce`: strawberry, cauliflower and melon. Rotten strawberries
remain eligible and retain their harmful effect. Legacy tomato/parsnip and processed
food are excluded because their consumption contracts assume player-specific behaviour.

`NearestEdibleFoodFinder` and `DeterministicPathfinder` use breadth-first search on
the current map. Occupied or impassable tiles block movement; equal-distance
choices use a fixed x-then-y neighbour order. Targets are recalculated each turn,
and `PickUpFoodAction` rechecks item presence, location and eligibility before pickup.

On entry, `FoodLureEffect` moves at most three eligible ground items from unoccupied,
reachable tiles within square radius 2 onto the beast's tile. It preserves item
identity and does not take inventory items. Staying in Foraging does not repeat
this entry effect. Consumption reuses `ConsumeAction` and the original food effects.

### D: Escape and Entry Effects

`ThreatScanner` detects players and hostile creatures. `EscapeRoutePlanner` searches
reachable locations, preferring safer destinations, then shorter paths, with a
fixed coordinate order for ties. `SafetyEvaluator` rejects destination tiles
containing fire or charged terrain and requires sufficient distance from threats.
Intermediate path tiles are not all filtered for those hazards.

Entering Fleeing places spore traps on empty, enterable adjacent tiles without
duplicating existing spore traps. Camouflaged and Territorial currently reset
counters on entry; their environmental entry effects are not implemented there.

**Try it after resolving the build blocker:** leave the beast undisturbed for ten
turns and make food available by watering strawberries or dropping suitable produce.
Keep the farmer outside threat range to observe foraging, then approach to observe
state transitions. Use the automated scenarios for controlled edge cases.

## Design Documents

Links below point to the current files in `docs/design`.

| Requirement                       | Class diagram                                                                       | Sequence diagram(s)                                                                                                                                                                                                                                | Design rationale                                                                                                            |
|-----------------------------------|-------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| REQ1                              | [UML](docs/design/UML%20DIagram/REQ1%20UML.png)                                     | [Watering](docs/design/Sequence%20Diagram/REQ1-SequenceDiagram/Watering.png), [Harvesting](docs/design/Sequence%20Diagram/REQ1-SequenceDiagram/Harvesting.png), [Consumption](docs/design/Sequence%20Diagram/REQ1-SequenceDiagram/Consumption.png) | [PDF](docs/design/Design%20Rationale/REQ1%20design%20rationale.pdf)                                                         |
| REQ2                              | [UML](docs/design/UML%20DIagram/REQ2-UML.png)                                       | [Sequence](docs/design/Sequence%20Diagram/REQ2-SequenceDiagram.png)                                                                                                                                                                                | [PDF](docs/design/Design%20Rationale/REQ2-Design-Rationale.pdf)                                                             |
| REQ3                              | [UML](docs/design/UML%20DIagram/REQ3-UML.png)                                       | [Sequence](docs/design/Sequence%20Diagram/REQ3-SequenceDiagram.png)                                                                                                                                                                                | [PDF](docs/design/Design%20Rationale/REQ3-Design-Rationale.pdf)                                                             |
| REQ4                              | [UML](docs/design/UML%20DIagram/REQ4-UML.drawio.png)                                | [Combat and coating diagrams](docs/design/Sequence%20Diagram/REQ4-Sequence_Diagram)                                                                                                                                                                | [PDF](docs/design/Design20Rationale/REQ4_design_rationale.pdf)                                                              |
| REQ5 A — Framework, camouflage    | [UML](docs/design/UML%20DIagram/REQ5%20UML/REQ5-Core_and_Camouflage_UML.drawio.png) | [Sequence](docs/design/Sequence Diagram/REQ5 SequenceDiagram/REQ5-Core_and_CamouflageSequenceDiagram.png)                                                                                                                                          | [PDF](docs/design/Design Rationale/REQ5-Design-Rationale.pdf) |
| REQ5 B — Foraging                 | [UML](docs/design/UML%20DIagram/req5%20uml.png)                                     | [Sequence](docs/design/Sequence Diagram/REQ5 SequenceDiagram/REQ5 StateTransition.png)                                                                                                                                                             | [PDF](docs/design/Design Rationale/REQ5-Design-Rationale.pdf)                                                             |
| REQ5 C — Alert + Switching effect | [UML](docs/design/UML%20DIagram/REQ5%20UML/REQ5-Territorial%20State%20UML.png)      | [Sequence](docs/design/Sequence Diagram/REQ5 SequenceDiagram/REQ5-TerritorialStateSequenceDiagram.png)                                                                                                                                             | [PDF](docs/design/Design Rationale/REQ5-Design-Rationale.pdf)                               |
| REQ5 D — Fleeing                  | [UML](docs/design/UML%20DIagram/REQ5-Fleeing%20State%20UML.png)                     | [Sequence](docs/design/Sequence Diagram/REQ5 SequenceDiagram/REQ5-FleeingStateSequenceDiagram.png)                                                                                                                                                 | [PDF](docs/design/Design Rationale/REQ5-Design-Rationale.pdf)                                                |

REQ5 currently has separate B and D documents in these folders. A complete,
consolidated REQ5 design set covering A–D is not listed here.

## Verification and Remaining Integration

The local foraging verification script compiles all source files and runs the
REQ5 B scenarios:

```powershell
./scripts/test-req5-foraging.ps1
# If the JDK is not detected automatically:
./scripts/test-req5-foraging.ps1 -JdkPath 'C:\path\to\jdk'
```

The scenarios cover diet filtering, blocked paths, deterministic ties, occupied
targets, stale pickup, changed targets, consumption, timed effects, transitions
and entry effects. **The 9 October 2026 run stopped during compilation at the
`ChaseAction` constructor mismatch; no test result is claimed for this revision.**
The `scripts` and `tests` directories are currently local, untracked files and
must be included if this verification command is to be available after cloning.

Further integration checks identified from the current source:

- **REQ5 Territorial:** guard an empty threat list before `getFirst()`; fix the quiet-turn counter, which currently does not advance when no threat is present; align the chase action and its target handling.
- **REQ5 effects:** complete the Camouflaged/Territorial environmental entry effects and check spore-trap triggering and item-loss rules. The current trap effect removes inventory items rather than placing them on the ground.
- **REQ3 processing:** verify nested effects and item removal. `ProcessedProduce` delegates to a wrapped item no longer in inventory, while its own consumed wrapper is not removed by the current consumption path.
- **Whole game:** exercise trading, hazards, all combat paths and transitions together. The foraging scenarios alone do not verify all five requirements.



```
   ,-,--.  ,--.--------.   ,---.       _,.----.  ,--.-.,-.                
 ,-.'-  _\/==/,  -   , -\.--.'  \    .' .' -   \/==/- |\  \               
/==/_ ,_.'\==\.-.  - ,-./\==\-/\ \  /==/  ,  ,-'|==|_ `/_ /,--.--------.  
\==\  \    `--`\==\- \   /==/-|_\ | |==|-   |  .|==| ,   //==/,  -   , -\ 
 \==\ -\        \==\_ \  \==\,   - \|==|_   `-' \==|-  .| \==\.-.  - ,-./ 
 _\==\ ,\       |==|- |  /==/ -   ,||==|   _  , |==| _ , \ `--`--------`  
/==/\/ _ |      |==|, | /==/-  /\ - \==\.       /==/  '\  |               
\==\ - , /      /==/ -/ \==\ _.\=\.-'`-.`.___.-'\==\ /\=\.'               
 `--`---'       `--`--`  `--`                    `--`                     
    _,.---._                     ,----.                  ,----.           
  ,-.' - ,  `.   .--.-. .-.-. ,-.--` , \ .--.-. .-.-. ,-.--` , \          
 /==/ ,    -  \ /==/ -|/=/  ||==|-  _.-`/==/ -|/=/  ||==|-  _.-`          
|==| - .=.  ,  ||==| ,||=| -||==|   `.-.|==| ,||=| -||==|   `.-.          
|==|  : ;=:  - ||==|- | =/  /==/_ ,    /|==|- | =/  /==/_ ,    /          
|==|,  '='  ,  ||==|,  \/ - |==|    .-' |==|,  \/ - |==|    .-'           
 \==\ _   -    ;|==|-   ,   /==|_  ,`-._|==|-   ,   /==|_  ,`-._          
  '.='.  ,  ; -\/==/ , _  .'/==/ ,     //==/ , _  .'/==/ ,     /          
    `--`--'' `--`--`..---'  `--`-----`` `--`..---'  `--`-----``           
        ,-.-.  ,---.                              ,----.                  
 ,--.-./=/ ,/.--.'  \       _.-.      _.-.     ,-.--` , \ ,--.-.  .-,--.  
/==/, ||=| -|\==\-/\ \    .-,.'|    .-,.'|    |==|-  _.-`/==/- / /=/_ /   
\==\,  \ / ,|/==/-|_\ |  |==|, |   |==|, |    |==|   `.-.\==\, \/=/. /    
 \==\ - ' - /\==\,   - \ |==|- |   |==|- |   /==/_ ,    / \==\  \/ -/     
  \==\ ,   | /==/ -   ,| |==|, |   |==|, |   |==|    .-'   |==|  ,_/      
  |==| -  ,//==/-  /\ - \|==|- `-._|==|- `-._|==|_  ,`-._  \==\-, /       
  \==\  _ / \==\ _.\=\.-'/==/ - , ,/==/ - , ,/==/ ,     /  /==/._/        
   `--`--'   `--`        `--`-----'`--`-----'`--`-----``   `--`-`         
```
