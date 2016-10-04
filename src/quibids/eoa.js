var page = require('webpage').create(),
system = require('system'),
url;
page.settings.resourceTimeout = 180000;  //timeout 180s
url = system.args[1];

page.onResourceTimeout = function(e) {
    console.log(e.errorCode);   // it'll probably be 408
    console.log(e.errorString); // it'll probably be 'Network timeout on resource'
    console.log(e.url);         // the url whose request timed out
    phantom.exit(1);
};

page.open(url, function(status) {
  if(status === "success") {
    var realbids = page.evaluate(function() {
		return document.getElementById("breakdown_realbids").innerText;
	});
	
	var voucherbids = page.evaluate(function() {
		return document.getElementById("breakdown_voucherbids").innerText;
	});
	
	var endtime = page.evaluate(function() {
		return document.getElementById("end-time-disclaim").getElementsByClassName("light-grey bold")[1].innerText;
	});
	
	console.log(realbids);
	console.log(voucherbids);
	console.log(endtime)
	
  }
  phantom.exit();
});