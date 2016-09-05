var page = require('webpage').create(),
system = require('system'),
url;
url = system.args[1];
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