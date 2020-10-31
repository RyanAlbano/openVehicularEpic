package ve.utilities;

/**
 * A dictionary which holds all duplicate String literals for the project, as well as duplicate map names.
 */
public enum D {
 ;
 //Variable names always match the string EXACTLY, when possible
 public static final String
 autoAim = "autoAim", aerialOnly = "aerialOnly", antigravity = "antigravity",
 base = "base", basic = "basic", boulder = "boulder", brightmetal = "brightmetal", BiDirectional = "BiDirectional", blink = "blink", bounce = "bounce", blueJet = "blueJet", BONUS = "BONUS", BonusOpen = "BonusOpen",
 controller = "controller", chuff = "chuff", curve = "curve", cactus = "cactus", CANCEL = "CANCEL", crashSoft = "crashSoft", crashHard = "crashHard", crashDestroy = "crashDestroy",
 destroyed = "destroyed", drive = "drive", death = "death", deathExplode = "deathExplode",
 END = "END", exhaust = "exhaust",
 fire = "fire", firelight = "firelight", flick1 = "flick1", flick2 = "flick2", force = "force", fromXZ = "fromXZ", fromYZ = "fromYZ", foliage = "foliage", fastCull = "fastCull", fastCullB = "fastCullB", fastCullF = "fastCullF", fastCullR = "fastCullR", fastCullL = "fastCullL",
 ground = "ground", grid = "grid", gate = "gate", grass = "grass", GameSettings = "GameSettings", GameVehicles = "GameVehicles", getXY = "getXY", getYZ = "getYZ",
 hard = "hard", HostReady = "HostReady", hide = "hide", hitShot = "hitShot", hitRicochet = "hitRicochet", hitExplosive = "hitExplosive",
 Instant = "Instant", ice = "ice",
 jetfighter = "jetfighter", joinerReady = "joinerReady",
 light = "light", lit = "lit", line = "line", landingGear = "landingGear",
 metal = "metal", meteor = "meteor", Meshes_/*:*/ = "Meshes: ", Map = "Map", MatchLength = "MatchLength", massiveHit = "massiveHit", mineExplode = "mineExplode",
 name = "name", Name = "Name", None = "None", nuke = "nuke", nukeMax = "nukeMax", noTexture = "noTexture", noSpecular = "noSpecular",
 OPEN_VEHICULAR_EPIC = "OPEN VEHICULAR EPIC", OPTIONS = "OPTIONS",
 paved = "paved", pavedColor = "pavedColor", particle = "particle", Port = "Port",
 RA = "RA", rain = "rain", rock = "rock", RGB = "RGB", repair = "repair", reflect = "reflect", rimFaces = "rimFaces", Ready = "Ready",
 size = "size", scale = "scale", sand = "sand", strip = "strip", shiny = "shiny", sport = "sport", snow = "snow", steers = "steers", steerXZ = "steerXZ", steerXY = "steerXY", steerYZ = "steerYZ", steerFromXZ = "steerFromXZ", steerFromYZ = "steerFromYZ", spinner = "spinner", selfIlluminate = "selfIlluminate", sportRimFaces = "sportRimFaces", skidHard = "skidHard", skidOff = "skidOff", scrape = "scrape",
 type = "type", texture = "texture", tree = "tree", turret = "turret", turretBarrel = "turretBarrel", train = "train", thrust = "thrust", thrustBlue = "thrustBlue", thrustWhite = "thrustWhite", theRandomColor = "theRandomColor", tornado = "tornado", tsunami = "tsunami", tsunamiSplash = "tsunamiSplash", TargetHost = "TargetHost",
 UserName = "UserName",
 vehicles = "vehicles", Vehicle = "Vehicle", Vertices_/*:*/ = "Vertices: ",
 white = "white", waypoint = "waypoint", Waypoint = "Waypoint", wood = "wood", wheel = "wheel", water = "water", wheelPoint = "wheelPoint", wheelFaces = "wheelFaces", wheelRingFaces = "wheelRingFaces",
 XY = "XY", XZ = "XZ", YZ = "YZ";

 /**
  * Returns the input String with a single whitespace on each side. This is useful when checking a String using .contains() to prevent undesired string matches.
  */
 public static String thick(String s) {
  return " " + s + " ";
 }

 public enum Maps {
  ;
  public static final String
  lapsOfGlory = "Laps of Glory",
  speedway2000000 = "V.E. Speedway 2000000",
  theBottleneck = "the Bottleneck",
  testOfDamage = "the Test of Damage",
  crystalCavern = "Crystal Cavern",
  matrix2x3 = "Matrix 2x3",
  vehicularFalls = "Vehicular Falls",
  summitOfEpic = "SUMMIT of EPIC",
  theSun = "the Sun",
  tunnelOfDoom = "the Tunnel of Doom",
  devilsStairwell = "Devil's Stairwell",
  highlands = "Highlands",
  ghostCity = "Ghost City",
  methodMadness = "is there a Method to the Madness?",
  circleRaceXL = "Circle Race XL",
  volcanicProphecy = "Volcanic Prophecy",
  theMaze = "the Maze",
  outerSpace1 = "Outer Space V1",
  XYLand = "XY Land";
 }
}
