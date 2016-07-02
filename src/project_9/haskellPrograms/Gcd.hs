module Gcd where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
          Load (ImmValue 10) regA
        , Store regA (DirAddr 1)
        , Load (ImmValue 5) regA
        , Store regA (DirAddr 2)
        , Jump (Rel 15)
        , Jump (Rel 6)
        , Load (DirAddr 1) regA
        , Load (DirAddr 2) regB
        , Compute Sub regA regB regA
        , Store regA (DirAddr 1)
        , Jump (Rel 9)
        , Load (DirAddr 1) regA
        , Load (DirAddr 2) regB
        , Compute Gt regA regB regA
        , Branch regA (Rel (-8))
        , Load (DirAddr 2) regB
        , Load (DirAddr 1) regC
        , Compute Sub regB regC regB
        , Store regB (DirAddr 2)
        , Load (DirAddr 1) regA
        , Load (DirAddr 2) regB
        , Compute NEq regA regB regA
        , Branch regA (Rel (-17))
        , EndProg
       ]
