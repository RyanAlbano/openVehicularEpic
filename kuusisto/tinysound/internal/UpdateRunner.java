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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.SourceDataLine;

import kuusisto.tinysound.TinySound;
import ve.utilities.sound.Sounds;

/**
 * The UpdateRunner class implements Runnable and is what performs automatic
 * updates of the TinySound system.  UpdateRunner is an internal class of the
 * TinySound system and should be of no real concern to the average user of
 * TinySound.
 *
 * @author Finn Kuusisto
 */
public class UpdateRunner implements Runnable {

 private final AtomicBoolean running;
 private final SourceDataLine outLine;
 private final Mixer mixer;

 /**
  * Constructs a new UpdateRunner to update the TinySound system.
  *
  * @param mixer   the mixer to read audio data from
  * @param outLine the line to write audio data to
  */
 public UpdateRunner(Mixer mixer, SourceDataLine outLine) {
  running = new AtomicBoolean();
  this.mixer = mixer;
  this.outLine = outLine;
 }

 /**
  * Stop this UpdateRunner from updating the TinySound system.
  */
 public void stop() {
  running.set(false);
 }

 @Override
 public void run() {
  //mark the updater as running
  running.set(true);
  //1-sec buffer
  byte[] audioBuffer = new byte[(int) TinySound.FORMAT.getFrameRate() * TinySound.FORMAT.getFrameSize()];
  int numBytesRead = 0;
  double framesAccrued = 0;
  long lastUpdate = System.nanoTime();
  //keep running until told to stop
  while (running.get()) {
   //check the time
   long currTime = System.nanoTime();
   //accrue frames
   double secDelta = ((currTime - lastUpdate) * .000000001);
   framesAccrued += secDelta * TinySound.FORMAT.getFrameRate();
   //read frames if needed
   int framesToRead = (int) framesAccrued;
   int framesToSkip = 0;
   //check if we need to skip frames to catch up
   int maxFramesPerUpdate = (int) ((TinySound.FORMAT.getFrameRate() / 1000.) * Sounds.bufferSize);
   if (framesToRead > maxFramesPerUpdate) {
    framesToSkip = framesToRead - maxFramesPerUpdate;
    framesToRead = maxFramesPerUpdate;
   }
   //skip frames
   if (framesToSkip > 0) {
    mixer.skip(framesToSkip * TinySound.FORMAT.getFrameSize());
   }
   //read frames
   if (framesToRead > 0) {
    //read from the mixer
    int bytesToRead = framesToRead * TinySound.FORMAT.getFrameSize();
    int tmpBytesRead = mixer.read(audioBuffer, numBytesRead, bytesToRead);
    numBytesRead += tmpBytesRead; //mark how many read
    //fill rest with zeroes
    int remaining = bytesToRead - tmpBytesRead;
    for (int i = 0; i < remaining; i++) {
     audioBuffer[numBytesRead + i] = 0;
    }
    numBytesRead += remaining; //mark zeroes read
   }
   //mark frames read and skipped
   framesAccrued -= (framesToRead + framesToSkip);
   //write to speakers
   if (numBytesRead > 0) {
    outLine.write(audioBuffer, 0, numBytesRead);
    numBytesRead = 0;
   }
   //mark last update
   lastUpdate = currTime;
   //give the CPU back to the OS for a bit
   try {
    Thread.sleep(1);
   } catch (InterruptedException ignored) {
   }
  }
 }
}