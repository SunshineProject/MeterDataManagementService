#!/usr/bin/python
import sys, getopt
from datetime import datetime, date, time
import datetime
import csv

def main(argv):
   inputfile = ''
   freq = ''
   dts = ''
   dte = ''
   print argv
   try:
      opts, args = getopt.getopt(argv,"i:f:s:e:",["ifile=","freq=","startdt=","enddt="])
   except getopt.GetoptError:
      print 'test.py -i <inputfile> -f <freq>'
      sys.exit(2)
   for opt, arg in opts:
     if opt == '-h':
       print 'test.py -i <inputfile> -f <freqHours>'
       sys.exit()
     elif opt in ("-i", "--ifile"):
       inputfile = arg
     elif opt in ("-f", "--freq"):
         freq = arg
     elif opt in ("-s", "--startdt"):
       dts = arg
     elif opt in ("-e", "--enddt"):
       dte = arg

   print 'Input file is "', inputfile
   print 'Freq file is "', freq
   print 'Date start ', dts
   print 'Date end ', dte

   print inputfile
  	 
   start_end = setstartend(dts,dte)
   
   add_value(start_end,inputfile, freq)  
   





def add_value(s_e, inputfile, fr):
   with open(inputfile, 'rb') as csvfileread:
    spamread = csv.reader(csvfileread, delimiter=';', quotechar='|')

    with open(inputfile, 'a') as csvfilewrite:
       spamwriter = csv.reader(csvfilewrite, delimiter=';', quotechar='|')
       while True:
         try:
           row = spamread.next()
         except csv.Error:
           print "Error"
         except StopIteration:
          print "Iteration End"
          break
         stringdate = row[0]+' '+row[1]
         date_row = datetime.datetime.strptime(stringdate, "%Y.%m.%d %H:%M:%S")
         print 'confronto'
         print s_e[0]
         print date_row
         while s_e[0] < date_row:
           date_row = date_row - get_freq(fr)
           value = get_new_value(row, spamread.next())
           print 'eccolooo ' + date_row.strftime("%Y.%m.%d;%H:%M:%S") + str(value)
           spamwriter.write(date_row.strftime("%Y.%m.%d;%H:%M:%S") + value)
           


def get_new_value(r, rr):
  d = float(rr[2]) - float(r[2])
  return float(r[2]) - d


def get_freq(fr):
  if fr == '1h':
    return datetime.timedelta(hours=1)
  elif fr == '1d':
    return datetime.timedelta(days=1)
  elif fr == '1w':
    return datetime.timedelta(weeks=1)
  elif fr == '1y':
    return datetime.timedelta(years=1)
	   
def setstartend(dts, dte):
  dtos = datetime.datetime.strptime(dts, "%Y.%m.%d %H:%M:%S")
  dtoe = datetime.datetime.strptime(dte, "%Y.%m.%d %H:%M:%S")
  return [dtos,dtoe]
  
  
  
if __name__ == "__main__":
   main(sys.argv[1:])
