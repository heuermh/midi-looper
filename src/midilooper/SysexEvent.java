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
package midilooper;

import rwmidi.MidiOutput;
import rwmidi.SysexMessage;

/**
 * Sysex event.
 */
class SysexEvent implements Event
{
    /** Sysex message for this sysex event. */
    private final SysexMessage sysexMessage;

    /** MIDI output. */
    private final MidiOutput output;

    /** Output channel. */
    private final int outputChannel;


    /**
     * Create a new sysex event for the specified sysex message.
     *
     * @param sysexMessage sysex message for this sysex event
     * @param output MIDI output, must not be null
     * @param outputChannel output channel
     */
    SysexEvent(final SysexMessage sysexMessage, final MidiOutput output, final int outputChannel)
    {
        if (sysexMessage == null)
        {
            throw new IllegalArgumentException("sysexMessage must not be null");
        }
        if (output == null)
        {
            throw new IllegalArgumentException("output must not be null");
        }
        this.sysexMessage = sysexMessage;
        this.output = output;
        this.outputChannel = outputChannel;
    }


    /** {@inheritDoc} */
    public String toString()
    {
        return "S " + String.valueOf(sysexMessage.getMessage());
    }

    /** {@inheritDoc} */
    public void run()
    {
        output.sendSysex(sysexMessage.getMessage());
    }
}
