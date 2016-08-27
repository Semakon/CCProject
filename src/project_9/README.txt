AtlantisCompiler README-file
==================================================================
How to use:

To use the AtlantisCompiler, an Atlantis file should be added 
to the /src/project_9/samplePrograms directory. The filename
(without the .atl extension) should then be written in the
program arguments of src/project_9/AtlantisCompiler.java. After 
this is done, the AtlantisCompiler.java's main method can be run 
to produce a Haskell file. This file will be generated in the
/src/project_9/haskellPrograms directory. This file can then
be moved to a different location if desired, but the following
Haskell files should be in the same directory as the generated
file in order for it to compile.

- BasicFunctions.hs
- HardwareTypes.hs
- Sprockell.hs
- System.hs
- Simulation.hs

If these are all present, starting GHCI and loading the generated
file should compile. To test the program simply input:

sysTest [prog]

==================================================================
