//
//  RadaePdfViewController.h
//  RadaeePdfDemo
//
//  Created by Paolo Messina on 17/07/15.
//
//

#import <UIKit/UIKit.h>
@class RDPDFViewController;

@interface RadaePdfViewController : UIViewController{
    RDPDFViewController *m_pdf;
    void *buffer;
}

@end
