module Basic where

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
        , Compute Sub reg0 regA regB
        , Store regB (DirAddr 2)
        , Load (ImmValue 3) regB
        , Store regB (DirAddr 3)
        , Load (DirAddr 2) regB
        , Load (ImmValue 3) regC
        , Compute Mul regB regC regB
        , Store regB (DirAddr 3)
        , Load (DirAddr 3) regB
        , Load (DirAddr 1) regC
        , Compute Add regB regC regB
        , Store regB (DirAddr 2)
        , Load (DirAddr 2) regB
        , Load (DirAddr 3) regC
        , Compute Sub regB regC regB
        , Store regB (DirAddr 1)
        , Load (ImmValue 0) regB
        , Store regB (DirAddr 4)
        , Load (ImmValue 0) regB
        , Store regB (DirAddr 5)
        , Load (DirAddr 1) regB
        , Load (ImmValue 1) regC
        , Compute Equal regB regC regB
        , Load (DirAddr 2) regC
        , Load (ImmValue 3) regD
        , Compute Equal regC regD regC
        , Compute Or regB regC regB
        , Store regB (DirAddr 4)
        , Load (DirAddr 3) regB
        , Load (DirAddr 2) regC
        , Load (DirAddr 1) regD
        , Compute Sub regC regD regC
        , Compute Equal regB regC regB
        , Store regB (DirAddr 5)
        , Load (DirAddr 5) regB
        , Load (DirAddr 4) regC
        , Compute Equal regC reg0 regD
        , Compute And regB regD regB
        , Store regB (DirAddr 6)
        , EndProg
       ]
