var express = require("express");
var router = express.Router();
var bodyParser = require("body-parser");
const geolib = require('geolib');
var VerifyToken = require(__root + "auth/VerifyToken");
var utm =  require('utm');
var math = require('mathjs');
var kmeans = require('node-kmeans');
var mongoose = require("mongoose");
var nos = 5;
var gmu = require( 'google-polyline' );

//Globals
var srcu,theta;
var gen = mongoose.model('gen' , new mongoose.Schema({ lat: Number , lng: Number}, 
  { collection : 'general' })); 
var fem = mongoose.model('fem' , new mongoose.Schema({ lat: Number , lng: Number}, 
  { collection : 'female' })); 
var kid = mongoose.model('kid' , new mongoose.Schema({ lat: Number , lng: Number}, 
    { collection : 'kid' })); 
var gar = mongoose.model('gar' , new mongoose.Schema({ lat: Number , lng: Number}, 
      { collection : 'garden' }));
var hospital = mongoose.model('hospital' , new mongoose.Schema({ lat: Number , lng: Number}, 
      { collection : 'hos' }));

router.use(bodyParser.urlencoded({ extended: true }));

router.use(bodyParser.json());

const gmap = require('@google/maps').createClient({
  key : 'AIzaSyB6ky0s6kmaxH15hsxsNHKuZeI6n_OG2eA'
}) 


var sortByProperty = function (property) {
  return function (x, y) {
      return ((x[property] === y[property]) ? 0 : ((x[property] > y[property]) ? 1 : -1));
  };
};

function getPlaces(query,callback){
  response = [];
  gmap.placesNearby(query,function(err,res){
    if(!err){
      response[0] = res.json;
      if("next_page_token" in response[0]){
        gmap.placesNearby({pagetoken: response[0]["next_page_token"]} ,function(err,res){
          if(!err){
            response[1] = res.json;
            if("next_page_token" in response[1]){
              gmap.placesNearby({pagetoken:response[1]["next_page_token"]},function(err,res){
                if(!err){
                  response[2] = res.json;
                  callback(response);
                }
                else console.log(err); 
              });
            }
            else callback(response);
          }
          else console.log(err);
        });
      }
      else callback(response);
    }
    else console.log(err);
  });
}

function trans(old){
  //console.log(old);
  var p = utm.fromLatLon(old["lat"],old["lng"]);
  //console.log(p);
   return {
    easting: (p["easting"]-srcu["easting"])*math.cos(theta) + (p["northing"]-srcu["northing"])*math.sin(theta),
    northing: (p["northing"]-srcu["northing"])*math.cos(theta) - (p["easting"]-srcu["easting"])*math.sin(theta),
    zoneNum: p["zoneNum"],
    zoneLetter: p["zoneLetter"]
  }
}


