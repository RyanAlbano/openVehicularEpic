package ve.vehicles;

import ve.Sound;

class VehicleAudio {
 Sound fix, land, boost, gate,
 turret, burn, grind, airEngage, splash, splashOverSurface, tsunamiSplash, mineExplosion,
 crashSoft, crashHard, crashDestroy,
 hitShot, hitRicochet, hitExplosive, explode, force, scrape, skid, exhaust, fly, train, massiveHit, spinner, nuke, chuff, backUp,
 engine, turbineThrust;

 public void close(Vehicle vehicle) {
  for (Special special : vehicle.specials) {
   if (special.sound != null) {
    special.sound.close();
   }
  }
  if (airEngage != null) airEngage.close();
  if (burn != null) burn.close();
  if (land != null) land.close();
  if (fix != null) fix.close();
  if (grind != null) grind.close();
  if (boost != null) boost.close();
  if (gate != null) gate.close();
  if (turret != null) turret.close();
  if (splash != null) splash.close();
  if (splashOverSurface != null) splashOverSurface.close();
  if (tsunamiSplash != null) tsunamiSplash.close();
  if (mineExplosion != null) mineExplosion.close();
  if (engine != null) engine.close();
  if (turbineThrust != null) turbineThrust.close();
  if (explode != null) explode.close();
  if (fly != null) fly.close();
  if (crashSoft != null) crashSoft.close();
  if (crashHard != null) crashHard.close();
  if (crashDestroy != null) crashDestroy.close();
  if (exhaust != null) exhaust.close();
  if (force != null) force.close();
  if (scrape != null) scrape.close();
  if (chuff != null) chuff.close();
  if (hitShot != null) hitShot.close();
  if (hitRicochet != null) hitRicochet.close();
  if (hitExplosive != null) hitExplosive.close();
  if (skid != null) skid.close();
  if (train != null) train.close();
  if (spinner != null) spinner.close();
  if (massiveHit != null) massiveHit.close();
  if (backUp != null) backUp.close();
  if (nuke != null) nuke.close();
 }
}
