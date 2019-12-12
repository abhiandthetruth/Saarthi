var express = require("express");
var router = express.Router();
var bodyParser = require("body-parser");

var VerifyToken = require(__root + "auth/VerifyToken");

router.use(bodyParser.urlencoded({ extended: true }));
router.use(bodyParser.json());

const types = {
  theatre: "clean,quality,price,sound,food,network,safe,staff,parking",
  garden: "clean,green,mosquitoe,parking,kid",
  Shop: "price, quality ,parking",
  hospital: "clean,price,parking,doctor,time",
  temple: "clean,safety-safe,parking",
  restaurants: "clean,quality,cost,parking,food,staff,location,pack",
  hotels: "clean,quality,price-cost,safety,parking"
};

const https = require("https");
// CREATES A NEW USER
router.post("/", function(req, res) {
  console.log(req.body.name);
  https
    .get(
      "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" +
        req.body.name +
        "&inputtype=textquery&fields=place_id&key=AIzaSyBGe3kt3IzV8XbIQWDNC3AnkDTA1yWcdlQ",
      resp => {
        let data = "";

        // A chunk of data has been recieved.
        resp.on("data", chunk => {
          data += chunk;
        });

        // The whole response has been received. Print out the result.
        resp.on("end", () => {
          const x = JSON.parse(data);

          https
            .get(
              "https://maps.googleapis.com/maps/api/place/details/json?place_id=" +
                x.candidates[0].place_id +
                "&key=AIzaSyBGe3kt3IzV8XbIQWDNC3AnkDTA1yWcdlQ",
              resp => {
                let data = "";

                // A chunk of data has been recieved.
                resp.on("data", chunk => {
                  data += chunk;
                });

                // The whole response has been received. Print out the result.
                resp.on("end", () => {
                  var reviews = JSON.parse(data);
                  // res.send(reviews);
                  ///      res.send(reviews);
                  // res.send(reviews);
                  //  console.log(reviews["result"]["reviews"]);
                  var finaltext = " ";
                  for (
                    var i = 0;
                    i < reviews["result"]["reviews"].length;
                    i++
                  ) {
                    finaltext += "." + reviews["result"]["reviews"][i]["text"];
                    // console.log(
                    //   reviews["result"]["reviews"][i]["text"] + "\n \n \n \n"
                    // );
                  }

                  finaltext = finaltext.replace(",", ".");
                  finaltext = finaltext.replace("?", ".");
                  finaltext = finaltext.replace("!", ".");
                  //res.send(finaltext);
                  var arr = finaltext.split(".");
                  var x = 0;
                  var Sentiment = require("sentiment");
                  var sentiment = new Sentiment();

                  if (reviews["result"]["types"].includes("movie_theater")) {
                    //res.send("it worked");
                    var vars = types["theatre"].split(",");
                  } else if (reviews["result"]["types"].includes("park")) {
                    //res.send("hii");
                    var vars = types["garden"].split(",");
                  } else if (
                    reviews["result"]["types"].includes("hospital") ||
                    reviews["result"]["types"].includes("health")
                  ) {
                    //res.send("hiii");
                    var vars = types["hospital"].split(",");
                  } else if (reviews["result"]["types"].includes("food")) {
                    var vars = types["restaurants"].split(",");
                  }
                  //res.send(reviews);

                  var y = {};
                  for (var k = 0; k < vars.length; k++) {
                    y[vars[k]] = 0;
                  }

                  for (var i = 0; i < arr.length; i++) {
                    var result = sentiment.analyze(arr[i]);
                    //console.dir(result.score); // Score: -2, Comparative: -0.666
                    for (var j = 0; j < vars.length; j++) {
                      var n = arr[i].search(vars[j]);
                      if (n > 0) {
                        y[vars[j]] += result.score;
                      }
                    }
                  }
                  console.log(y);
                  res.send(y);
                });
              }
            )
            .on("error", err => {
              console.log("Error: " + err.message);
            });
        });
      }
    )
    .on("error", err => {
      console.log("Error: " + err.message);
    });
});

module.exports = router;
