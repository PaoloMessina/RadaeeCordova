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
        url = [params objectForKey:@"url"];
        
        if(![[NSURL URLWithString:url] isFileURL]){
            NSDictionary* header = [params objectForKey:@"headerParams"];
            NSMutableURLRequest *request = [[NSMutableURLRequest alloc]
                                            initWithURL:[NSURL
                                                         URLWithString:url]];
            [request setHTTPMethod:@"GET"];
            if(header){
                for(NSString *key in [header allKeys])
                {
                    NSString *value = header[key]; // assuming the value is indeed a string
                    [request setValue:value forHTTPHeaderField:key];
                }
            }
            
            //NSString *jsonString = @"{}";
            //[request setValue:[NSString stringWithFormat:@"%lu",(unsigned long)[jsonString length]] forHTTPHeaderField:@"Content-length"];
            //[request setHTTPBody:[jsonString dataUsingEncoding:NSUTF8StringEncoding]];
            
            receivedData = [[NSMutableData alloc] init];
            pdfConn = [[NSURLConnection alloc] initWithRequest:request delegate:self];
        } else {
            /*NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
             NSString *documentsDirectory = [paths objectAtIndex:0];
             NSString *filePath = [documentsDirectory stringByAppendingPathComponent:@"filePdfTest.pdf"];*/
            
            NSData *data = [[NSFileManager defaultManager] contentsAtPath:[url stringByReplacingOccurrencesOfString:@"file://" withString:@""]];
            int len = (int)[data length];
            Byte *byteData = (Byte*)malloc(len);
            memcpy(byteData, [data bytes], len);
            
            if( m_pdf == nil )
            {
                m_pdf = [[RDPDFViewController alloc] initWithNibName:@"RDPDFViewController" bundle:nil];
            }
            [m_pdf setData:params];
            [m_pdf setByteData:byteData];
            [m_pdf setByteDataLenght:len];
            [m_pdf setDelegate:self];
            
            m_pdf.hidesBottomBarWhenPushed = YES;
            [self.viewController presentViewController:m_pdf animated:YES completion:nil];
        }
        
        
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append the new data to receivedData.
    [receivedData appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    if (statusCode != 200)
    {
        [connection cancel];  // stop connecting; no more delegate messages
        //NSLog(@"didReceiveResponse statusCode with %i", statusCode);
        NSString * error = [[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding];
        [self pdfChargeDidFailWithError:error andCode:statusCode];
        return;
    } else {
        // do something with the data, for example log:
        int len = (int)[receivedData length];
        [self pdfChargeDidFinishLoading:len];
        Byte *byteData = (Byte*)malloc(len);
        memcpy(byteData, [receivedData bytes], len);
        
        NSDictionary *params = (NSDictionary*) [cdv_command argumentAtIndex:0];
        
        if( m_pdf == nil )
        {
            m_pdf = [[RDPDFViewController alloc] initWithNibName:@"RDPDFViewController" bundle:nil];
        }
        [m_pdf setData:params];
        [m_pdf setByteData:byteData];
        [m_pdf setByteDataLenght:len];
        [m_pdf setDelegate:self];
        
        m_pdf.hidesBottomBarWhenPushed = YES;
        [self.viewController presentViewController:m_pdf animated:YES completion:nil];
    }
}

/*- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
 {
 // Release the connection and the data object
 // by setting the properties (declared elsewhere)
 // to nil.  Note that a real-world app usually
 // requires the delegate to manage more than one
 // connection at a time, so these lines would
 // typically be replaced by code to iterate through
 // whatever data structures you are using.
 pdfConn = nil;
 receivedData = nil;
 
 // inform the user
 NSLog(@"Connection failed! Error - %@ %@",
 [error localizedDescription],
 [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
 
 //[delegate pdfChargedidFailWithError:[error localizedDescription] andCode:[[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]];
 }*/

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    if ([response respondsToSelector:@selector(statusCode)])
    {
        statusCode = [((NSHTTPURLResponse *)response) statusCode];
        /*if (statusCode > 200)
         {
         //[connection cancel];  // stop connecting; no more delegate messages
         NSLog(@"didReceiveResponse statusCode with %i", statusCode);
         [delegate pdfChargeDidFailWithError:@"Error chargin pdf" andCode:statusCode];
         [self dismissViewControllerAnimated:YES completion:nil];
         }*/
    }
}

- (void)chargePdfSendResult: (CDVPluginResult*)result
{
    showPdfInProgress = NO;
    m_pdf = nil;
    [self.commandDelegate sendPluginResult: result callbackId: [self.cdv_command callbackId]];
}

- (void)pdfChargeDidFinishLoading:(int)lenght{
    showPdfInProgress = NO;
    [self chargePdfSendResult:[CDVPluginResult
                               resultWithStatus: CDVCommandStatus_OK
                               messageAsString:@"Pdf Succesfully charged"]];
}

- (void)pdfChargeDidFailWithError:(NSString*)errorMessage andCode:(NSInteger)statusCode{
    showPdfInProgress = NO;
    //if(m_pdf)
        //[m_pdf dismissViewControllerAnimated:YES completion:nil];
    NSDictionary *dict = @{
                                @"errorMessage" : errorMessage,
                                @"statusCode" : [NSNumber numberWithInteger:statusCode]
                                };
    [self chargePdfSendResult:[CDVPluginResult
                               resultWithStatus: CDVCommandStatus_ERROR
                               messageAsDictionary:dict]];
}

@end

