from pymongo import MongoClient
client = MongoClient()
db = client.Saarthi
gen = db.general
fp = open("db_gen1.txt","r")
f = fp.readlines()
i=0
post = []
for x in f:
     if i==0:
             post.append({"lat": float((x.split())[0][3:]), "lng": float((x.split())[1])})
             i=i+1
     else:
             post.append({"lat": float((x.split())[0]), "lng": float((x.split())[1])})

for i in post:
     gen.insert_one(i)
