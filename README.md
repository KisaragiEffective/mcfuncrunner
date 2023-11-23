# mcfuncrunner
**NOTE: You don't have to use this plugin if you are on 1.12 or later!**

This emulates `/function` command for server on 1.11.2 or earlier version.

## Supported feature
* line comment from first column
* `/gamerule maxCommandChainLength`
  * Excess commands are discarded implicitly. 

## Unsupported features
* legacy comment that begins with `/`
  * Please begin them with `#`
* tagged functions, such as:
  * `/gamerule gameLoopFunciton` (1.12-pre1 or later, and 17w49b or earlier)
  * tagged with `tick` (17w49b, 1.13 or later)
  * tagged with `load` (18w01a, 1.13 or later)
* macro expansion (23w31a, 1.20.2 or later)
* line-continuation (23w31a, 1.20.2 or later)
* `/function ... [if|unless] [selector]` (1.12-pre4 or later)
