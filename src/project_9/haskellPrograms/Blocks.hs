module Blocks where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
          Load (ImmValue 0) regA
        , Store regA (DirAddr 1)
        , Jump (Rel 5)
        , Load (DirAddr 1) regA
        , Load (ImmValue 2) regB
        , Compute Mul regA regB regA
        , Store regA (DirAddr 1)
        , Load (ImmValue 2) regA
        , Load (DirAddr 1) regB
        , Compute Gt regA regB regA
        , Branch regA (Rel (-7))
        , Jump (Rel 4)
        , Load (DirAddr 1) regA
        , Store regA (DirAddr 2)
        , Jump (Rel 5)
        , Load (DirAddr 1) regA
        , Load (ImmValue 2) regB
        , Compute Equal regA regB regA
        , Branch regA (Rel (-6))
        , EndProg
       ]
