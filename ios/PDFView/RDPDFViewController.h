//
//  RDPDFViewController.h
//  PDFViewer
//
//  Created by Radaee on 12-10-29.
//  Copyright (c) 2012年 Radaee. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PDFView.h"
#import "PDFIOS.h"

@class PDFView;
@class PDFV;
@class RDPDFViewController;

// define the protocol for the delegate
@protocol RDPDFViewControllerDelegate
// define protocol functions that can be used in any class using this delegate
- (void)pdfChargeDidFinishLoading:(int)dim;
- (void)pdfChargeDidFailWithError:(NSString*)error andCode:(int)errorCode;
@end;

@interface RDPDFViewController : UIViewController
{
    PDFView *m_view;
    PDFDoc *m_doc;
    
    NSURLConnection *pdfConn;
    NSString *url;
    NSMutableData *receivedData;
    void *buffer;
}

@property (nonatomic, weak) IBOutlet UIView *container;
@property (nonatomic, weak) IBOutlet UILabel *titleLabel;
@property (nonatomic, weak) IBOutlet UIButton *closeButton;
@property (nonatomic, weak) IBOutlet UIButton *backButton;
@property (nonatomic, weak) IBOutlet UIView *barView;

@property (nonatomic, retain) NSDictionary *data;

-(IBAction) close:(id)sender;

-(int)PDFOpen:(NSString *)path :(NSString *)pwd;
-(int)PDFopenMem : (void *)data : (int)data_size :(NSString *)pwd;
-(int)PDFOpenStream :(id<PDFStream>)stream :(NSString *)password;

-(void)PDFClose;

-(BOOL)isPortrait;

// define delegate property
@property (nonatomic, assign) id  delegate;

@end
