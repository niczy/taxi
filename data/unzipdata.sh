#!/bin/bash
for i in {1..12}
do
  unzip trip_data_$i.csv.zip
  unzip trip_fare_$i.csv.zip
done
