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
package kuusisto.tinysound.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

/**
 * The StreamMusic class is an implementation of the Music interface that
 * streams audio data from a temporary file to reduce memory overhead.
 *
 * @author Finn Kuusisto
 */
public class StreamMusic implements Music {

 private URL dataURL;
 private Mixer mixer;
 private MusicReference reference;

 /**
  * Construct a new StreamMusic with the given data and the Mixer with which
  * to register this StreamMusic.
  *
  * @param dataURL            URL of the temporary file containing audio data
  * @param numBytesPerChannel the total number of bytes for each channel in
  *                           the file
  * @param mixer              Mixer that will handle this StreamSound
  * @throws IOException if a stream cannot be opened from the URL
  */
 public StreamMusic(URL dataURL, long numBytesPerChannel, Mixer mixer)
 throws IOException {
  this.dataURL = dataURL;
  this.mixer = mixer;
  reference = new StreamMusicReference(this.dataURL, false, false, 0,
  0, numBytesPerChannel, 1.0, 0.0);
  this.mixer.registerMusicReference(reference);
 }

 /**
  * Play this StreamMusic and loop if specified.
  *
  * @param loop if this StreamMusic should loop
  */
 @Override
 public void play(boolean loop) {
  reference.setLoop(loop);
  reference.setPlaying(true);
 }

 /**
  * Play this StreamMusic at the specified volume and loop if specified.
  *
  * @param loop   if this StreamMusic should loop
  * @param volume the volume to play the this StreamMusic
  */
 @Override
 public void play(boolean loop, double volume) {
  setLoop(loop);
  setVolume(volume);
  reference.setPlaying(true);
 }

 /**
  * Play this StreamMusic at the specified volume and pan, and loop if
  * specified.
  *
  * @param loop   if this StreamMusic should loop
  * @param volume the volume to play the this StreamMusic
  * @param pan    the pan at which to play this StreamMusic [-1.0,1.0], values
  *               outside the valid range will be ignored
  */
 @Override
 public void play(boolean loop, double volume, double pan) {
  setLoop(loop);
  setVolume(volume);
  setPan(pan);
  reference.setPlaying(true);
 }

 /**
  * Stop playing this StreamMusic and set its position to the beginning.
  */
 @Override
 public void stop() {
  reference.setPlaying(false);
  rewind();
 }

 /**
  * Stop playing this StreamMusic and keep its current position.
  */
 @Override
 public void pause() {
  reference.setPlaying(false);
 }

 /**
  * Play this StreamMusic from its current position.
  */
 @Override
 public void resume() {
  reference.setPlaying(true);
 }

 /**
  * Set this StreamMusic's position to the beginning.
  */
 @Override
 public void rewind() {
  reference.setPosition(0);
 }

 /**
  * Set this StreamMusic's position to the loop position.
  */
 @Override
 public void rewindToLoopPosition() {
  long byteIndex = reference.getLoopPosition();
  reference.setPosition(byteIndex);
 }

 /**
  * Determine if this StreamMusic is playing.
  *
  * @return true if this StreamMusic is playing
  */
 @Override
 public boolean playing() {
  return reference.getPlaying();
 }

 /**
  * Determine if this StreamMusic has reached its end and is done playing.
  *
  * @return true if this StreamMusic has reached the end and is done playing
  */
 @Override
 public boolean done() {
  return reference.done();
 }

 /**
  * Determine if this StreamMusic will loop.
  *
  * @return true if this StreamMusic will loop
  */
 @Override
 public boolean loop() {
  return reference.getLoop();
 }

 /**
  * Set whether this StreamMusic will loop.
  *
  * @param loop whether this StreamMusic will loop
  */
 @Override
 public void setLoop(boolean loop) {
  reference.setLoop(loop);
 }

