#!/bin/bash

mkdir midilooper
mkdir midilooper/library
cp COPYING midilooper
cp README midilooper
cp -R src midilooper
cp -R examples midilooper
cd src
javac -classpath "../lib/rwmidi-0.1c.jar" midilooper/*.java
jar cvf ../midilooper/library/midilooper.jar midilooper/*.class
cd ..