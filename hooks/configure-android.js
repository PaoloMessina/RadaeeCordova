/**
 * Created by p.messina on 14/10/2015.
 */

module.exports = function (ctx) {

    if (ctx.opts.platforms.indexOf('android') < 0) {
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
    
    function getConfidId(configString){
    	if (window.DOMParser) {
    		parseXml = function(xmlStr) {
        		return ( new window.DOMParser() ).parseFromString(xmlStr, "text/xml");
    		};
		} else if (typeof window.ActiveXObject != "undefined" && new window.ActiveXObject("Microsoft.XMLDOM")) {
    		parseXml = function(xmlStr) {
        		var xmlDoc = new window.ActiveXObject("Microsoft.XMLDOM");
        		xmlDoc.async = "false";
        		xmlDoc.loadXML(xmlStr);
        		return xmlDoc;
    		};
		} else {
    		parseXml = function() { return null; }
		}

		var xmlDoc = parseXml(config);
		if (xmlDoc) {
    		return xmlDoc.documentElement.getAttribute("id");
		}
    }
    
    var ourconfigfile = path.join(ctx.opts.projectRoot, "config.xml");
    var configXMLPath = "config.xml";
    var data = fs.readFileSync(ourconfigfile, 'utf8');
    
    //var replaceWith = "it.almaviva.radaeepdfdemo.R;";
    var replaceWith = getConfidId(data) + ".R";
    
    var platformRoot = path.join(ctx.opts.projectRoot, 'platforms/android');
    var fileImportR = [
    	{filePath: 'src/com/radaee/pdf/Global.java', importStatement: 'com.radaee.viewlib.R'},
    	{filePath: 'src/com/radaee/reader/PDFCurlViewAct.java', importStatement: 'com.radaee.viewlib.R'},
    	{filePath: 'src/com/radaee/reader/PDFLayoutView.java', importStatement: 'com.radaee.viewlib.R'},
    	{filePath: 'src/com/radaee/reader/PDFNavAct.java', importStatement: 'com.radaee.viewlib.R'},
    	{filePath: 'src/com/radaee/reader/PDFViewAct.java', importStatement: 'com.radaee.viewlib.R'},
    	{filePath: 'src/com/radaee/reader/PDFViewController.java', importStatement: 'com.radaee.viewlib.R'},
    	{filePath: 'src/com/radaee/util/OutlineListAdt.java', importStatement: 'com.radaee.viewlib.R'},
    	{filePath: 'src/com/radaee/util/PDFGridItem.java', importStatement: 'com.radaee.viewlib.R'},
    	{filePath: 'src/it/almaviva/cordovaplugins/ReaderActivity.java', importStatement: 'it.almaviva.radaeepdfdemo.R'}
    ];


    console.log('*****************************************');
    console.log('*       inject file R  ANDROID             *');
    console.log('*****************************************');
    console.log('*       Inject: ' + replaceWith + '    *');
    
    fileImportR.forEach(function(val) {
    	var fullfilename = path.join(platformRoot, val.filePath);
    	console.log('*  Inject in file: ' + fullfilename + ' the import statemet: ' + val.importStatement + '  *');
    	if (fs.existsSync(fullfilename)) {
    		replace_string_in_file(fullfilename, val.importStatement, replaceWith);
    	} else {
            console.error('* missing file:', fullfilename);
        }
    });
}