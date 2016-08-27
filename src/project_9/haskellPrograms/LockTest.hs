module LockTest where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
          Load (ImmValue 0) regA
        , WriteInstr regA (DirAddr 3)
        , Load (ImmValue 10) regA
        , WriteInstr regA (DirAddr 5)
        , Branch regSprID (Abs 8)
        , Load (ImmValue 13) regA
        , WriteInstr regA (DirAddr 1)
        , Jump (Abs 25)
        , ReadInstr (IndAddr regSprID)
        , Receive regA
        , Compute Equal regA reg0 regB
        , Branch regB (Rel (-3))
        , Jump (Ind regA)
        , TestAndSet (DirAddr 4)
        , Receive regA
        , Compute Equal regA reg0 regA
        , Branch regA (Rel (-3))
        , ReadInstr (DirAddr 3)
        , Receive regA
        , Load (ImmValue 5) regB
        , Compute Add regA regB regA
        , WriteInstr regA (DirAddr 3)
        , WriteInstr reg0 (DirAddr 4)
        , WriteInstr reg0 (IndAddr regSprID)
        , EndProg
        , Branch regSprID (Abs 8)
        , Load (ImmValue 29) regA
        , WriteInstr regA (DirAddr 2)
        , Jump (Abs 41)
        , TestAndSet (DirAddr 6)
        , Receive regA
        , Compute Equal regA reg0 regA
        , Branch regA (Rel (-3))
        , ReadInstr (DirAddr 5)
        , Receive regA
        , Load (ImmValue 10) regB
        , Compute Add regA regB regA
        , WriteInstr regA (DirAddr 5)
        , WriteInstr reg0 (DirAddr 6)
        , WriteInstr reg0 (IndAddr regSprID)
        , EndProg
        , TestAndSet (DirAddr 4)
        , Receive regA
        , Compute Equal regA reg0 regA
        , Branch regA (Rel (-3))
        , ReadInstr (DirAddr 3)
        , Receive regA
        , Load (ImmValue 7) regB
        , Compute Sub regA regB regA
        , WriteInstr regA (DirAddr 3)
        , WriteInstr reg0 (DirAddr 4)
        , TestAndSet (DirAddr 6)
        , Receive regA
        , Compute Equal regA reg0 regA
        , Branch regA (Rel (-3))
        , ReadInstr (DirAddr 5)
        , Receive regA
        , Load (ImmValue 15) regB
        , Compute Sub regA regB regA
        , WriteInstr regA (DirAddr 5)
        , WriteInstr reg0 (DirAddr 6)
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
