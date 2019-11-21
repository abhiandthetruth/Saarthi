import pandas as pd
import googlemaps
import json
from pymongo import MongoClient
client = MongoClient()
db = client.Saarthi
nis= db.nis

df = pd.read_excel("Average noise pollution statistics for the year 2016.xlsx", "Yearly Average")
fp = open("db_nis.txt","a",encoding="utf8")
fp1 = open("dbn_nis1.txt","a",encoding="utf8")
gmaps = googlemaps.Client(key='AIzaSyBNbSHpD55qfU-m2WbrhoPEEIAHFEIIKVE')
print(df.iloc[38,2])
print(df.iloc[38,15])
loc = []
for i in range(3,39):
    loc.append([df.iloc[i,2],df.iloc[i,15]])
print(gmaps.geocode(loc[0][0]+' Pune'))
for i in loc:
    #print(i)
    result = gmaps.geocode(i[0] + ' Pune')
    if (len(result)!=0) :
        lat = result[0]["geometry"]["location"]["lat"]
        lon = result[0]["geometry"]["location"]["lng"]
        fp.write(str(lat) + " " + str(lon) + " " + str(i[1]) + "\n")
        fp1.write(str(lat) + " " + str(lon) + " " + str(i[1]) + "\n" + " [" + i[0] +"]" + "\n")
        nis.insert_one({"lat":lat, "lng": lon, "attr": i[1]})
fp.close()
fp1.close()
