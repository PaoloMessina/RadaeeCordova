# RadaeeCordova Plugin

This plugin use the RadaeePDF native library for open PDF files, the plugin wrap the most important library features.
    
## License

This plugin is released under the Apache 2.0 license

**Only the plugin source code is under the license Apache 2.0, the library included in the plugin follow the license of his owner, please check it on:**
http://www.radaeepdf.com/ecommerce/technical-specification

## Installation

    cordova plugins install it.almaviva.cordovaplugins.radaeepdf
    
## Configuration

### iOS

	1. Add **#import "PDFVGlobal.h"** in your AppDelegate.h
	2. Add **AppInit();** in **- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions** of your AppDelegate.m
	3. Add **#import "NSObject+PerformBlock.h"** in you <Project_name>.pch file

### Android

## API

### Scan barcode

	radaeePdf.open(params, success, error);
	
### Arguments:

- **params**:

    ```javascript
    {
    	url: "MANDATORY the url of the remote pdf resource", 
		barColor: "OPTIONAL the color of the status/navigation bar in HEX format es: #AEC7F5", 
		showClose: "OPTIONAL specify if you want to show a close button, otherwise will be visible the bacj button default: true", 
		title: "OPTIONAL Navigation Bar title"
    }
    ```

- **onSuccess**: function (message) {...} _Callback for successful opening._
- **onFailure**: function (err) {...} _Callback for cancelled show or error._

Status:

- Android: DONE
- iOS: DONE

Quirks:

- __Android__: The app must have installed at least one pdf reader.
