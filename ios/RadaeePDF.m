//
//  AlmaZBarReaderViewController.h
//  Paolo Messina
//
//  Created by Paolo Messina on 06/07/15.
//
//

#import "RadaeePDF.h"
#import "RDPDFViewController.h"


#pragma mark - Synthesize

@implementation RadaeePDF
@synthesize cdv_command;


#pragma mark - Cordova Plugin

- (void)pluginInitialize
{
    showPdfInProgress = NO;
}

#pragma mark - Plugin API

- (void)show: (CDVInvokedUrlCommand*)command;
{
    self.cdv_command = command;
    if(showPdfInProgress){
        [self.commandDelegate
         sendPluginResult: [CDVPluginResult
                            resultWithStatus: CDVCommandStatus_ERROR
                            messageAsString:@"A pdf opening is already in progress."]
         callbackId: [command callbackId]];
    } else {
        showPdfInProgress = YES;
        // Get user parameters
        NSDictionary *params = (NSDictionary*) [cdv_command argumentAtIndex:0];
        
        if( m_pdf == nil )
        {
            m_pdf = [[RDPDFViewController alloc] initWithNibName:@"RDPDFViewController" bundle:nil];
        }
        [m_pdf setData:params];
        [m_pdf setDelegate:self];
        
        m_pdf.hidesBottomBarWhenPushed = YES;
        [self.viewController presentViewController:m_pdf animated:YES completion:nil];
    }
}

- (void)chargePdfSendResult: (CDVPluginResult*)result
{
    showPdfInProgress = NO;
    m_pdf = nil;
    [self.commandDelegate sendPluginResult: result callbackId: [self.cdv_command callbackId]];
}

- (void)pdfChargeDidFinishLoading:(int)lenght{
    [self chargePdfSendResult:[CDVPluginResult
                               resultWithStatus: CDVCommandStatus_OK
                               messageAsString:@"Pdf Succesfully charged"]];
}

- (void)pdfChargeDidFailWithError:(NSString*)errorMessage andCode:(int)statusCode{
    showPdfInProgress = NO;
    [m_pdf dismissViewControllerAnimated:YES completion:nil];
    NSDictionary *dict = @{
                                @"errorMessage" : errorMessage,
                                @"statusCode" : [NSNumber numberWithInt:statusCode]
                                };
    [self chargePdfSendResult:[CDVPluginResult
                               resultWithStatus: CDVCommandStatus_ERROR
                               messageAsDictionary:dict]];
}

@end

