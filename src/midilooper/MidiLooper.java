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

import java.util.Stack;

import rwmidi.MidiInput;
import rwmidi.MidiOutput;

/**
 * MIDI looper.
 */
public final class MidiLooper
{
    /** MIDI input. */
    private final MidiInput input;

    /** MIDI output. */
    private final MidiOutput output;

    /** Output channel. */
    private final int outputChannel;

    /** Stack of loops. */
    private final Stack<Loop> loops = new Stack<Loop>();

    /** Undo stack of loops. */
    private final Stack<Loop> undo = new Stack<Loop>();


    /**
     * Create a new MIDI looper with the specified input and output.
     *
     * @param input MIDI input, must not be null
     * @param output MIDI output, must not be null
     * @param outputChannel output channel
     */
    public MidiLooper(final MidiInput input, final MidiOutput output, final int outputChannel)
    {
        if (input == null)
        {
            throw new IllegalArgumentException("input must not be null");
        }
        if (output == null)
        {
            throw new IllegalArgumentException("output must not be null");
        }
        this.input = input;
        this.output = output;
        this.outputChannel = outputChannel;
    }


    /**
     * Start recording a new loop or stop recording and start playing the current
     * loop if the current loop is recording.
     */
    public void record()
    {
        if (loops.empty())
        {
            start();
        }
        else
        {
            Loop current = loops.peek();
            if (current.isRecording())
            {
                stop();
            }
            else
            {
                start();
            }
        }
    }

    /**
     * Stop recording the current loop, start playing it, and start recording a new loop.
     */
    public void overdub()
    {
        stop();
        start();
    }

    /**
     * Undo the current loop, unless it is recording.
     */
    public void undo()
    {
        if (!loops.empty())
        {
            Loop current = loops.peek();
            if (current.isPlaying())
            {
                loops.pop();
                current.stop();
            }
            undo.push(current);
        }
    }

    /**
     * Redo the last undo operation, unless the current loop is recording.
     */
    public void redo()
    {
        if (loops.empty())
        {
            if (!undo.empty())
            {
                Loop last = undo.pop();
                last.play();
                loops.push(last);
            }
        }
        else
        {
            Loop current = loops.peek();
            if (!current.isRecording())
            {
                if (!undo.empty())
                {
                    Loop last = undo.pop();
                    last.play();
                    loops.push(last);
                }
            }
        }
    }

    /**
     * Return the number of loops in the loop stack.
     *
     * @return the number of loops in the loop stack
     */
    public int getLoopCount()
    {
        return loops.size();
    }

    /**
     * Return the number of loops in the undo stack.
     *
     * @return the number of loops in the undo stack
     */
    public int getUndoCount()
    {
        return undo.size();
    }

    /**
     * Start recording a new loop.
     */
    private void start()
    {
        loops.push(new Loop(input, output, outputChannel));
    }

    /**
     * Stop recording the current loop and start playing it.
     */
    private void stop()
    {
        if (!loops.empty())
        {
            Loop current = loops.peek();
            current.stop();
            current.play();
        }
    }
}
