//
//  RDPDFViewController.m
//  RadaeePdfDemo
//
//  Created by Paolo Messina on 17/07/15.
//
//

#import "RDPDFViewController.h"

@implementation RDPDFViewController
@synthesize pagecount;

-(int)PDFopenMem : (void *)data : (int)data_size :(NSString *)pwd
{
    [self PDFClose];
    PDF_ERR err = 0;
    m_doc = [[PDFDoc alloc] init];
    err = [m_doc openMem:data :data_size :pwd];
    switch( err )
    {
        case err_ok:
            break;
        case err_password:
            return 2;
            break;
        default: return 0;
    }
    CGRect rect = [[UIScreen mainScreen]bounds];
    
    //GEAR
    if (![self isPortrait] && rect.size.width < rect.size.height) {
        float height = rect.size.height;
        rect.size.height = rect.size.width;
        rect.size.width = height;
    }
    //END
    if(SYS_VERSION>=7.0)
    {
        m_view = [[PDFView alloc] initWithFrame:CGRectMake(0, 0, rect.size.width, rect.size.height)];
    }
    else
    {
        m_view = [[PDFView alloc] initWithFrame:CGRectMake(0, 0, rect.size.width, rect.size.height-20-44)];
    }
    [m_view vOpen :m_doc :(id<PDFViewDelegate>)self];
    pagecount =[m_doc pageCount];
    [self.view addSubview:m_view];
    m_bSel = false;
    return 1;
}

- (void)PDFClose
{
    if( m_view != nil ) {
        [m_view vClose];
        [m_view removeFromSuperview]; m_view = NULL;
    }
    m_doc = NULL;
}

- (BOOL)isPortrait
{
    return ([[UIApplication sharedApplication] statusBarOrientation] == UIInterfaceOrientationPortrait || [[UIApplication sharedApplication] statusBarOrientation] == UIInterfaceOrientationPortraitUpsideDown);
}

- (void)OnPageChanged :(int)pageno{}
- (void)OnLongPressed:(float)x :(float)y{}
- (void)OnSingleTapped:(float)x :(float)y :(NSString *)text{} - (void)OnTouchDown: (float)x :(float)y{}
- (void)OnTouchUp:(float)x :(float)y{}
- (void)OnSelEnd:(float)x1 :(float)y1 :(float)x2 :(float)y2{}
- (void)OnOpenURL:(NSString*)url{}
- (void)OnFound:(bool)found{}
- (void)OnMovie:(NSString *)fileName{}
- (void)OnSound:(NSString *)fileName{}

@end
