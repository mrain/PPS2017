# PPS 2017 - Escape
## Usage
You need a java environment installed.
First compile the simulator:
```sh
$ javac escape/sim/*.java
```
Then run simulator for fun! For example:
```sh
java escape.sim.Simulator -p random random random --gui --fps 1
```
| Parameters | Meanings |
| ------ | ------ |
| `-p/--players player0 player1 ...` | Specifying the players |
| `-g/--gui` | Enable GUI |
| `--fps [float number]` | Set fps |
| `-t/--timelimit [integer number]` | Set the total timelimit for each player (in millisecond) |
| `-v/--verbose` | Enable the detailed events log |
| `-l/--log [file]` | Save the detailed events log to file |
