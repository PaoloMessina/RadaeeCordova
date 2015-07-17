#import <Cordova/CDV.h>

@interface RadaeePDF : CDVPlugin{
    CDVInvokedUrlCommand* command;
}

@property (nonatomic, retain) CDVInvokedUrlCommand *command;

- (void)show: (CDVInvokedUrlCommand*)command;

@end
