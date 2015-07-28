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
#import <CoreData/CoreData.h>

@class PDFView;
@class PDFV;

@interface RDPDFViewController : UIViewController
{
    PDFView *m_view;
    PDFDoc *m_doc;
}
@property (strong, nonatomic) UIWindow *window;
@property (assign, nonatomic)int pagecount;

-(int)PDFOpen:(NSString *)path :(NSString *)pwd;
-(int)PDFopenMem : (void *)data : (int)data_size :(NSString *)pwd;
-(int)PDFOpenStream :(id<PDFStream>)stream :(NSString *)password;

-(void)PDFClose;

-(BOOL)isPortrait;

@end
