//
//  RDPDFViewController.m
//  PDFViewer
//
//  Created by Radaee on 12-10-29.
//  Copyright (c) 2012年 Radaee. All rights reserved.
//

#import "RDPDFViewController.h"

#define SYS_VERSION [[[UIDevice currentDevice]systemVersion] floatValue]

@interface RDPDFViewController ()


@end

@implementation RDPDFViewController
@synthesize window = _window;
@synthesize pagecount;
extern NSUserDefaults *userDefaults;
bool b_outline;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self = [super initWithNibName:nil bundle:nil]) {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    b_outline = false;
    
    if ([self respondsToSelector:@selector(automaticallyAdjustsScrollViewInsets)]) {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
}

-(void)viewWillAppear:(BOOL)animated
{
}

-(void)viewWillDisappear:(BOOL)animated
{
    if(!b_outline)
    {
        //[m_ThumbView vClose] should before [m_view vClose]
        [m_view vClose];
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"changeOrientation" object:nil];
    return YES;
}

- (BOOL)shouldAutorotate
{
    return YES;
}

-(NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskAllButUpsideDown;
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation
{
    CGRect rect =[[UIScreen mainScreen]bounds];
    if ([self isPortrait])
    {
        if (rect.size.height < rect.size.width) {
            
            float height = rect.size.height;
            rect.size.height = rect.size.width;
            rect.size.width = height;
        }
    }
    else
    {
        if (rect.size.height > rect.size.width) {
            
            float height = rect.size.height;
            rect.size.height = rect.size.width;
            rect.size.width = height;
        }
    }
    
    [m_view setFrame:rect];
    [m_view sizeThatFits:rect.size];
    
    CGRect boundsc = [[UIScreen mainScreen]bounds];
    int cwidth = boundsc.size.width;
    int cheight = boundsc.size.height;
    
    if ([self isPortrait]) {
        if (cwidth > cheight) {
            cwidth = cheight;
            cheight = boundsc.size.width;
        }
    }
    else
    {
        if (cwidth < cheight) {
            cwidth = cheight;
            cheight = boundsc.size.width;
        }
    }
    
    [m_view resetZoomLevel];
}

-(int)PDFOpen:(NSString *)path : (NSString *)pwd
{
    [self PDFClose];
    PDF_ERR err = 0;
    m_doc = [[PDFDoc alloc] init];
    err = [m_doc open:path :pwd];
    
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
    return 1;
}

-(int)PDFOpenStream:(id<PDFStream>)stream :(NSString *)password
{
    [self PDFClose];
    PDF_ERR err = 0;
    m_doc = [[PDFDoc alloc] init];
    // err = [m_doc open:path :pwd];
    err = [m_doc openStream:stream :password];
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
    [m_view vOpen:m_doc: (id<PDFViewDelegate>)self];
    pagecount =[m_doc pageCount];
    [self.view addSubview:m_view];
    return 1;
}

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
    return 1;
}

-(void)PDFClose
{
    if( m_view != nil )
    {
        [m_view vClose];
        [m_view removeFromSuperview];
        m_view = NULL;
    }
    m_doc = NULL;
}

-(void)OnPageChanged :(int)pageno {}

-(void)OnSingleTapped:(float)x :(float)y {}

-(void)OnDoubleTapped:(float)x :(float)y {}

-(void)OnLongPressed:(float)x :(float)y {}

-(void)OnSingleTapped:(float)x :(float)y :(NSString *)text {}

-(void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(BOOL)isPortrait
{
    return ([[UIApplication sharedApplication] statusBarOrientation] == UIInterfaceOrientationPortrait ||
            [[UIApplication sharedApplication] statusBarOrientation] == UIInterfaceOrientationPortraitUpsideDown);
}

@end
