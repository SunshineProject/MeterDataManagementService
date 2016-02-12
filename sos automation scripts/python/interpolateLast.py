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
   inputfile = ''
   freq = ''
   dts = ''
   dte = ''
   print argv
   try:
     opts, args = getopt.getopt(argv,"i:f:s:",["ifile=","freq=","startdt="])
   except getopt.GetoptError:
     print 'name_script.py -i <inputfile> -f <freq> -s <timestamp_start>'
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

   dts = setDate(dts, inputfile)

   with open("/home/SUNSHINE/importer/temp.csv", 'wb') as csvfilewriter:
     spamw = csv.writer(csvfilewriter, delimiter=';')
     spamw.writerow([inputfile,freq,dts])


   start_end = setstartend(dts.replace(";"," "),'2013-11-30 02:00:00')
   add_value(start_end,inputfile, freq)
   #replace_csv(inputfile)
   sort(inputfile, inputfile, 0)
   replace_csv(inputfile)
   
   

def setDate(d, filename):
  vett = os.path.basename(filename).split("_")
  n = vett[len(vett)-1].split(".")[0]
  nn = int(n)-1
  nn = str(nn)
  while len(nn) < len(n):
    nn = "0"+nn
  new_name_file = ""
  vett[len(vett)-1]=nn+".csv"
  new_name_file = vett[0]+"_"+vett[1]+"_"+vett[2]+"_"+vett[3]+"_"+vett[4]
  #for elem in vett:
  #  new_name_file = new_name_file +"_"+elem
 
  datefile = "/tmp/import/"+ new_name_file
  print "nome file da cui leggere "+datefile
  if os.path.isfile(datefile):
    with open (datefile, 'rb') as csv_lastdate_file:
      spamreader = csv.reader(csv_lastdate_file, delimiter=';')
      row = spamreader.next()
    return row[0]
  return d


def add_value(s_e, inputfile, fr):
   s_e[0] = s_e[0] + get_freq(fr)
   with open(inputfile, 'rb') as csvfilerw:
     spamreader = csv.reader(csvfilerw, delimiter=';', quotechar='|')
     with open("temp.csv", 'wb') as csvfilewriter:
       spamw = csv.writer(csvfilewriter, delimiter=';') 
       try:
         row = spamreader.next()
         rrow = spamreader.next()       
       except csv.Error:
         print "Error"
       except StopIteration:
         print "Iteration End"
       stringdate = row[0]
       row_num = len(row)
       date_row = datetime.datetime.strptime(stringdate, "%Y-%m-%d %H:%M:%S")
       if s_e[0] < date_row:
         #date_miss = s_e[0] + get_freq(fr)
         value = get_new_value(row, rrow, s_e[0])
         #date_to_write = date_miss.strftime("%Y.%m.%d;%H:%M:%S")
         #print date_to_write
         #spamw.writerow([date_to_write[1:15] ,str(value)])
         if row_num == 2:
           spamw.writerow([s_e[0].strftime("%Y-%m-%d %H:%M:%S"),str(value)])
         else:#se c'e' anche la colonna del costo
           rowC = row
           rrowC = rrow
           rowC[1] = rowC[2]
           rrowC[1] = rrowC[2]
           valueC = get_new_value(rowC, rrowC, s_e[0])
           spamw.writerow([s_e[0].strftime("%Y-%m-%d %H:%M:%S"),str(value),str(valueC)])
       else:
         spamw.writerow(row)
         spamw.writerow(rrow)
       while True:
         try:
           row = spamreader.next()
           spamw.writerow(row)
         except StopIteration:
           print "Iteration End"
           break
       csvfilewriter.close()
     csvfilerw.close()    
		 
   with open("temp.csv", 'rb') as tempread:
     spamrtemp = csv.reader(tempread, delimiter=';', quotechar='|')
     with open(inputfile, 'w') as csvfilerw:
       spamw = csv.writer(csvfilerw, delimiter=';',quoting=csv.QUOTE_MINIMAL)
       date_index = s_e[0] + get_freq(fr) # parto gia' dal secondo perche' la prima riga l'ho inserita nel ciclo precedente
       row = spamrtemp.next()
       rrow = spamrtemp.next()
       row_num = len(row)
       while True:
         stringdate = rrow[0]
         date_row = datetime.datetime.strptime(stringdate, "%Y-%m-%d %H:%M:%S")
         print "date_row > date_index" + str(date_row) + " > "+ str(date_index)
         if date_row > date_index:
           value = get_new_value(row, rrow, date_index)
           print "scrittura " + date_row.strftime("%Y-%m-%d %H:%M:%S")+ " "+str(value)
           #spamw.writerow([date_index.strftime('%Y.%m.%d;%H:%M:%S'),str(value)])
           if row_num == 2:
             spamw.writerow([date_index.strftime('%Y-%m-%d %H:%M:%S'),str(value)])
           else:#se c'e' anche la colonna del costo
             rowC = row
             rrowC = rrow
             rowC[1] = rowC[2]
             rrowC[1] = rrowC[2]
             valueC = get_new_value(rowC, rrowC, s_e[0])
             spamw.writerow([date_index.strftime('%Y-%m-%d %H:%M:%S'),str(value),str(valueC)])
         else:
           spamw.writerow(row)
           row = rrow
           try:
             #print "terzo ora scrivo"
  	     rrow = spamrtemp.next()
             #print "quarto ora scrivo"
           except StopIteration:
             spamw.writerow(row)
             print "End Iterator"
             break
         date_index = date_index + get_freq(fr)
           

