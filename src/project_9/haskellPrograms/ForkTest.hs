module ForkTest where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
          Load (ImmValue 5) regA
        , WriteInstr regA (DirAddr 3)
        , Branch regSprID (Abs 6)
        , Load (ImmValue 11) regA
        , WriteInstr regA (DirAddr 1)
        , Jump (Abs 18)
        , ReadInstr (IndAddr regSprID)
        , Receive regA
        , Compute Equal regA reg0 regB
        , Branch regB (Rel (-3))
        , Jump (Ind regA)
        , ReadInstr (DirAddr 3)
        , Receive regA
        , Load (ImmValue 4) regB
        , Compute Add regA regB regA
        , WriteInstr regA (DirAddr 3)
        , WriteInstr reg0 (IndAddr regSprID)
        , EndProg
        , Branch regSprID (Abs 6)
        , Load (ImmValue 22) regA
        , WriteInstr regA (DirAddr 2)
        , Jump (Abs 29)
        , ReadInstr (DirAddr 3)
        , Receive regA
        , Load (ImmValue 10) regB
        , Compute Add regA regB regA
        , WriteInstr regA (DirAddr 3)
        , WriteInstr reg0 (IndAddr regSprID)
        , EndProg
        , ReadInstr (DirAddr 3)
        , Receive regA
        , Load (ImmValue 3) regB
        , Compute Sub regA regB regA
        , WriteInstr regA (DirAddr 3)
        , ReadInstr (DirAddr 1)
        , Receive regA
        , Compute NEq regA reg0 regA
        , Branch regA (Rel (-3))
        , ReadInstr (DirAddr 2)
        , Receive regA
        , Compute NEq regA reg0 regA
        , Branch regA (Rel (-3))
        , EndProg
       ]

demoTest = sysTest [prog,prog,prog]
