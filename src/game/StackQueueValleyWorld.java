package game;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.actors.Farmer;
import game.actors.Merchant;
import game.actors.SporeBeast;
import game.animals.Chicken;
import game.animals.Cow;
import game.products.minerals.AlienDevice;
import game.products.minerals.Geode;
import game.terrains.CauliflowerCrop;
import game.terrains.CopperNode;
import game.terrains.Dirt;
import game.terrains.Ladder;
import game.terrains.MelonCrop;
import game.terrains.ParsnipCrop;
import game.terrains.Puddle;
import game.terrains.Rock;
import game.terrains.StrawberryCrop;
import game.terrains.TeleportationCircle;
import game.terrains.TomatoCrop;
import game.terrains.facilities.PreservesJar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Represents the game world containing the farm and mines maps.
 */
public class StackQueueValleyWorld extends World {

    /**
     * Creates the game world.
     *
     * @param display the display used by the world
     */
    public StackQueueValleyWorld(Display display) {
        super(display);
    }

    /**
     * Initialises the farm, mines, actors, items, and map connections.
     *
     * @throws Exception if the world cannot be initialised
     */
    public void initialise() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();

        groundCreator.registerGround('.', Dirt::new);
        // REQ1: the parsnip and tomato crops now share the crop framework, so a
        // cauliflower flood never converts a neighbouring crop into a puddle.
        groundCreator.registerGround('P', ParsnipCrop::new);
        groundCreator.registerGround('T', TomatoCrop::new);
        groundCreator.registerGround('S', StrawberryCrop::new);
        groundCreator.registerGround('F', CauliflowerCrop::new);
        groundCreator.registerGround('M', MelonCrop::new);
        groundCreator.registerGround('J', PreservesJar::new);

        List<String> inheritedFarm = Arrays.asList(
                "..P....T..",
                "....SFM...",
                ".......P..",
                "...T....J.",
                "..........",
                ".P........",
                ".......T.."
        );

        GameMap inheritedFarmMap = new GameMap(
                "Inherited Farm",
                groundCreator,
                inheritedFarm);
        this.addGameMap(inheritedFarmMap);

        /*
         * REQ2: create and register the mines map.
         */
        GameMap minesMap = new GameMap(
                "Mines",
                groundCreator,
                '.',
                10,
                7);
        this.addGameMap(minesMap);

        /*
         * REQ2: configure map traversal and mine contents.
         */
        configureTravel(inheritedFarmMap, minesMap);
        populateMines(minesMap);
        placeAlienDevice(inheritedFarmMap, minesMap);

        Farmer farmer = new Farmer("Bob");
        this.addPlayer(farmer, inheritedFarmMap.at(5, 3));

        /*
         * Main branch actor placement.
         */
        Chicken chicken = new Chicken();
        inheritedFarmMap.addActor(chicken, randomLocation(inheritedFarmMap, chicken));
        Cow cow = new Cow();
        inheritedFarmMap.addActor(cow, randomLocation(inheritedFarmMap, cow));
        Merchant merchant = new Merchant("George");
        inheritedFarmMap.addActor(merchant, inheritedFarmMap.at(5, 5));
//        inheritedFarmMap.addActor(merchant, randomLocation(inheritedFarmMap, merchant));

//        Actor bat = new game.animals.Bat();
//        minesMap.addActor(bat, randomLocation(minesMap, bat));
//        Actor slime = new game.animals.Slime();
//        minesMap.addActor(slime, randomLocation(minesMap, slime));
//        Actor ghost = new game.animals.CaveGhost();
//        minesMap.addActor(ghost, randomLocation(minesMap, ghost));

        placeSporeBeast(inheritedFarmMap);
    }

    /** Places the REQ5 creature on the first free dirt tile without replacing actors. */
    private void placeSporeBeast(GameMap map) throws Exception {
        SporeBeast beast = new SporeBeast();
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location location = map.at(x, y);
                if (location.getGround() instanceof Dirt && location.canActorEnter(beast)) {
                    map.addActor(beast, location);
                    return;
                }
            }
        }
        throw new IllegalStateException("No free dirt tile for SporeBeast.");
    }

    /**
     * Connects ladder pairs and teleportation-circle pairs between the
     * farm and the mines.
     *
     * @param farmMap the farm map
     * @param minesMap the mines map
     */
    private void configureTravel(GameMap farmMap, GameMap minesMap) {
        connectLadders(farmMap.at(0, 0), minesMap.at(0, 0));
        connectLadders(farmMap.at(9, 6), minesMap.at(9, 6));

        connectTeleportationCircles(
                farmMap.at(9, 0),
                minesMap.at(9, 0));

        connectTeleportationCircles(
                farmMap.at(0, 6),
                minesMap.at(0, 6));
    }

    /**
     * Places rocks, copper nodes, puddles, and geodes in the mines.
     *
     * @param minesMap the mines map
     */
    private void populateMines(GameMap minesMap) {
        minesMap.at(4, 2).setGround(new Rock());
        minesMap.at(5, 2).setGround(new Rock());
        minesMap.at(4, 4).setGround(new CopperNode());

        minesMap.at(3, 3).setGround(new Puddle());
        minesMap.at(4, 3).setGround(new Puddle());
        minesMap.at(5, 3).setGround(new Puddle());
        minesMap.at(3, 4).setGround(new Puddle());
        minesMap.at(5, 4).setGround(new Puddle());

        minesMap.at(7, 2).addItem(new Geode());
        minesMap.at(7, 4).addItem(new Geode());
    }

    /**
     * Places an Alien Device on the farm and configures its destinations.
     *
     * @param farmMap the farm map
     * @param minesMap the mines map
     */
    private void placeAlienDevice(GameMap farmMap, GameMap minesMap) {
        AlienDevice alienDevice = new AlienDevice();

        alienDevice.addDestination(farmMap.at(4, 1));
        alienDevice.addDestination(minesMap.at(7, 5));

        farmMap.at(4, 1).addItem(alienDevice);
    }

    /**
     * Places two connected ladders.
     *
     * @param firstLocation the first ladder location
     * @param secondLocation the second ladder location
     */
    private void connectLadders(
            Location firstLocation,
            Location secondLocation) {

        Ladder firstLadder = new Ladder();
        Ladder secondLadder = new Ladder();

        firstLadder.setDestination(secondLocation);
        secondLadder.setDestination(firstLocation);

        firstLocation.setGround(firstLadder);
        secondLocation.setGround(secondLadder);
    }

    /**
     * Places two connected teleportation circles.
     *
     * @param firstLocation the first circle location
     * @param secondLocation the second circle location
     */
    private void connectTeleportationCircles(
            Location firstLocation,
            Location secondLocation) {

        TeleportationCircle firstCircle = new TeleportationCircle();
        TeleportationCircle secondCircle = new TeleportationCircle();

        firstCircle.setDestination(secondLocation);
        secondCircle.setDestination(firstLocation);

        firstLocation.setGround(firstCircle);
        secondLocation.setGround(secondCircle);
    }

    /** Selects from the current free, enterable tiles; fails promptly if the map is full. */
    protected Location randomLocation(GameMap map, Actor actor) {
        List<Location> candidates = new ArrayList<>();
        for (int y : map.getYRange()) {
            for (int x : map.getXRange()) {
                Location location = map.at(x, y);
                if (location.canActorEnter(actor)) candidates.add(location);
            }
        }
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No free enterable tile for " + actor);
        }
        return candidates.get(new Random().nextInt(candidates.size()));
    }
}
