# MINECRAFTD

## Overview

Minecraftd is a very simple daemon intended for knowledgeable linux users. It
is intended for use with minecraft servers, but there's no reason you couldn't
use it with any arbitrary process if you wanted to. Minecraftd works by running
as a daemon process that will spawn your minecraft server as a subprocess. It
will then pipe the server's standard output into a log file inside the server's
working directory. Most importantly, the daemon also hosts a socket connection
which you may connect to with a socket client such as netcat. This connection
allows you to read the server standard output and issue commands via standard
input.

This program serves a similar purpose to existing software such as
(mcrcon)[https://github.com/Tiiffi/mcrcon], with the key differences being as
follows:
- Minecraftd is intended to be used over a local socket connection. Furthermore,
  it is advised that you **disallow incoming connections to minecraftd's port**.
  Otherwise the internet will have unmoderated connection to your entire server.
  This use case eliminates a lot of code and potential security flaws required
  by a program like mcrcon that allows you to securely communicate over the
  internet.
- This program's inherent simplicity means **you can trust it**. The entire
  program is only a few hundred lines long and **does not use any external
  libraries**. Security is simplicity.

Minecraftd was designed to be used in linux environments. I make no guarantees
that building or running this in windows will work. If you are interested in
windows support, please create an issue.

## Building

To build, simply clone this repo and run `./gradlew jar` in the root directory.
The jar will be compiled to build/libs/minecraftd.jar. This project should
support all java versions from jdk8 onwards. If you have issues building any
of these versions, please make an issue.

## Running

To run minecraftd, first set the environment variable "MC_SERVER_WD" to the
working directory of the server.jar of your minecraft server. Then, simply
execute the jar and pass in the command and arguments of the program which you
would like to run. For example:

`java -jar minecraftd.jar java -XmsG2 -Xmx8G -jar server.jar nogui`

A log file with the name "minecraftd-timestamp.log" will be created in the server
working directory, where "timestamp" is the timestamp of the file's creation time.
This log file will log direct server output.

To interface with the server directly, create a socket connection to the port 7788
with a socket client such as netcat. Then you may interface with the server exactly
as if it were running as a foreground process.

**NOTE: minecraftd is intended to be ran as a daemon and will NOT allow you to
interface with the server if ran as a foreground process. You may still run it as
a foreground process, but the only way to communicate with the server is via a
socket connection via another shell instance.**

Minecraftd behaves properly as a systemd unit. Terminating the daemon with
`systemctl stop` will gracefully shut down the server.