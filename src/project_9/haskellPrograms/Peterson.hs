module Peterson where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
          Load (ImmValue 0) regA
        , WriteInstr regA (DirAddr 2)
        , Load (ImmValue 0) regA
        , WriteInstr regA (DirAddr 4)
        , Branch regSprID (Abs 8)
        , Load (ImmValue 13) regA
        , WriteInstr regA (DirAddr 1)
        , Jump (Abs 32)
        , ReadInstr (IndAddr regSprID)
        , Receive regA
        , Compute Equal regA reg0 regB
        , Branch regB (Rel (-3))
        , Jump (Ind regA)
        , Load (ImmValue 1) regA
        , WriteInstr regA (DirAddr 2)
        , Load (ImmValue 1) regA
        , WriteInstr regA (DirAddr 6)
        , Jump (Rel 3)
        , Load (ImmValue 0) regA
        , Store regA (DirAddr 1)
        , ReadInstr (DirAddr 4)
        , Receive regA
        , ReadInstr (DirAddr 6)
        , Receive regB
        , Load (ImmValue 1) regC
        , Compute Equal regB regC regB
        , Compute And regA regB regA
        , Branch regA (Rel (-9))
        , Load (ImmValue 0) regA
        , WriteInstr regA (DirAddr 2)
        , WriteInstr reg0 (IndAddr regSprID)
        , EndProg
        , Load (ImmValue 1) regA
        , WriteInstr regA (DirAddr 4)
        , Load (ImmValue 0) regA
        , WriteInstr regA (DirAddr 6)
        , Jump (Rel 3)
        , Load (ImmValue 0) regA
        , Store regA (DirAddr 1)
        , ReadInstr (DirAddr 2)
        , Receive regA
        , ReadInstr (DirAddr 6)
        , Receive regB
        , Load (ImmValue 0) regC
        , Compute Equal regB regC regB
        , Compute And regA regB regA
        , Branch regA (Rel (-9))
        , Load (ImmValue 0) regA
        , WriteInstr regA (DirAddr 4)
        , ReadInstr (DirAddr 1)
        , Receive regA
        , Compute NEq regA reg0 regA
        , Branch regA (Rel (-3))
        , EndProg
       ]

demoTest = sysTest [prog,prog]
