var mongoose = require("mongoose");
var ParkingSpotSchema = new mongoose.Schema({
  name: {
    type: String,
    default: ""
  },
  timing: {
    type: String,
    default: ""
  },
  userId: {
    type: String,
    default: ""
  },
  phone: {
    type: String,
    default: ""
  },
  lat: {
    type: String,
    default: ""
  },
  lang: {
    type: String,
    default: ""
  },
  price: {
    type: Number,
    default: 0
  }
});

mongoose.model("ParkingSpot", ParkingSpotSchema);
module.exports = mongoose.model("ParkingSpot");
