#import <Cordova/CDV.h>

@class RDPDFViewController;

@interface RadaeePDF : CDVPlugin{
    CDVInvokedUrlCommand* cdv_command;
    RDPDFViewController *m_pdf;
    void *buffer;
}

@property (nonatomic, retain) CDVInvokedUrlCommand *cdv_command;

- (void)show: (CDVInvokedUrlCommand*)command;

@end
