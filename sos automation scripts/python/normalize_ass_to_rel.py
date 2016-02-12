#!/usr/bin/python
import sys, getopt, os
import logging
import csv
from datetime import datetime


TIMEFORMAT = '%Y-%m-%d %H:%M:%S'

def main(argv):

    inputfile = argv[0]
    arg_value = argv[1]
    arg_valueCost = argv[2]

    new_values = read_last_insert(inputfile)
    print new_values

    arg_value = None
    arg_date = None
    arg_cost = None
    print len(new_values)

    if len(new_values) > 1:
        arg_date = new_values[0]
        arg_value = new_values[1]
    if len(new_values)>2:
       arg_valueCost = new_values[2]

    last_reading_time = cast_string_to_date(arg_date)
    last_reading_value =cast_arg_to_float(arg_value)
    last_reading_cost =cast_arg_to_float(arg_valueCost)
    fileToRel(inputfile,last_reading_value,last_reading_cost,last_reading_time)


    exit()
    print argv
    last_value = cast_arg_to_float(arg_value)
    last_valueCost = cast_arg_to_float(arg_valueCost)

    file_to_rel(inputfile, last_value, arg_valueCost)

  

def read_last_insert(filename):
    vett_name = os.path.basename(filename).split("_")
    datefile = "/home/SUNSHINE/importer/support/"+vett_name[0]+"_"+vett_name[1]+"_"+vett_name[2]+"_LR.csv"
    print datefile
    if os.path.exists(datefile):
        with open (datefile, 'rb') as csv_lastdate_file:
            spamreader = csv.reader(csv_lastdate_file, delimiter=';')
            row = spamreader.next()
            csv_lastdate_file.close()
            return row
    return []


def write_last_insert(filename,date,value):
    vett_name = os.path.basename(filename).split("_")
    datefile = "/home/SUNSHINE/importer/support/"+vett_name[0]+"_"+vett_name[1]+"_"+vett_name[2]+"_LR.csv"
    print datefile
    with open(datefile, 'wb') as file_witer:
        writer = csv.writer(file_witer, delimiter=';')
        writer.writerow([date,value])
        file_witer.close()



def cast_string_to_date(stringDate):
    try:
        newdate = datetime.strptime(stringDate.strip(), TIMEFORMAT)
    except Exception as errore:
        newdate = datetime.strptime('2010-02-27 10:59:00',  TIMEFORMAT)
    return newdate

def cast_arg_to_float(v):
  try:
    value = float(v)
  except :
    value = 0 
  return value
  


def fileToRel(nomeFile,lastValue,lastCost,lastDate):
    newValues = []
    with open(nomeFile, 'rb') as file_read:
        csvReader = csv.reader(file_read, delimiter=';')
        print nomeFile

        for row in csvReader:
            newDate = cast_string_to_date(row[0])
            if(newDate > lastDate):
                value = cast_arg_to_float(row[1])
                newValues.append([newDate.strftime(TIMEFORMAT),value-lastValue])
                lastValue = value
                lastDate = newDate
        file_read.close()
    if len(newValues) > 0 :
        name_f_R = nomeFile[:nomeFile.find(os.path.basename(nomeFile))] + os.path.basename(nomeFile)[:11]+'R'+os.path.basename(nomeFile)[12:]
        print name_f_R
        with open(name_f_R, 'wb') as file_witer:
            writer = csv.writer(file_witer, delimiter=';')
            for row in newValues:
                writer.writerow(row)
            file_witer.close()
        write_last_insert(nomeFile, lastDate.strftime(TIMEFORMAT), lastValue)
        if (name_f_R.find('_ir_') >= 0):
            new_name_f = name_f_R.replace("_ir_","_1h_")
            os.rename(name_f_R, new_name_f)
    os.remove(nomeFile)






#il metodo porta i valori da consumi a letture, per ogni valore sommo il precedente, ad eccezzione del primo
def file_to_rel(name_f, last_value, last_valueCost):
  row_num = ''
  sum = last_value
  sumC = last_valueCost
  with open(name_f, 'rb') as file_read:
    spam_reader = csv.reader(file_read, delimiter=';')
    with open("temp.csv", 'wb') as temp_write:
      spam_write = csv.writer(temp_write, delimiter=';')
      while True:
        try:
          row = spam_reader.next()
          row_num = len(str(row).split(','))
          print row
          print "numero della rigaaa: " + str(row_num)
          if row_num == 2:
            print "operazione "+ str(float(row[1]))+"-"+ str(sum)+"="+ str(float(row[1])- sum)
            spam_write.writerow([row[0],str(float(row[1])- sum)])
          else:
            spam_write.writerow([row[0],str(float(row[1])- sum), str(float(row[2])- sumC)])
          sum = float(row [1])
          if row_num > 2:
            sumC = float(row [2])
        except csv.Error:
          print "Error csv"
          break
        except StopIteration:
          print "Iteration End"
          break 
    temp_write.close()
  file_read.close()
  #metto la lettera R di relativo 
  name_f_R = name_f[:name_f.find(os.path.basename(name_f))] + os.path.basename(name_f)[:11]+'R'+os.path.basename(name_f)[12:]
  #se necessario sostituisco ir con 1h
  if (name_f.find('_ir_') >= 0):
    new_name_f = name_f.replace("_ir_","_1h_")
    os.rename('temp.csv', new_name_f)
    os.remove(name_f)
  else:
    os.remove(name_f)
    os.rename('temp.csv', name_f_R)


# string=$path_ass_to_rel" "$2" "$3" "$6

if __name__ == "__main__":
    argument =sys.argv[1:]

    main(argument)

