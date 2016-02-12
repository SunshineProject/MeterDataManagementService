#!/bin/bash

if [ ! $# -eq 6 ];
  then
    echo "##"
    echo "-info-"
    echo "pathscript.sh <rel_irr> <pathcsv> <last_value_insert> <freq> <datestart> <last_value_cost>"
    echo "##"
    exit
fi

#rel = misura relativa
#nrel = misura non relativa, assoluta
#irr = letture irregolari
#nirr = letture regolari

rel_irr="11"
rel_nirr="10"
nrel_irr="01"
nrel_nirr="00"
freq_q="15"

path_interpolation="/home/SUNSHINE/importer/interpolateLast.py"
path_rel_to_ass="/home/SUNSHINE/importer/normalize_rel_to_ass.py"
path_ass_to_rel="/home/SUNSHINE/importer/normalize_ass_to_rel.py"
path_to_hours="/home/SUNSHINE/importer/to_hours.py"
path_write_last_record="/home/SUNSHINE/importer/write_csv_LR.py"

#normalizzo in ore se il file contiene dati campionati su 15min
#if [ $4 -eq $freq_q ]; then
#  string=$path_to_hours" "$2
#  exec $string &
#  wait $!
#fi


if [ $1 -eq $rel_irr ]; then
  #in questo caso devo trasformare in assoluto, poi regolarizzare e in fine nuovamente in rel
  string=$path_rel_to_ass" "$2" > log.log"
  echo $string
  exec $string &
  wait $!
  string=$path_interpolation' -i '$2' -f '$4' -s '$5
  exec $string &
  wait $!
  string=$path_ass_to_rel" "$2" "$3" "$6
  exec $string &
  wait $!
fi

if [ $1 -eq $nrel_irr ]; then
  #devo regolarizzare e poi portareli relativi
  string=$path_interpolation' -i '$2' -f '$4' -s '$5
  exec $string &
  wait $!
  string=$path_ass_to_rel" "$2" "$3" "$6
  exec $string &
  wait $!
fi

if [ $1 -eq $nrel_nirr ]; then
  #in questo caso devo solo portare i le letture in consumi
  string=$path_ass_to_rel" "$2" "$3" "$6
  echo $string
  exec $string &
  wait $!
fi

#se sono relativi e regolari devo solo tracciare l'ultimo record
if [ $1 -eq $rel_nirr ]; then
  string=$path_write_last_record" "$2
  echo $string
  exec $string &
  wait $!
fi
 