function calc(src, dst,db,isattr,callback)
{
  db.find({},function(err,res){
    var coll = res;
    //console.log(coll);
    gmap.geocode({address: src}, function(err,res){
      if(!err){
        srcl = res.json.results[0].geometry.location;
        gmap.geocode({address: dst}, function(err,res){
          if(!err){
            dstl = res.json.results[0].geometry.location;
            console.log(srcl);
            console.log(dstl);
             gmap.directions({origin:srcl, destination:dstl, alternatives:true}, function(err,res){ 
              attrs = [];
               wp = [];
               count = res.json.routes.length;
               polylines = ['~','~','~'];
               times = [-1,-1,-1];
               //attrs[0] = 0; attrs[1] = 0; attrs[2] = 0;
               l = 0;
               attrs = [[1000,0],[1000,1],[1000,2]];
              if(!err){
                for(let i of res.json.routes){
                  attrs[l] = [0,l];
                  times[l] = i.legs[0].duration.text;
                  polylines[l] = i.overview_polyline.points;
                  arr = gmu.decode(polylines[l]);
                  
                  for(let j of arr){
                      for(let k of coll){
                        //console.log("hi");
                        z = geolib.getDistance({lat:k["lat"], lng: k["lng"]},{lat:j[0] , lng:j[1]});
                        var a;
                        if(isattr==1) a = k["attr"];
                        else a = 1; 
                        if(z>=1) attrs[l][0] = attrs[l][0] + a/(z*z);
                        else attrs[l][0] = attrs[l][0] + a*10;
                      }
                  }
                  //console.log(attrs);
                  wp[l] = [];
                  for(let m of i.legs[0].steps) wp[l].push(m.end_location);
                  l++;
                }
                attrs.sort( function( a, b )
                {
                  if ( a[0] == b[0] ) return 0;
                  return a[0] < b[0] ? -1 : 1;
                });
                ways = ['~','~','~'];
                console.log(count);
                gmap.directions({origin:srcl, destination: dstl, waypoints: wp[0]},function(err,res){
                  s = res.requestUrl;
                  ways[0]="https://www.google.com/maps/dir/?api=1&"  + s.slice(s.indexOf("?")+1,s.indexOf("key")-2);
                  if(count>1){
                    gmap.directions({origin:srcl, destination: dstl, waypoints: wp[1]},function(err,res){
                      s = res.requestUrl;
                      ways[1]="https://www.google.com/maps/dir/?api=1&"  + s.slice(s.indexOf("?")+1,s.indexOf("key")-2);
                      if(count>2){
                        gmap.directions({origin:srcl, destination: dstl, waypoints: wp[2]},function(err,res){
                          s = res.requestUrl;
                          ways[2]="https://www.google.com/maps/dir/?api=1&"  + s.slice(s.indexOf("?")+1,s.indexOf("key")-2);
                          callback({
                            "startLat" : srcl["lat"],
                            "startLang" : srcl["lng"],
                            "endLat" : dstl["lat"],
                            "endtLang" : dstl["lng"],
                            "green": polylines[attrs[0][1]],
                            "blue": polylines[attrs[1][1]],
                            "red": polylines[attrs[2][1]],
                            "greenLink": ways[attrs[0][1]],
                            "blueLink": ways[attrs[1][1]],
                            "redLink":ways[attrs[2][1]],
                            "greenTime": times[attrs[0][1]],
                            "blueTime": times[attrs[1][1]],
                            "redTime":times[attrs[2][1]]
                          });
                        });
                      }
                      else callback({
                        "startLat" : srcl["lat"],
                        "startLang" : srcl["lng"],
                        "endLat" : dstl["lat"],
                        "endtLang" : dstl["lng"],
                        "green": polylines[attrs[0][1]],
                        "red": polylines[attrs[1][1]],
                        "blue": polylines[attrs[2][1]],
                        "greenLink": ways[attrs[0][1]],
                        "redLink": ways[attrs[1][1]],
                        "blueLink":ways[attrs[2][1]],
                        "greenTime": times[attrs[0][1]],
                        "blueTime": times[attrs[2][1]],
                        "redTime":times[attrs[1][1]]
                      });
                    });
                }
                else callback({
                  "startLat" : srcl["lat"],
                  "startLang" : srcl["lng"],
                  "endLat" : dstl["lat"],
                  "endtLang" : dstl["lng"],
                  "green": polylines[attrs[0][1]],
                  "red": polylines[attrs[1][1]],
                  "blue": polylines[attrs[2][1]],
                  "greenLink": ways[attrs[0][1]],
                  "redLink": ways[attrs[1][1]],
                  "blueLink":ways[attrs[2][1]],
                  "greenTime": times[attrs[0][1]],
                        "blueTime": times[attrs[2][1]],
                        "redTime":times[attrs[1][1]]
                });
              });
            }
               else console.log(err);
             });
          }
          else console.log(err);
        });
      }
      else console.log(err);
    });
  });
  
}

function safe_gen(src, dst, callback){
   calc(src,dst,gen,0,function(response){
     callback(response);
   });
}

function noiseless(src, dst, callback){
  calc(src,dst,gen,1,function(response){
    callback(response);
  });
}

