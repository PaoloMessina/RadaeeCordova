#import <Cordova/CDV.h>

@interface RadaeePDF : CDVPlugin{
    CDVInvokedUrlCommand* command;
    RDPDFViewController *m_pdf;
    void *buffer;
}

@property (nonatomic, retain) CDVInvokedUrlCommand *command;

- (void)show: (CDVInvokedUrlCommand*)command;

@end
