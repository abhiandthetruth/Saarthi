import pandas as pd
import googlemaps
import json

df = pd.read_excel("List of registered nursing homes and hospitals under PMC for the year 2018.xlsx", "Sheet1")



fp = open("db_hos.txt","a",encoding="utf8")
fp1 = open("dbn_hos1.txt","a",encoding="utf8")
gmaps = googlemaps.Client(key='AIzaSyBNbSHpD55qfU-m2WbrhoPEEIAHFEIIKVE')
loc = []
for i in range(2,666):
    loc.append(df.iloc[i,3])
print(gmaps.geocode(loc[0]))
for i in loc:
    print(i)
    result = gmaps.geocode(i)
    if (len(result)!=0) :
        lat = result[0]["geometry"]["location"]["lat"]
        lon = result[0]["geometry"]["location"]["lng"]
        fp.write(str(lat) + " " + str(lon) +  "\n")
        fp1.write(str(lat) + " " + str(lon) +"\n" + " [" + i +"]" + "\n")
fp.close()
fp1.close()
