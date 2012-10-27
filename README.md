# coffeesim

A small little project for some purpose. There's a PDF *somewhere* around here that might indicate what this is all about.

## Usage

The intention is for this to be run as a commandline application. As such if checking out this project for the first time, run `lein uberjar` to create a standalone jar.

Once you have that you can perform of the following commands:

* `java -jar coffeesim-0.1.0-SNAPSHOT-standalone.jar parse Organic Fair Trade Decaf Longberry Kenyan`

where the domain of words that can be parsed is specified by the below mentioned python script

or

* `java -jar coffeesim-0.1.0-SNAPSHOT-standalone.jar summarize <absolute-path-to-the-file>`

In order to generate some fake data for a file to test against or some descriptions to parse, in the `test/data` directory, the `coffee_gen.py` script will generate a sample set of data.

* `python coffee_gen.py > sample.tsv`



## License

Copyright © 2012 Alex Bahouth

Distributed under the Eclipse Public License, the same as Clojure.
