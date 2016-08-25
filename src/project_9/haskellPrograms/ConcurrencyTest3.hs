module ConcurrencyTest where

import BasicFunctions
import HardwareTypes
import Sprockell
import System
import Simulation

prog :: [Instruction]
prog = [
         -- Start program
           Load (ImmValue 100) regA
         , WriteInstr regA (DirAddr 0)     -- Stored in shared memory
         
         -- Fork
         , Branch regSprID (Rel 4)
         , Load (ImmValue 11) regA
         , WriteInstr regA (DirAddr 1)     -- Sprockell 1 must jump to second EndProg
         , Jump (Abs 18)                   -- Sprockell 0 jumps to first EndProg
         
         -- BEGIN: loop
         , ReadInstr (IndAddr regSprID)
         , Receive regA
         , Compute Equal regA reg0 regB
         , Branch regB (Rel (-3))
         -- END: loop
         , Jump (Ind regA)
         
         -- Sprockell 1 is sent here
         , ReadInstr (DirAddr 0)
         , Receive regA
         , Load (ImmValue 150) regB
         , Compute Add regA regB regA
         , WriteInstr regA (DirAddr 0)
         , WriteInstr reg0 (IndAddr regSprID)
         , EndProg

         -- Sprockell 0 is sent here
         , ReadInstr (DirAddr 0)
         , Receive regA
         , Load (ImmValue 105) regB
         , Compute Sub regA regB regA
         , WriteInstr regA (DirAddr 0)
         
         -- Join (Sprockell 1)
         , ReadInstr (DirAddr 1)
         , Receive regA
         , Compute NEq regA reg0 regA
         , Branch regA (Rel (-3))
         
         -- Rest of the program
         , EndProg
       ]

demoTest = sysTest [prog,prog]
