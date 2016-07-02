module Scopes where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
          Load (ImmValue 0) regA
        , Store regA (DirAddr 1)
        , Load (ImmValue 1) regA
        , Store regA (DirAddr 2)
        , Jump (Rel 22)
        , Load (ImmValue 0) regA
        , Store regA (DirAddr 3)
        , Jump (Rel 4)
        , Load (ImmValue 3) regA
        , Store regA (DirAddr 4)
        , Jump (Rel 3)
        , Load (DirAddr 3) regA
        , Branch regA (Rel (-4))
        , Jump (Rel 6)
        , Load (ImmValue 0) regA
        , Store regA (DirAddr 4)
        , Load (ImmValue 1) regA
        , Store regA (DirAddr 3)
        , Jump (Rel 7)
        , Load (DirAddr 2) regA
        , Load (ImmValue 1) regB
        , Compute Equal regA regB regA
        , Branch regA (Rel (-8))
        , Load (ImmValue 4) regB
        , Store regB (DirAddr 4)
        , Jump (Rel 5)
        , Load (DirAddr 1) regA
        , Load (ImmValue 0) regB
        , Compute Equal regA regB regA
        , Branch regA (Rel (-24))
        , EndProg
       ]
