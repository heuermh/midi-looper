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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rwmidi.Controller;
import rwmidi.MidiInput;
import rwmidi.MidiOutput;
import rwmidi.Note;
import rwmidi.ProgramChange;
import rwmidi.SysexMessage;

/**
 * Loop.
 */
public class Loop implements Runnable
{
    /** MIDI input. */
    private final MidiInput input;

    /** MIDI output. */
    private final MidiOutput output;

    /** Output channel. */
    private final int outputChannel;

    /** Last timestamp, in milliseconds. */
    private long last;

    /** True if this loop is playing. */
    private boolean playing;

    /** True if this loop is recording. */
    private boolean recording;

    /** Pending completion of loop task. */
    private ScheduledFuture<?> future;

    /** Executor. */
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /** List of events. */
    private final List<Event> events = new ArrayList<Event>();


    /**
     * Create a new loop and start recording.
     *
     * @param input MIDI input, must not be null
     * @param output MIDI output, must not be null
     * @param outputChannel output channel
     */
    public Loop(final MidiInput input, final MidiOutput output, final int outputChannel)
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

        last = System.currentTimeMillis();
        playing = false;
        recording = true;
        input.plug(this);
    }


    /**
     * Stop this loop.
     */
    void stop()
    {
        if (playing)
        {
            playing = false;
            System.out.println(this.hashCode() + " cancelling loop task...");
            future.cancel(true);
        }
        if (recording)
        {
            recording = false;
            // unplug input?
        }
    }

    /**
     * Play this loop.
     */
    void play()
    {
        playing = true;
        System.out.println(this.hashCode() + " scheduling loop task...");
        System.out.println(this.hashCode() + "    " + events);
        future = executor.scheduleWithFixedDelay(this, 1L, 1L, TimeUnit.NANOSECONDS);
    }

    /**
     * Return true if this loop is playing.
     *
     * @return true if this loop is playing
     */
    boolean isPlaying()
    {
        return playing;
    }

    /**
     * Return true if this loop is recording.
     *
     * @return true if this loop is recording
     */
    boolean isRecording()
    {
        return recording;
    }

    /** {@inheritDoc} */
    public void run()
    {
        for (Event event : events)
        {
            System.out.println(this.hashCode() + " " + event.toString());
            event.run();
        }
    }

    /**
     * Note on callback.
     *
     * @param note note
     */
    public void noteOnReceived(final Note note)
    {
        System.out.println("heard note on, recording=" + recording);
        if (recording)
        {
            long current = System.currentTimeMillis();
            if ((current - last) > 0)
            {
                events.add(new Wait(current - last));
            }
            events.add(new NoteOnEvent(note, output, outputChannel));
            last = current;
        }
    }

    /**
     * Note off callback.
     *
     * @param note note
     */
    public void noteOffReceived(final Note note)
    {
        System.out.println("heard note off, recording=" + recording);
        if (recording)
        {
            long current = System.currentTimeMillis();
            if ((current - last) > 0)
            {
                events.add(new Wait(current - last));
            }
            events.add(new NoteOffEvent(note, output, outputChannel));
            last = current;
        }
    }

    /**
     * Controller change callback.
     *
     * @param controller controller
     */
    public void controllerChangeReceived(final Controller controller)
    {
        System.out.println("heard controller, recording=" + recording);
        if (recording)
        {
            long current = System.currentTimeMillis();
            if ((current - last) > 0)
            {
                events.add(new Wait(current - last));
            }
            events.add(new ControllerEvent(controller, output, outputChannel));
            last = current;
        }
    }

    /**
     * Program change callback.
     *
     * @param programChange program change
     */
    public void programChangeReceived(final ProgramChange programChange)
    {
        System.out.println("heard programChange, recording=" + recording);
        if (recording)
        {
            long current = System.currentTimeMillis();
            if ((current - last) > 0)
            {
                events.add(new Wait(current - last));
            }
            events.add(new ProgramChangeEvent(programChange, output, outputChannel));
            last = current;
        }
    }

    /**
     * Sysex callback.
     *
     * @param sysexMessage sysex message
     */
    public void sysexReceived(final SysexMessage sysexMessage)
    {
        System.out.println("heard sysex, recording=" + recording);
        if (recording)
        {
            long current = System.currentTimeMillis();
            if ((current - last) > 0)
            {
                events.add(new Wait(current - last));
            }
            events.add(new SysexEvent(sysexMessage, output, outputChannel));
            last = current;
        }
    }
}
