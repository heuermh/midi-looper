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
import rwmidi.ProgramChange;

/**
 * Program change event.
 */
class ProgramChangeEvent implements Event
{
    /** Program change for this program change event. */
    private final ProgramChange programChange;

    /** MIDI output. */
    private final MidiOutput output;

    /** Output channel. */
    private final int outputChannel;


    /**
     * Create a new program change event for the specified program change.
     *
     * @param programChange program change for this program change event
     * @param output MIDI output, must not be null
     * @param outputChannel output channel
     */
    ProgramChangeEvent(final ProgramChange programChange, final MidiOutput output, final int outputChannel)
    {
        if (programChange == null)
        {
            throw new IllegalArgumentException("programChange must not be null");
        }
        if (output == null)
        {
            throw new IllegalArgumentException("output must not be null");
        }
        this.programChange = programChange;
        this.output = output;
        this.outputChannel = outputChannel;
    }


    /** {@inheritDoc} */
    public String toString()
    {
        return "PC " + programChange.getNumber();
    }

    /** {@inheritDoc} */
    public void run()
    {
        output.sendProgramChange(programChange.getNumber());
    }
}
