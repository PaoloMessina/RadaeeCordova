# RadaeeCordova Plugin

This plugin use the RadaeePDF native library for open PDF files, the plugin wrap the most important library features.

## Installation

    cordova plugins install it.almaviva.cordovaplugins.radaeepdf
    
## License

This plugin is released under the Apache 2.0 license

**Only the plugin source code is under the license Apache 2.0, the library included in the plugin follow the license of his owner, please check it on:**
http://www.radaeepdf.com/ecommerce/technical-specification

## API

### Scan barcode

	radaeePdf.open(params, success, error);
	
### Arguments:


Status:

- Android: DONE
- iOS: DONE

Quirks:

- __Android__: The app must have installed at least one pdf reader.
