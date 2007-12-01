if (!SeleniumExt) { 
  var SeleniumExt = {};
}

SeleniumExt.ErrorCapture = {
  outputDir : "",

  getCommandUrl: function() {
    if (!SeleniumExt.ErrorCapture.commandURL) {
      var url = window.location.href;
      var slashPairOffset = url.indexOf("//") + "//".length;
      var pathSlashOffset = url.substring(slashPairOffset).indexOf("/");
      SeleniumExt.ErrorCapture.commandURL = url.substring(0, slashPairOffset + pathSlashOffset) + "/selenium-server/driver/";
    }
    return SeleniumExt.ErrorCapture.commandURL;
  },

  send: function(command, params) {
    var xmlHttpObject = XmlHttp.create();

    var postParams = ["cmd=" + encodeURIComponent(command)];
    for(var key in params) {
      postParams.push(key + "=" + encodeURIComponent(params[key]));
    }
    var postData = postParams.join("&")

    xmlHttpObject.open("POST", SeleniumExt.ErrorCapture.getCommandUrl(), false);
    xmlHttpObject.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xmlHttpObject.send(postData);

    return xmlHttpObject.responseText;
  },

  captureScreenshot: function(fileName) {
    return SeleniumExt.ErrorCapture.send("captureScreenshot", {"1": fileName});
  },

  extractFileName: function(doc) {
    var path = doc.location.pathname;
    return path.substring(path.lastIndexOf("/") + 1, path.indexOf("."));
  },

  testComplete: function() {
    if (htmlTestRunner.testFailed) {
      var caseName = SeleniumExt.ErrorCapture.extractFileName(this.htmlTestCase.testDocument);
      var suiteName = SeleniumExt.ErrorCapture.extractFileName(this.htmlTestCase.htmlTestSuiteRow.htmlTestSuite.suiteDocument);
      var fileName = (SeleniumExt.ErrorCapture.outputDir || ".") + "/" + suiteName + "-" + caseName + "-" + (new Date()).getTime() + ".png";
      var result = SeleniumExt.ErrorCapture.captureScreenshot(fileName);
      if (result == "OK") {
        LOG.info("capture error window: OK " + fileName);
      } else {
        LOG.warn("capture error window: " + result);
      }
    }
  }
}


HtmlRunnerTestLoop.prototype._testComplete = HtmlRunnerTestLoop.prototype.testComplete;
HtmlRunnerTestLoop.prototype.testComplete = function() {
  var self = this;
  window.setTimeout(function() {
    SeleniumExt.ErrorCapture.testComplete.apply(self, arguments);
    HtmlRunnerTestLoop.prototype._testComplete.apply(self, arguments);
  }, 1);
}
