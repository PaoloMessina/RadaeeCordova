cordova.define("it.almaviva.cordovaplugins.radaeepdf", function(require, exports, module) { //
//  AlmaZBarReaderViewController.h
//  Paolo Messina
//
//  Created by Paolo Messina on 06/07/15.
//
//

var argscheck = require('cordova/argscheck'),
    exec      = require('cordova/exec');

function radaeePdf () {};

radaeePdf.prototype = {

    open: function (params, success, failure)
    {
        argscheck.checkArgs('*fF', 'RadaeePDF.show', arguments);

        params = params || {};
        if(params.text_title === undefined) params.text_title = "";
        if(params.showClose === undefined) params.showClose = false;
        if(params.barColor == undefined) params.barColor = "#FFFFFF";

        exec(success, failure, 'RadaeePDF', 'show', [params]);
    }
};

module.exports = new radaeePdf();

});
