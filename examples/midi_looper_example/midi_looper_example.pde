/*

    MIDI looper library for Processing.
    Copyright (c) 2010 held jointly by the individual authors.

    This file is part of MIDI looper library for Processing.

    MIDI looper library for Processing is free software: you can redistribute it and/or
    modify it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    MIDI looper library for Processing is distributed in the hope that it will be
    useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MIDI looper library for Processing.  If not, see
    <http://www.gnu.org/licenses/>.

*/
import rwmidi.*;
import midilooper.*;

MidiInput input;
MidiOutput output;
MidiLooper looper;

// set these as appropriate
int inputDevice = 0;
int outputDevice = 0;
int outputChannel = 0;

void setup()
{
  println("Input devices:");
  for (String inputDevice : RWMidi.getInputDeviceNames())
  {
    println("  " + inputDevice);
  }
  println("Output devices:");
  for (String outputDevice : RWMidi.getOutputDeviceNames())
  {
    println("  " + outputDevice);
  }

  input = RWMidi.getInputDevices()[inputDevice].createInput(this);
  output = RWMidi.getOutputDevices()[outputDevice].createOutput();
  looper = new MidiLooper(input, output, outputChannel);
}

void noteOnReceived(final Note note)
{
  println("note on " + note.getPitch() + " " + note.getVelocity());
}

void noteOffReceived(final Note note)
{
  println("note off " + note.getPitch() + " " + note.getVelocity());
}

void draw()
{
  // empty
}

void keyPressed()
{
  if (key == 'u' || key == 'U')
  {
    looper.undo();
    println("undo " + looper.getUndoCount() + ", " + looper.getLoopCount() + " loops remaining");
  }
  else if (key == 'r' || key == 'R')
  {
    looper.redo();
    println("redo " + looper.getLoopCount() + ", " + looper.getUndoCount() + " undos remaining");
  }
}

void mousePressed()
{
  if (mouseButton == LEFT)
  {
    int loopCountBefore = looper.getLoopCount();
    looper.record();
    int loopCountAfter = looper.getLoopCount();
    if (loopCountAfter == loopCountBefore)
    {
      print("stop ");
    }
    else
    {
      print("start ");
    }
    println("recording loop " + loopCountAfter);
  }
  else if (mouseButton == RIGHT)
  {
    looper.overdub();
    println("overdub loop " + looper.getLoopCount());    
  }
}

