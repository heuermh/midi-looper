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

/**
 * Wait event.
 */
class Wait implements Event
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
    public String toString()
    {
        return "W " + delay;
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
