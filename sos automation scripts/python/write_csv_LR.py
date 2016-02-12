#!/usr/bin/python
import sys, getopt, os
from datetime import datetime, date, time
import time
import codecs
import datetime
import csv

def main(argv):
   inputfile = argv[0]
   print inputfile
   vett_name = os.path.basename(inputfile).split("_")
   row_prec = get_last_row(inputfile)
   write_last_reading("/home/SUNSHINE/importer/support/"+vett_name[0]+"_"+vett_name[1]+"_"+vett_name[2]+"_LR.csv",row_prec)



def get_last_row(csv_filename):
   with open(csv_filename,'r') as f:
     reader = csv.reader(f, delimiter=';', quotechar='|')
     lastline = reader.next()
     for line in reader:
       lastline = line
     return lastline


def write_last_reading(name_f,row):
  with open(name_f, 'w') as temp_write:
    spam_write = csv.writer(temp_write, delimiter=';')
    spam_write.writerow(row)
  temp_write.close()

if __name__ == "__main__":
   main(sys.argv[1:])

