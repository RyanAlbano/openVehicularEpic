/*
 * Copyright (c) 2012, Finn Kuusisto
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package kuusisto.tinysound;

/**
 * The Music interface is an abstraction for music.  Music objects should only
 * be loaded via the TinySound {@code loadMusic()} functions.  Music can be
 * played, paused, resumed, stopped and looped from specified positions.
 *
 * @author Finn Kuusisto
 */
public interface Music {

 /**
  * Play this Music and loop if specified.
  *
  * @param loop if this Music should loop
  */
 void play(boolean loop);

 /**
  * Play this Music at the specified volume and loop if specified.
  *
  * @param loop   if this Music should loop
  * @param volume the volume to play the this Music
  */
 void play(boolean loop, double volume);

 /**
  * Play this Music at the specified volume and pan, and loop if specified.
  *
  * @param loop   if this Music should loop
  * @param volume the volume to play the this Music
  * @param pan    the pan at which to play this Music [-1.0,1.0], values outside
  *               the valid range will be ignored
  */
 void play(boolean loop, double volume, double pan);

 /**
  * Stop playing this Music and set its position to the beginning.
  */
 void stop();

 /**
  * Stop playing this Music and keep its current position.
  */
 void pause();

 /**
  * Play this Music from its current position.
  */
 void resume();

 /**
  * Set this Music's position to the beginning.
  */
 void rewind();

 /**
  * Set this Music's position to the loop position.
  */
 void rewindToLoopPosition();

 /**
  * Determine if this Music is playing.
  *
  * @return true if this Music is playing
  */
 boolean playing();

 /**
  * Determine if this Music has reached its end and is done playing.
  *
  * @return true if this Music is at the end and is done playing
  */
 boolean done();

 /**
  * Determine if this Music will loop.
  *
  * @return true if this Music will loop
  */
 boolean loop();

 /**
  * Set whether this Music will loop.
  *
  * @param loop whether this Music will loop
  */
 void setLoop(boolean loop);

 /**
  * Get the loop position of this Music by sample frame.
  *
  * @return loop position by sample frame
  */
 int getLoopPositionByFrame();

 /**
  * Get the loop position of this Music by seconds.
  *
  * @return loop position by seconds
  */
 double getLoopPositionBySeconds();

 /**
  * Set the loop position of this Music by sample frame.
  *
  * @param frameIndex sample frame loop position to set
  */
 void setLoopPositionByFrame(int frameIndex);

 /**
  * Set the loop position of this Music by seconds.
  *
  * @param seconds loop position to set by seconds
  */
 void setLoopPositionBySeconds(double seconds);

 /**
  * Get the volume of this Music.
  *
  * @return volume of this Music
  */
 double getVolume();

 /**
  * Set the volume of this Music.
  *
  * @param volume the desired volume of this Music
  */
 void setVolume(double volume);

 /**
  * Get the pan of this Music.
  *
  * @return pan of this Music
  */
 double getPan();

 /**
  * Set the pan of this Music.  Must be between -1.0 (full pan left) and 1.0
  * (full pan right).  Values outside the valid range will be ignored.
  *
  * @param pan the desired pan of this Music
  */
 void setPan(double pan);

 /**
  * Unload this Music from the system.  Attempts to use this Music after
  * unloading will result in error.
  */
 void unload();

}
