if (!SeleniumExt) { 
  var SeleniumExt = {};
}

SeleniumExt.ErrorCapture = {
  outputDir : "",
  isCaptureEachVerify : false,

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

  capture: function(testCase) {
    var caseName = SeleniumExt.ErrorCapture.extractFileName(testCase.testDocument);
    var suiteName = SeleniumExt.ErrorCapture.extractFileName(testCase.htmlTestSuiteRow.htmlTestSuite.suiteDocument);
    var fileName = (SeleniumExt.ErrorCapture.outputDir || ".") + "/" + suiteName + "-" + caseName + "-" + (new Date()).getTime() + ".png";
    var result = SeleniumExt.ErrorCapture.captureScreenshot(fileName);
    if (result == "OK") {
      LOG.info("capture error window: OK " + fileName);
    } else {
      LOG.warn("capture error window: " + result);
    }
  },

  extractFileName: function(doc) {
    var path = doc.location.pathname;
    return path.substring(path.lastIndexOf("/") + 1, path.indexOf("."));
  }
}

HtmlRunnerTestLoop.prototype.$commandComplete = HtmlRunnerTestLoop.prototype.commandComplete;
HtmlRunnerTestLoop.prototype.commandComplete = function(result) {
  var commandObj = this.currentRow.getCommand();
  var command = "";
  if (commandObj) {
    command = commandObj.command;
  }

  if (result.failed ||
      SeleniumExt.ErrorCapture.isCaptureEachVerify && command.indexOf("verify") >= 0) {
    SeleniumExt.ErrorCapture.isNextTimeCapture = true;
  }

  this.$commandComplete(result);
}

HtmlRunnerTestLoop.prototype.$commandError = HtmlRunnerTestLoop.prototype.commandError;
HtmlRunnerTestLoop.prototype.commandError = function(errorMessage) {
  SeleniumExt.ErrorCapture.isNextTimeCapture = true;

  this.$commandError(errorMessage);
}


HtmlRunnerTestLoop.prototype.$continueTestAtCurrentCommand = HtmlRunnerTestLoop.prototype.continueTestAtCurrentCommand;
HtmlRunnerTestLoop.prototype.continueTestAtCurrentCommand = function() {
  if (SeleniumExt.ErrorCapture.isNextTimeCapture) {
    SeleniumExt.ErrorCapture.capture(this.htmlTestCase);
    SeleniumExt.ErrorCapture.isNextTimeCapture = false;
  }
  this.$continueTestAtCurrentCommand();
}

HtmlRunnerTestLoop.prototype.$testComplete = HtmlRunnerTestLoop.prototype.testComplete;
HtmlRunnerTestLoop.prototype.testComplete = function() {
  var self = this;
  window.setTimeout(function() {
    if (SeleniumExt.ErrorCapture.isNextTimeCapture) {
      SeleniumExt.ErrorCapture.capture(self.htmlTestCase);
      SeleniumExt.ErrorCapture.isNextTimeCapture = false;
    }
  }, 1);
  this.$testComplete();
}

