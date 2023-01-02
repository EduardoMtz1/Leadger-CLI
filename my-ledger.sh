#!/bin/bash
javac Ledger.java
java Ledger  --price-db ./Sample_files/prices_db \
-f ./Sample_files/index.ledger "$@"