 /**
  * Get the loop position of this StreamMusic by sample frame.
  *
  * @return loop position by sample frame
  */
 @Override
 public int getLoopPositionByFrame() {
  int bytesPerChannelForFrame = TinySound.FORMAT.getFrameSize() /
  TinySound.FORMAT.getChannels();
  long byteIndex = reference.getLoopPosition();
  return (int) (byteIndex / bytesPerChannelForFrame);
 }

 /**
  * Get the loop position of this StreamMusic by seconds.
  *
  * @return loop position by seconds
  */
 @Override
 public double getLoopPositionBySeconds() {
  int bytesPerChannelForFrame = TinySound.FORMAT.getFrameSize() /
  TinySound.FORMAT.getChannels();
  long byteIndex = reference.getLoopPosition();
  return (byteIndex / (TinySound.FORMAT.getFrameRate() *
  bytesPerChannelForFrame));
 }

 /**
  * Set the loop position of this StreamMusic by sample frame.
  *
  * @param frameIndex sample frame loop position to set
  */
 @Override
 public void setLoopPositionByFrame(int frameIndex) {
  //get the byte index for a channel
  int bytesPerChannelForFrame = TinySound.FORMAT.getFrameSize() /
  TinySound.FORMAT.getChannels();
  long byteIndex = frameIndex * (long) bytesPerChannelForFrame;
  reference.setLoopPosition(byteIndex);
 }

 /**
  * Set the loop position of this StreamMusic by seconds.
  *
  * @param seconds loop position to set by seconds
  */
 @Override
 public void setLoopPositionBySeconds(double seconds) {
  //get the byte index for a channel
  int bytesPerChannelForFrame = TinySound.FORMAT.getFrameSize() /
  TinySound.FORMAT.getChannels();
  long byteIndex = (long) (seconds * TinySound.FORMAT.getFrameRate()) *
  bytesPerChannelForFrame;
  reference.setLoopPosition(byteIndex);
 }

 /**
  * Get the volume of this StreamMusic.
  *
  * @return volume of this StreamMusic
  */
 @Override
 public double getVolume() {
  return reference.getVolume();
 }

 /**
  * Set the volume of this StreamMusic.
  *
  * @param volume the desired volume of this StreamMusic
  */
 @Override
 public void setVolume(double volume) {
  if (volume >= 0.0) {
   reference.setVolume(volume);
  }
 }

 /**
  * Get the pan of this StreamMusic.
  *
  * @return pan of this StreamMusic
  */
 @Override
 public double getPan() {
  return reference.getPan();
 }

 /**
  * Set the pan of this StreamMusic.  Must be between -1.0 (full pan left)
  * and 1.0 (full pan right).  Values outside the valid range will be ignored
  * .
  *
  * @param pan the desired pan of this StreamMusic
  */
 @Override
 public void setPan(double pan) {
  if (pan >= -1.0 && pan <= 1.0) {
   reference.setPan(pan);
  }
 }

 /**
  * Unload this MemMusic from the system.  Attempts to use this MemMusic
  * after unloading will result in error.
  */
 @Override
 public void unload() {
  //unregister the reference
  mixer.unRegisterMusicReference(reference);
  reference.dispose();
  mixer = null;
  dataURL = null;
  reference = null;
 }

 /////////////
 //Reference//
 /////////////

 /**
  * The StreamMusicReference is an implementation of the MusicReference
  * interface.
  */
 private static class StreamMusicReference implements MusicReference {

  private URL url;
  private InputStream data;
  private final long numBytesPerChannel; //not per frame, but the whole sound
  private final byte[] buf;
  private final byte[] skipBuf;
  private boolean playing;
  private boolean loop;
  private long loopPosition;
  private long position;
  private double volume;
  private double pan;

