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
import java.util.concurrent.TimeUnit;

import promidi.Controller;
import promidi.Note;
import promidi.ProgramChange;

/**
 * MIDI looper.
 */
public final class MidiLooper
{
    /** Stack of loops. */
    private final Stack<Loop> loops = new Stack<Loop>();

    /** Undo stack of loops. */
    private final Stack<Loop> undo = new Stack<Loop>();

    // midi in, midi out


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
        loops.push(new Loop());
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

    /**
     * Loop.
     */
    private class Loop implements Runnable
    {
        /** Last timestamp, in milliseconds. */
        private long last;

        /** True if this loop is playing. */
        private boolean playing;

        /** True if this loop is recording. */
        private boolean recording;

        /** Executor. */
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        /** List of events. */
        private final List<Event> events = new ArrayList<Event>();


        /**
         * Create a new loop and start recording.
         */
        Loop()
        {
            last = System.currentTimeMillis();
            playing = false;
            recording = true;
            // add midi in listener
        }


        /**
         * Stop this loop.
         */
        void stop()
        {
            if (playing)
            {
                playing = false;
                executor.shutdownNow();
            }
            if (recording)
            {
                recording = false;
                // remove midi in listener
            }
        }

        /**
         * Play this loop.
         */
        void play()
        {
            playing = true;
            // try ... catch RejectedExecutionException?
            executor.scheduleWithFixedDelay(this, 1L, 1L, TimeUnit.NANOSECONDS);
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
                event.run();
            }
        }

        // midi in listener; record note ons, note offs, controller (in)s, and program changes
    }

    /**
     * Event.
     */
    private interface Event extends Runnable
    {
        // empty
    }

    /**
     * Wait event.
     */
    private class Wait implements Event
    {
        /** Delay in milliseconds. */
        private final long delay;


        /**
         * Create a new wait event with the specified delay in millieseconds.
         *
         * @param delay delay in milliseconds
         */
        Wait(final long delay)
        {
            this.delay = delay;
        }


        /** {@inheritDoc} */
        public void run() 
        {
            try
            {
                Thread.currentThread().sleep(delay);
            }
            catch (InterruptedException e)
            {
                // ignore
            }
        }
    }

    /**
     * Note on event.
     */
    private class NoteOnEvent implements Event
    {
        /** Note for this note on event. */
        private final Note note;


        /**
         * Create a new note on event for the specified note.
         *
         * @param note note for this note on event
         */
        NoteOnEvent(final Note note)
        {
            this.note = note;
        }


        /** {@inheritDoc} */
        public void run()
        {
            // send note on
        }
    }

    /**
     * Note off event.
     */
    private class NoteOffEvent implements Event
    {
        /** Note for this note off event. */
        private final Note note;


        /**
         * Create a new note off event for the specified note.
         *
         * @param note note for this note off event
         */
        NoteOffEvent(final Note note)
        {
            this.note = note;
        }


        /** {@inheritDoc} */
        public void run()
        {
            // send note off
        }
    }

    /**
     * Controller event.
     */
    private class ControllerEvent implements Event
    {
        /** Controller for this controller event. */
        private final Controller controller;


        /**
         * Create a new controller event for the specified controller.
         *
         * @param controller controller for this controller event
         */
        ControllerEvent(final Controller controller)
        {
            this.controller = controller;
        }


        /** {@inheritDoc} */
        public void run()
        {
            // send controller
        }
    }

    /**
     * Program change event.
     */
    private class ProgramChangeEvent implements Event
    {
        /** Program change for this program change event. */
        private final ProgramChange programChange;


        /**
         * Create a new programChange event for the specified programChange.
         *
         * @param programChange program change for this program change event
         */
        ProgramChangeEvent(final ProgramChange programChange)
        {
            this.programChange = programChange;
        }


        /** {@inheritDoc} */
        public void run()
        {
            // send program change
        }
    }
}