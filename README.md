# explicit-model-checker
1. user interface (anyi)
2. user input format - backend format(statenode) + hashtable (bo)
3. checker method waiting to finish: EG,EF(anyi)
4. parse property to be verified to tree: waiting for formats (bo)
5.      ***new update 11/19: finished based on the format assumption
5. tree evaluation(bo) waiting to finish: AG,AF,AX,AU (bo)
6.      ***new update 11/19：AX finished
7.                        AG,AF,AU: waiting for EG and EF
6. testing (before merging, test all checker methods) (bo, anyi)
7.      ***new update 11/19: test for (4) is finished, test cases are recorded in the class PropertyEvaluation.java
