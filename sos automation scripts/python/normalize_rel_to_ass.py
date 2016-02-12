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
   file_to_ass(inputfile)  
  
  
   

#il metodo porta i valori da consumi a letture, per ogni valore sommo il precedente, ad eccezzione del primo
def file_to_ass(name_f):
  row_num = ''
  sum = 0
  sumC = 0
  with open(name_f, 'r') as file_read:
    spam_reader = csv.reader(file_read, delimiter=';', quotechar='|')
    with open("temp.csv", 'w') as temp_write:
      spam_write = csv.writer(temp_write, delimiter=';')
      try:
        row = spam_reader.next()
        spam_write.writerow(row)
        row_num = len(row)
      except csv.Error:
        print "Error csv"
      except StopIteration:
        print "Iteration End"
      while True:
        try:
          row_prec = row
          sum = sum + float(row_prec[1])
          if row_num > 2:
            sumC = sumC + float(row_prec[2])
          row = spam_reader.next()
          if row_num == 2:
            print "questo e' quello che scrive "+ row[0]+";"+ str(float(row[1]))+"+"+ str(sum)
            print "row[0] "+ row[0]                    
            spam_write.writerow([row[0], str(float(row[1])+ float(sum))])
          else:
            spam_write.writerow([row[0], str(float(row[1])+ float(sum)),str(float(row[2])+ float(sumC))])
        except csv.Error:
          print "Error csv"
        except StopIteration:
          print "Iteration End"
          break 
  #rinomino il file temp 
  os.rename('temp.csv',name_f)
  #shutil.rmtree("/tmp/import/")
  #os.mkdir("/tmp/import/")
  #write_last_reading("/tmp/import/"+os.path.basename(name_f),row_prec)
  vett_name = os.path.basename(name_f).split("_")
  write_last_reading("/home/SUNSHINE/importer/support/"+vett_name[0]+"_"+vett_name[1]+"_"+vett_name[2]+"_LR.csv",row_prec)



def write_last_reading(name_f,row):
  with open(name_f, 'w') as temp_write:
    spam_write = csv.writer(temp_write, delimiter=';')
    spam_write.writerow(row)
  temp_write.close()
    
if __name__ == "__main__":
   main(sys.argv[1:])
