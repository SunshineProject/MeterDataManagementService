#!/usr/bin/python
import sys, getopt
from datetime import datetime, date, time
import time
import codecs 
import datetime 
import csv
import operator
import os

def main(argv):
   inputfile = argv[0]
   to_hours(inputfile)
        

def to_hours(inputfile):
   row_back = ""
   with open(inputfile, 'rb') as csvfilerw:
     spamreader = csv.reader(csvfilerw, delimiter=';', quotechar='|')
     with open("temp.csv", 'wb') as csvfilewriter:
       spamw = csv.writer(csvfilewriter, delimiter=';')
       count = 0
       while True:
         try:
           row = spamreader.next()
           if count == 0:
             print 'count '+ str(count)
             spamw.writerow(row)
             row_back = row
           count = (count + 1) % 4
         except csv.Error:
           print "Error"
           break
         except StopIteration:
           print "Iteration End"
           break
     csvfilewriter.close()
   csvfilerw.close()
   new_name_f = inputfile.replace("_15_","_1h_")
   os.rename('temp.csv', new_name_f)
   os.remove(inputfile)
   vett_name = os.path.basename(inputfile).split("_")
   write_last_reading("/tmp/import/"+vett_name[0]+"_"+vett_name[1]+"_"+vett_name[2]+"_LR.csv",row_back)


def write_last_reading(name_f,row):
  with open(name_f, 'w') as temp_write:
    spam_write = csv.writer(temp_write, delimiter=';')
    spam_write.writerow(row)
  temp_write.close()


   


if __name__ == "__main__":
   main(sys.argv[1:])