  /**
   * Constructs a new StreamMusicReference with the given audio data and
   * settings.
   *
   * @param dataURL            URL of the temporary file containing audio data
   * @param playing            true if the music should be playing
   * @param loop               true if the music should loop
   * @param loopPosition       byte index of the loop position in music data
   * @param position           byte index position in music data
   * @param numBytesPerChannel the total number of bytes for each channel
   *                           in the file
   * @param volume             volume to play the music
   * @param pan                pan to play the music
   * @throws IOException if a stream cannot be opened from the URL
   */
  StreamMusicReference(URL dataURL, boolean playing, boolean loop,
                       long loopPosition, long position, long numBytesPerChannel,
                       double volume, double pan) throws IOException {
   url = dataURL;
   this.playing = playing;
   this.loop = loop;
   this.loopPosition = loopPosition;
   this.position = position;
   this.numBytesPerChannel = numBytesPerChannel;
   this.volume = volume;
   this.pan = pan;
   buf = new byte[4];
   skipBuf = new byte[50];
   //now get the data stream
   data = url.openStream();
  }

  /**
   * Get the playing setting of this StreamMusicReference.
   *
   * @return true if this StreamMusicReference is set to play
   */
  @Override
  public synchronized boolean getPlaying() {
   return playing;
  }

  /**
   * Get the loop setting of this StreamMusicReference.
   *
   * @return true if this StreamMusicReference is set to loop
   */
  @Override
  public synchronized boolean getLoop() {
   return loop;
  }

  /**
   * Get the byte index of this StreamMusicReference.
   *
   * @return byte index of this StreamMusicReference
   */
  @Override
  public synchronized long getPosition() {
   return position;
  }

  /**
   * Get the loop-position byte index of this StreamMusicReference.
   *
   * @return loop-position byte index of this StreamMusicReference
   */
  @Override
  public synchronized long getLoopPosition() {
   return loopPosition;
  }

  /**
   * Get the volume of this StreamMusicReference.
   *
   * @return volume of this StreamMusicReference
   */
  @Override
  public synchronized double getVolume() {
   return volume;
  }

  /**
   * Get the pan of this StreamMusicReference.
   *
   * @return pan of this StreamMusicReference
   */
  @Override
  public synchronized double getPan() {
   return pan;
  }

  /**
   * Set whether this StreamMusicReference is playing.
   *
   * @param playing whether this StreamMusicReference is playing
   */
  @Override
  public synchronized void setPlaying(boolean playing) {
   this.playing = playing;
  }

  /**
   * Set whether this StreamMusicReference will loop.
   *
   * @param loop whether this StreamMusicReference will loop
   */
  @Override
  public synchronized void setLoop(boolean loop) {
   this.loop = loop;
  }

  /**
   * Set the byte index of this StreamMusicReference.
   *
   * @param position the byte index to set
   */
  @Override
  public synchronized void setPosition(long position) {
   if (position >= 0 && position < numBytesPerChannel) {
    //if it's later, skip
    if (position >= this.position) {
     skipBytes(position - this.position);
    } else { //otherwise skip from the beginning
     //first close our current stream
     try {
      data.close();
     } catch (IOException e) {
      //whatever...
     }
     //open a new stream
     try {
      data = url.openStream();
      this.position = 0;
      skipBytes(position);
     } catch (IOException e) {
      System.err.println("Failed to open stream for StreamMusic");
      playing = false;
     }
    }
   }
  }

  /**
   * Set the loop-position byte index of this StreamMusicReference.
   *
   * @param loopPosition the loop-position byte index to set
   */
  @Override
  public synchronized void setLoopPosition(long loopPosition) {
   if (loopPosition >= 0 && loopPosition < numBytesPerChannel) {
    this.loopPosition = loopPosition;
   }
  }

  /**
   * Set the volume of this StreamMusicReference.
   *
   * @param volume the desired volume of this StreamMusicReference
   */
  @Override
  public synchronized void setVolume(double volume) {
   this.volume = volume;
  }

  /**
   * Set the pan of this StreamMusicReference.  Must be between -1.0 (full
   * pan left) and 1.0 (full pan right).
   *
   * @param pan the desired pan of this StreamMusicReference
   */
  @Override
  public synchronized void setPan(double pan) {
   this.pan = pan;
  }

