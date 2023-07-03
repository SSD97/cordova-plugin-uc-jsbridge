var exec = require("cordova/exec");

exports.initUpswingSDK = function (partnerID, success, error) {
    exec(success, error, "UCJSBridge", "initUpswingSDK", [partnerID]);
};

exports.logOutFromUpswingSDK = function (success, error) {
    exec(success, error, "UCJSBridge", "logOutFromUpswingSDK", []);
};