def to_hours(inputfile):
   with open(inputfile, 'rb') as csvfilerw:
     spamreader = csv.reader(csvfilerw, delimiter=';', quotechar='|')
     with open("temp.csv", 'wb') as csvfilewriter:
       spamw = csv.writer(csvfilewriter, delimiter=';')
       count = 0
       if True:
         try:
           if count == 0:
             spamw.writerow(spamreader.next())
           count = (count + 1) % 4
         except csv.Error:
           print "Error"
         except StopIteration:
           print "Iteration End"
     csvfilewriter.close()
   csvfilerw.close()
   new_name_f = inputfile.replace("_15_","_1h_")
   os.rename('temp.csv', new_name_f)
   os.remove(inputfile)
   return new_name_f



def get_new_value(r1, rr2, dtx):
  str_d_1 = r1[0]
  str_d_2 = rr2[0]
  print "str1 " + str_d_1
  dt = datetime.datetime.strptime(str_d_1.replace('"',''), "%Y-%m-%d %H:%M:%S")
  x1 = time.mktime(dt.timetuple())
  dt = datetime.datetime.strptime(str_d_2.replace('"',''), "%Y-%m-%d %H:%M:%S")
  x2 = time.mktime(dt.timetuple())
  x = time.mktime(dtx.timetuple())
  y1 = float(r1[1])
  y2 = float(rr2[1])
  y = ((y1 - y2 )/(x1 - x2))*x + y2 - (((y1 - y2 )/(x1 - x2))*x2)
  if y < 0 :
    y = 0
  return y

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
  dtos = datetime.datetime.strptime(dts, "%Y-%m-%d %H:%M:%S")
  dtoe = datetime.datetime.strptime(dte, "%Y-%m-%d %H:%M:%S")
  print ("start-end " + dtos.strftime("%Y-%m-%d %H:%M:%S")+" - "+str(dtoe))
  return [dtos,dtoe]
  
 # print strdate
  
def replace_csv(name_f):
  contents = codecs.open(name_f, encoding='utf-8').read()
  with open(name_f, 'w') as out_file:
    out_file.write(contents.replace('"', ''))
  out_file.close()


def sort(name_f, output_name, column): 
  data = csv.reader(open(name_f),delimiter=';')
  sortedlist = sorted(data, key=operator.itemgetter(column))    # 0 specifies according to first column we want to sort
  #now write the sorte result into new CSV file
  with open(output_name, "wb") as f:
    fileWriter = csv.writer(f, delimiter=';')
    for row in sortedlist:
      print "riga "+ str(row)
      fileWriter.writerow(row)
  f.close()


    


if __name__ == "__main__":
   main(sys.argv[1:])