  /**
   * Get the number of bytes remaining for each channel until the end of
   * this StreamMusicReference.
   *
   * @return number of bytes remaining for each channel
   */
  @Override
  public synchronized long bytesAvailable() {
   return numBytesPerChannel - position;
  }

  /**
   * Determine if there are no bytes remaining and play has stopped.
   *
   * @return true if there are no bytes remaining and the reference is no
   * longer playing
   */
  @Override
  public synchronized boolean done() {
   long available = numBytesPerChannel - position;
   return available <= 0 && !playing;
  }

  /**
   * Skip a specified number of bytes of the audio data.
   *
   * @param num number of bytes to skip
   */
  @Override
  public synchronized void skipBytes(long num) {
   if ((position + num) >= numBytesPerChannel) {
    //if we're not looping, nothing special needs to happen
    if (loop) {
     //compute the next position
     long loopLength = numBytesPerChannel -
     loopPosition;
     long bytesOver = (position + num) -
     numBytesPerChannel;
     long nextPosition = loopPosition +
     (bytesOver % loopLength);
     //and set us there
     setPosition(nextPosition);
    } else {
     position += num;
     //now stop since we're out
     playing = false;
    }
    return;
   }
   //this is the number of bytes to skip per channel, so double it
   long numSkip = num << 1;
   //spin read since skip is not always supported apparently and won't
   //guarantee a correct skip amount
   int tmpRead = 0;
   int numRead = 0;
   try {
    while (numRead < numSkip && tmpRead != -1) {
     //determine safe length to read
     long remaining = numSkip - numRead;
     int len = remaining > skipBuf.length ?
     skipBuf.length : (int) remaining;
     //and read
     tmpRead = data.read(skipBuf, 0, len);
     numRead += tmpRead;
    }
   } catch (IOException e) {
    //hmm... I guess invalidate this reference
    position = numBytesPerChannel;
    playing = false;
   }
   //increment the position appropriately
   if (tmpRead == -1) { //reached end of file in the middle of reading
    position = numBytesPerChannel;
    playing = false;
   } else {
    position += num;
   }
  }

  /**
   * Get the next two bytes from the music data in the specified
   * endianness.
   *
   * @param data      length-2 array to write in next two bytes from each
   *                  channel
   * @param bigEndian true if the bytes should be read big-endian
   */
  @Override
  public synchronized void nextTwoBytes(int[] data, boolean bigEndian) {
   int tmpRead = 0;
   int numRead = 0;
   try {
    while (numRead < buf.length && tmpRead != -1) {
     tmpRead = this.data.read(buf, numRead,
     buf.length - numRead);
     numRead += tmpRead;
    }
   } catch (IOException e) {
    //this shouldn't happen if the bytes were written correctly to
    //the temp file, but this sound should now be invalid at least
    position = numBytesPerChannel;
    System.err.println("Failed reading bytes for stream music");
   }
   //copy the values into the caller buffer
   if (bigEndian) {
    //left
    data[0] = ((buf[0] << 8) |
    (buf[1] & 0xFF));
    //right
    data[1] = ((buf[2] << 8) |
    (buf[3] & 0xFF));
   } else {
    //left
    data[0] = ((buf[1] << 8) |
    (buf[0] & 0xFF));
    //right
    data[1] = ((buf[3] << 8) |
    (buf[2] & 0xFF));
   }
   //increment the position appropriately
   if (tmpRead == -1) { //reached end of file in the middle of reading
    //this should never happen
    position = numBytesPerChannel;
   } else {
    position += 2;
   }
   //wrap if looping, stop otherwise
   if (position >= numBytesPerChannel) {
    if (loop) {
     setPosition(loopPosition);
    } else {
     playing = false;
    }
   }
  }

  /**
   * Does any cleanup necessary to dispose of resources in use by this
   * StreamMusicReference.
   */
  @Override
  public synchronized void dispose() {
   playing = false;
   position = numBytesPerChannel;
   url = null;
   try {
    data.close();
   } catch (IOException e) {
    //whatever... this should never happen
   }
  }
 }
}