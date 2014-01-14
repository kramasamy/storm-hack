# Storm Hackathon

Learn to use Storm!

---

Table of Contents

* <a href="#getting-started">Getting started</a>
* <a href="#maven">Using storm-hack with Maven</a>
* <a href="#intellij-idea">Using storm-hack with IntelliJ IDEA</a>

---

<a name="getting-started"></a>

# Getting started

## Prerequisites

First, you will need a laptop that connect to internet using Wi-FI. In the laptop,
make sure that you have `java` and `git` installed and in your user's `PATH`.  

Second, you will need a Twitter account. If you do not have a Twitter account, please 
create one. From your Twitter account, follow the Storm experts

    @karthikz, @sanjeevrk, @staneja, @challenger_nik, @jason_j, @ankitoshniwal

You will be using Twitter direct messaging capability to get help from them. 

Third, if you need to use Twitter firehose/Tweet stream for your idea, 
create a set of credentials by following the instructions at 

    https://dev.twitter.com/discussions/631

Fourth, partner with someone to form a group. We encourage 2/3 students per group. 

Finally, make sure you have the storm-hack code available on your machine.  Git/GitHub beginners 
may want to use the following command to download the latest storm-hack code and change to the 
new directory that contains the downloaded code.

    $ git clone https://github.com/kramasamy/storm-hack.git && cd storm-hack


## Overview

storm-hack contains a variety of examples of using Storm.  If this is your first time working 
with Storm, check out these topologies first:

1. [ExclamationTopology](src/jvm/storm/starter/ExclamationTopology.java):  Basic topology written in all Java
2. [WordCountTopology](src/jvm/storm/starter/WordCountTopology.java):  Basic topology for counting words all written in Java

After you have familiarized yourself with these topologies, take a look at the other topopologies in
[src/jvm/storm/starter/](src/jvm/storm/starter/) such as [RollingTopWords](src/jvm/storm/starter/RollingTopWords.java)
for more advanced implementations.

If you want to learn more about how Storm works, please head over to the
[Storm project page](http://github.com/nathanmarz/storm).

<a name="maven"></a>

# Using storm-hack with Maven

## Install Maven

[Maven](http://maven.apache.org/) is an alternative to Leiningen.  Install Maven (preferably version 3.x) by following
the [Maven installation instructions](http://maven.apache.org/download.cgi).


## Running topologies with Maven

storm-hack contains [m2-pom.xml](m2-pom.xml) which can be used with Maven using the `-f` option. For example, to
compile and run `WordCountTopology` in local mode, use the command:

    $ mvn -f m2-pom.xml compile exec:java -Dstorm.topology=storm.starter.WordCountTopology

You can also run clojure topologies with Maven:

    $ mvn -f m2-pom.xml compile exec:java -Dstorm.topology=storm.starter.clj.word_count

## Packaging storm-hack for use on a Storm cluster

You can package a jar suitable for submitting to a Storm cluster with the command:

    $ mvn -f m2-pom.xml package

This will package your code and all the non-Storm dependencies into a single "uberjar" at the path
`target/storm-hack-{version}-jar-with-dependencies.jar`.


## Running unit tests

Use the following Maven command to run the unit tests that ship with storm-hack.  

    $ mvn -f m2-pom.xml test


<a name="intellij-idea"></a>

# Using storm-hack with IntelliJ IDEA

## Importing storm-hack as a project in IDEA

The following instructions will import storm-hack as a new project in IntelliJ IDEA.

* Copy `m2-pom.xml` to `pom.xml`.  This is requried so that IDEA (or Eclipse) can properly detect the maven
  configuration.
* Open _File > Import Project..._ and navigate to the top-level directory of your storm-hack clone (e.g.
  `~/git/storm-hack`).
* Select _Import project from external model_, select "Maven", and click _Next_.
* In the following screen, enable the checkbox _Import Maven projects automatically_.  Leave all other values at their
  defaults.  Click _Next_.
* Click _Next_ on the following screen about selecting Maven projects to import.
* Select the JDK to be used by IDEA for storm-hack, then click _Next_.
    * At the time of this writing you should use JDK 6.
    * It is strongly recommended to use Sun/Oracle JDK 6 rather than OpenJDK 6.
* You may now optionally change the name of the project in IDEA.  The default name suggested by IDEA is "storm-hack".
  Click _Finish_ once you are done.
