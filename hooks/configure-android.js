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
    
    var cordova_util = ctx.requireCordovaModule("cordova-lib/src/cordova/util"),
        ConfigParser = ctx.requireCordovaModule("cordova-lib/src/configparser/ConfigParser")
        projectRoot = cordova_util.isCordova(),
        xml = cordova_util.projectConfig(projectRoot),
        cfg = new ConfigParser(xml);
    
    var ourconfigfile = path.join(ctx.opts.projectRoot, "config.xml");
    var configXMLPath = "config.xml";
    var replaceWith = cfg.widget.$.id;
    
    var platformRoot = path.join(ctx.opts.projectRoot, 'platforms/android');
    var fileImportR = [
    	{filePath: 'src/com/radaee/cordova/Global.java', importStatement: 'import com.radaee.viewlib.R;'},
    	{filePath: 'src/com/radaee/reader/PDFCurlViewAct.java', importStatement: 'import com.radaee.viewlib.R;'},
    	{filePath: 'src/com/radaee/reader/PDFLayoutView.java', importStatement: 'import com.radaee.viewlib.R;'},
    	{filePath: 'src/com/radaee/reader/PDFNavAct.java', importStatement: 'import com.radaee.viewlib.R;'},
    	{filePath: 'src/com/radaee/reader/PDFViewAct.java', importStatement: 'import com.radaee.viewlib.R;'},
    	{filePath: 'src/com/radaee/reader/PDFViewController.java', importStatement: 'import com.radaee.viewlib.R;'},
    	{filePath: 'src/com/radaee/util/OutlineListAdt.java', importStatement: 'import com.radaee.viewlib.R;'},
    	{filePath: 'src/com/radaee/util/PDFGridItem.java', importStatement: 'import com.radaee.viewlib.R;'},
    	{filePath: 'src/it/almaviva/cordovaplugins/ReaderActivity.java', importStatement: 'import it.almaviva.radaeepdfdemo.R;;'}
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