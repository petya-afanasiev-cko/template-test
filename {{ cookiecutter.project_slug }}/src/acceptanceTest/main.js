var chai = require("chai"),
    request = require("supertest"),
    config = require("./config/config.js");
// Configure chai
global.should = chai.should();
global.expect = chai.expect;

// Make available globally
global.config = config;
global.noop = function() {};