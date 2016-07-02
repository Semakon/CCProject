module IfStatement where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
          Load (ImmValue 1) regA
        , Store regA (DirAddr 1)
        , Load (ImmValue 2) regA
        , Store regA (DirAddr 2)
        , Jump (Rel 4)
        , Load (ImmValue 3) regA
        , Store regA (DirAddr 3)
        , Jump (Rel 7)
        , Load (DirAddr 1) regA
        , Load (DirAddr 2) regB
        , Compute Equal regA regB regA
        , Branch regA (Rel (-6))
        , Load (ImmValue 4) regB
        , Store regB (DirAddr 3)
        , EndProg
       ]
