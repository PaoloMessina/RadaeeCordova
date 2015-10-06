#import <Cordova/CDV.h>

@class RDPDFViewController;

@interface RadaeePDF : CDVPlugin{
    bool showPdfInProgress;
    CDVInvokedUrlCommand* cdv_command;
    RDPDFViewController *m_pdf;
    
    NSURLConnection *pdfConn;
    NSString *url;
    NSMutableData *receivedData;
    void *buffer;
    NSInteger statusCode;
}

@property (nonatomic, retain) CDVInvokedUrlCommand *cdv_command;

- (void)show: (CDVInvokedUrlCommand*)command;

@end
