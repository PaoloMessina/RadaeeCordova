/**
 * Created by p.messina on 14/10/2015.
 */

module.exports = function (ctx) {

    if (ctx.opts.platforms.indexOf('ios') < 0) {
        return;
    }
    var fs = ctx.requireCordovaModule('fs'),
        path = ctx.requireCordovaModule('path'),
        deferral = ctx.requireCordovaModule('q').defer();

    function replace_string_in_file(filename, to_replace, replace_with) {
        var data = fs.readFileSync(filename, 'utf8');
        var result = data.replace(new RegExp(to_replace, "g"), replace_with);
        fs.writeFileSync(filename, result, 'utf8');
    }
    
    /**
     * return the config.xml project id
    */
    function getConfidId(configString){
    	var firstCut = configString.split(" id=");
		//console.log(firstCut);
		var secondCut = firstCut[1].replace(/"/g,"");
		//console.log(secondCut);
		var id = secondCut.slice(0,secondCut.indexOf(" "));
		//console.log(id);
		return id;
    }
    
    /**
     * return the config.xml project name
    */
    function getConfigName(configString){
    	var startCut = cfg.indexOf("<name>") + 6;
		var stopCut = cfg.indexOf("</name>");
		var name = cfg.slice(startCut, stopCut);
		return name;
    }
    
    function isPresentString(fileName, stringToSearch){
    	var data = fs.readFileSync(filename, 'utf8');
    	return fs.search(stringToSearch) >= 0;
    }
    
    var platformRoot = path.join(ctx.opts.projectRoot, 'platforms/ios');
    var ourconfigfile = path.join(ctx.opts.projectRoot, "config.xml");
    var data = fs.readFileSync(ourconfigfile, 'utf8');
    var projectName = getConfigName(data);
    var pchFileName = projectName + "-Prefix.pch";
    var srcNativeRoot = path.join(platformRoot, projectName);
    
    var fileImportR = [
    	{filePath: pchFileName, toReplace: '#ifdef __OBJC__', replecement: '#ifdef __OBJC__\n#import "NSObject+PerformBlock.h"'}/*,
    	{filePath: 'Classes/AppDelegate.h', toReplace: 'com.radaee.viewlib.R', replecement: ''},
    	{filePath: 'Classes/AppDelegate.m', toReplace: 'com.radaee.viewlib.R', replecement: ''}*/
    ];


    console.log('*****************************************');
    console.log('*       Inject Radaee configuration file iOS             *');
    console.log('*****************************************');
    
    fileImportR.forEach(function(val) {
    	var fullfilename = path.join(srcNativeRoot, val.filePath);
    	console.log('*  Inject in file: ' + fullfilename + '  *');
    	if (fs.existsSync(fullfilename)) {
    		if(!isPresentString(fullfilename, val.replecement)){
    			replace_string_in_file(fullfilename, val.toReplace, val.replecement);
    		}
    	} else {
            console.error('* missing file:', fullfilename);
        }
    });
}