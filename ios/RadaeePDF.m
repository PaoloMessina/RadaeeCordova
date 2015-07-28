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
    
}

#pragma mark - Plugin API

- (void)show: (CDVInvokedUrlCommand*)command;
{
	self.cdv_command = command;
    
    // Get user parameters
    NSDictionary *params = (NSDictionary*) [cdv_command argumentAtIndex:0];

	if( m_pdf == nil )
    {
        m_pdf = [[RDPDFViewController alloc] initWithNibName:@"RDPDFViewController" bundle:nil];
    }
    
    //Open PDF from Mem demo
    char *path1 = [[[NSBundle mainBundle]pathForResource:@"PianoTerapeutico2" ofType:@"pdf"] UTF8String];
    FILE *file1 = fopen(path1, "rb");
    fseek(file1, 0, SEEK_END);
    int filesize1 = ftell(file1);
    fseek(file1, 0, SEEK_SET);
    
    
    buffer = malloc((filesize1)*sizeof(char));
    fread(buffer, filesize1, 1, file1);
    fclose(file1);
    
    [m_pdf PDFopenMem: buffer :filesize1 :nil]; 

    m_pdf.hidesBottomBarWhenPushed = YES;
    [self.viewController presentViewController:m_pdf animated:YES completion:nil];
    
    //use PDFopenMem ,here need release memory
    //free(buffer);

}

@end

