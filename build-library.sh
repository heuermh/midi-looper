#!/bin/bash

mkdir midilooper
mkdir midilooper/library
cp COPYING midilooper
cp README midilooper
cp -R src midilooper
cp -R examples midilooper
cd src
javac -classpath "../lib/promidi-2.0.jar" midilooper/*.java
jar cvf ../midilooper/library/midilooper.jar midilooper/*.class
cd ..