function safe_fem(src, dst, callback){
    calc(src,dst,fem,0,function(response){
      callback(response);
    });
}

function safe_kids(src, dst, callback){
   calc(src,dst,kid,0,function(response){
     callback(response);
   });
}



function fun_kids(src, dst,callback){
  gmap.geocode({address: src}, function(err,res){
    if(!err){
      srcl = res.json.results[0].geometry.location;
      gmap.geocode({address: dst}, function(err,res){
        if(!err){
          dstl = res.json.results[0].geometry.location;
          rad = geolib.getDistance(srcl,dstl);
          console.log(rad);
          gmap.placesNearby({location: srcl ,radius: rad, keyword: "garden"},function(res){
            console.log(err);  
            nears = res;
              srcu = utm.fromLatLon(srcl["lat"],srcl["lng"]);
              dstu = utm.fromLatLon(dstl["lat"],dstl["lng"]);
              if(dstu["easting"]-srcu["easting"]!=0)
                theta = math.atan((dstu["northing"]-srcu["northing"])/(dstu["easting"]-srcu["easting"])) + math.pi/2;
              else theta = math.pi;
              i = 0;
                  for(let near of nears){
                    for(let pt of near["results"]){
                      if(trans(pt["geometry"]["location"])["northing"]>0){
                        pts[i] = pt["geometry"]["location"];
                        i=i+1;
                      }
                    }
                  }
                  var vector = [];
                  var ptsu = [];
                  for(let i in ptsval) {
                    ptsu[i]=utm.fromLatLon(pts[i]["lat"],pts[i]["lng"]);
                    vector[i] = [ptsu[i]['easting'], ptsu[i]['northing']];
                  }
                  kmeans.clusterize(vector, {k: 5},function(err,res){
                    if(!err){
                      wp = res;
                      wpf = [];
                      j = 0;
                      for(let i of wp[0].clusterInd){
                          wpf[j] = pts[i];
                          j++;
                      }
                      gmap.directions({origin:srcl, destination:dstl, waypoints:wpf, optimize: true}, function(err,res){
                        callback(res);
                      });
                    }
                    else console.log(err);
                  });
    
              });
        }
        else console.log(err);
    });
    }
  });
}

function green(src,dst,callback){
  calc(src,dst,kid,0,function(response){
    console.log(response);
    var temp = {Link: response.redLink, Poly: response.red, time: response.redTime }
    response.redLink = response.greenLink;
    response.red = response.green;
    response.redTime = response.greenTime;
    response.greenLink = temp.Link;
    response.green = temp.Poly;
    response.greenTime = temp.time;
    console.log(response);
    callback(response);
  });
}

function hos(src,dst,callback){
  calc(src,dst,hospital,0,function(response){
    console.log(response);
    var temp = {Link: response.redLink, Poly: response.red, time: response.redTime }
    response.redLink = response.greenLink;
    response.red = response.green;
    response.redTime = response.greenTime;
    response.greenLink = temp.Link;
    response.green = temp.Poly;
    response.greenTime = temp.time;
    console.log(response);
    callback(response);
  });
}




//console.log(arr);


router.post("/", function(req, res) {
  console.log(req.body);
  switch(parseInt(req.body.mod)){
    case 1:
        safe_gen(req.body.src,req.body.dst,function(response){
          s = response;
          console.log(s);
          res.status(200).send(s);
        });
        break;
    case 2:
       hos(req.body.src,req.body.dst,function(response){
          s = response;
          res.status(200).send(s);
        });
        break;
    case 3:
        noiseless(req.body.src,req.body.dst,function(response){
          s = response;
          res.status(200).send(s);
        });
        break;
    case 4:
      green(req.body.src,req.body.dst,function(response){
        s = response;
        res.status(200).send(s);
      });
        break;
    case 5:
        green(req.body.src,req.body.dst,function(response){
          s = response;
          res.status(200).send(s);
        });
        break;
  }
    
});


module.exports = router;