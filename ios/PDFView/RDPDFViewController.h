//
//  RDPDFViewController.h
//  RadaeePdfDemo
//
//  Created by Paolo Messina on 17/07/15.
//
//

#import <Foundation/Foundation.h>
#define SYS_VERSION [[[UIDevice currentDevice]systemVersion] floatValue]

#import <UIKit/UIKit.h>
#import "PDFIOS.h"
#import "PDFView.h"
#import <CoreData/CoreData.h> 

@class PDFV;
@class PDFView;

@interface RDPDFViewController : UIViewController {
    PDFView *m_view;
    PDFDoc *m_doc;
    bool m_bSel;
}

@property (assign, nonatomic)int pagecount;

-(int)PDFopenMem : (void *)data : (int)data_size :(NSString *)pwd;

@end
