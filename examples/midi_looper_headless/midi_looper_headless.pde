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
String inputDeviceName = "USB Audio Device Unknown vendor";
String outputDeviceName = "Out To MIDI Yoke:  1 Unknown vendor";
int outputChannel = 0;

// midi notes to looper commands
int record = 51;
int overdub = 44;
int undo = 49;
int redo = 46;

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
  input = RWMidi.getInputDevice(inputDeviceName).createInput(this);
  output = RWMidi.getOutputDevice(outputDeviceName).createOutput();
  looper = new MidiLooper(input, output, outputChannel);
}

void draw()
{
  // empty
}

void record()
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

void overdub()
{
  looper.overdub();
  println("overdub loop " + looper.getLoopCount());    
}

void undo()
{
  looper.undo();
  println("undo " + looper.getUndoCount() + ", " + looper.getLoopCount() + " loops remaining");
}

void redo()
{
  looper.redo();
  println("redo " + looper.getLoopCount() + ", " + looper.getUndoCount() + " undos remaining");
}

void noteOnReceived(final Note note)
{
  if (record == note.getPitch())
  {
    record();
  }
  else if (overdub == note.getPitch())
  {
    overdub();
  }
  else if (undo == note.getPitch())
  {
    undo();
  }
  else if (redo == note.getPitch())
  {
    redo();
  }
  else
  {
    println("ON " + note.getPitch() + " " + note.getVelocity());
    output.sendNoteOn(outputChannel, note.getPitch(), note.getVelocity());  
  }
}

void noteOffReceived(final Note note)
{
  if ((record == note.getPitch()) ||
      (overdub == note.getPitch()) ||
      (undo == note.getPitch()) ||
      (redo == note.getPitch()))
  {
    // empty
  }
  else
  {
    println("OFF " + note.getPitch() + " " + note.getVelocity());
    output.sendNoteOff(outputChannel, note.getPitch(), note.getVelocity());  
  }
}

void programChangeReceived(final ProgramChange programChange)
{
  println("PC " + programChange.getNumber());
  output.sendProgramChange(programChange.getNumber());
}

void controllerChangeReceived(final Controller controller)
{
  println("C " + controller.getCC() + " " + controller.getValue());
  output.sendController(outputChannel, controller.getCC(), controller.getValue());
}

void sysexReceived(final SysexMessage sysexMessage)
{
  println("S " + String.valueOf(sysexMessage.getMessage()));
  output.sendSysex(sysexMessage.getMessage());
}
