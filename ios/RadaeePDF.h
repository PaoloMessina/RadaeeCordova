#import <Cordova/CDV.h>

@class RDPDFViewController;

@interface RadaeePDF : CDVPlugin{
    bool showPdfInProgress;
    CDVInvokedUrlCommand* cdv_command;
    RDPDFViewController *m_pdf;
}

@property (nonatomic, retain) CDVInvokedUrlCommand *cdv_command;

- (void)show: (CDVInvokedUrlCommand*)command;

@end
