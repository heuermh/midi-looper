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

import rwmidi.Controller;
import rwmidi.MidiInput;
import rwmidi.MidiOutput;

/**
 * Controller event.
 */
class ControllerEvent implements Event
{
    /** Controller for this controller event. */
    private final Controller controller;

    /** MIDI output. */
    private final MidiOutput output;

    /** Output channel. */
    private final int outputChannel;


    /**
     * Create a new controller event for the specified controller.
     *
     * @param controller controller for this controller event
     * @param output MIDI output, must not be null
     * @param outputChannel output channel
     */
    ControllerEvent(final Controller controller, final MidiOutput output, final int outputChannel)
    {
        if (controller == null)
        {
            throw new IllegalArgumentException("controller must not be null");
        }
        if (output == null)
        {
            throw new IllegalArgumentException("output must not be null");
        }
        this.controller = controller;
        this.output = output;
        this.outputChannel = outputChannel;
    }


    /** {@inheritDoc} */
    public String toString()
    {
        return "C " + controller.getCC() + " " + controller.getValue();
    }

    /** {@inheritDoc} */
    public void run()
    {
        output.sendController(outputChannel, controller.getCC(), controller.getValue());
    }
}

