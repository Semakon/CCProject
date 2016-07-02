module WhileLoop where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
          Load (ImmValue 0) regA
        , Store regA (DirAddr 1)
        , Load (ImmValue 5) regA
        , Store regA (DirAddr 2)
        , Jump (Rel 5)
        , Load (DirAddr 1) regA
        , Load (ImmValue 1) regB
        , Compute Add regA regB regA
        , Store regA (DirAddr 1)
        , Load (DirAddr 2) regA
        , Load (DirAddr 1) regB
        , Compute Gt regA regB regA
        , Branch regA (Rel (-7))
        , EndProg
       ]
