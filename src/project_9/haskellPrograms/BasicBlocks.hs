module BasicBlocks where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
          Load (ImmValue 0) regA
        , Store regA (DirAddr 1)
        , Load (ImmValue 2) regA
        , Load (DirAddr 1) regB
        , Compute Gt regA regB regA
        , Compute Equal regA reg0 regA
        , Branch regA (Rel 6)
        , Load (DirAddr 1) regB
        , Load (ImmValue 2) regC
        , Compute Mul regB regC regB
        , Store regB (DirAddr 1)
        , Jump (Abs 2)
        , Nop
        , Load (DirAddr 1) regA
        , Load (ImmValue 2) regB
        , Compute Equal regA regB regA
        , Compute Equal regA reg0 regA
        , Branch regA (Rel 3)
        , Load (DirAddr 1) regB
        , Store regB (DirAddr 2)
        , EndProg
       ]
