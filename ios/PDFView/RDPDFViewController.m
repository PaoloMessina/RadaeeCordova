//
//  RDPDFViewController.m
//  PDFViewer
//
//  Created by Radaee on 12-10-29.
//  Copyright (c) 2012年 Radaee. All rights reserved.
//

#import "RDPDFViewController.h"

#define SYS_VERSION [[[UIDevice currentDevice]systemVersion] floatValue]

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface RDPDFViewController ()


@end

@implementation RDPDFViewController
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
    
    bool showClose = [[self.data objectForKey:@"showClose"] boolValue];
    NSString *title = [self.data objectForKey:@"title"];
    url = [self.data objectForKey:@"url"];
    
    b_outline = false;
    
    if ([self respondsToSelector:@selector(automaticallyAdjustsScrollViewInsets)]) {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
    
    // View defaults to full size.  If you want to customize the view's size, or its subviews (e.g. webView),
    // you can do so here.
    // Lower screen 20px on ios 7
    if (SYS_VERSION>=7.0) {
        CGRect viewBounds = [self.container bounds];
        viewBounds.origin.y = 20;
        viewBounds.size.height = viewBounds.size.height - 20;
        self.container.frame = viewBounds;
    }
    NSString *barColor = [self.data objectForKey:@"barColor"];
    if([barColor containsString:@"#"])barColor = [barColor stringByReplacingOccurrencesOfString:@"#" withString:@""];
    unsigned int baseValue;
    [[NSScanner scannerWithString:barColor] scanHexInt:&baseValue];
    self.view.backgroundColor = UIColorFromRGB(baseValue);
    self.barView.backgroundColor = UIColorFromRGB(baseValue);
    self.backButton.hidden = showClose;
    self.closeButton.hidden = !showClose;
    self.titleLabel.text = title;
    
    /*NSString* pdfData = [self.data objectForKey:@"pdfData"];
    int len = (int)[pdfData length];
    Byte* byteArr = (Byte*)malloc(len);
    byteArr = [pdfData get];
    int result = [self PDFopenMem:byteArr :len :nil];*/
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc]
                                    initWithURL:[NSURL
                                                 URLWithString:url]];
    
    [request setHTTPMethod:@"GET"];
    //[request setValue:@"application/json"
    //forHTTPHeaderField:@"Content-type"];
    
    //NSString *jsonString = @"{}";
    
    //[request setValue:[NSString stringWithFormat:@"%lu",(unsigned long)[jsonString length]] forHTTPHeaderField:@"Content-length"];
    
    //[request setHTTPBody:[jsonString dataUsingEncoding:NSUTF8StringEncoding]];
    receivedData = [[NSMutableData alloc] init];
    
    
    
    NSURLConnection *cmdConn = [[NSURLConnection alloc] initWithRequest:request delegate:self];
    
    //Open PDF from Mem demo
    /*char *path1 = [[[NSBundle mainBundle]pathForResource:@"PianoTerapeutico2" ofType:@"pdf"] UTF8String];
    FILE *file1 = fopen(path1, "rb");
    fseek(file1, 0, SEEK_END);
    int filesize1 = ftell(file1);
    fseek(file1, 0, SEEK_SET);
    
    buffer = malloc((filesize1)*sizeof(char));
    fread(buffer, filesize1, 1, file1);
    fclose(file1);
    
    [self PDFopenMem: buffer :filesize1 :nil];*/
    
    //use PDFopenMem ,here need release memory
    //free(buffer);
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Append the new data to receivedData.
    [receivedData appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // do something with the data, for example log:
    int len = (int)[receivedData length];
    Byte *byteData = (Byte*)malloc(len);
    memcpy(byteData, [receivedData bytes], len);
    int result = [self PDFopenMem:byteData :len :nil];
    NSLog(@"%d", result);
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
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

-(BOOL)isPortrait
{
    return ([[UIApplication sharedApplication] statusBarOrientation] == UIInterfaceOrientationPortrait ||
            [[UIApplication sharedApplication] statusBarOrientation] == UIInterfaceOrientationPortraitUpsideDown);
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
    
    CGRect barViewRect = [self.barView bounds];
    float height = rect.size.height - 20 - barViewRect.size.height;
    CGRect newrect = CGRectMake(0, barViewRect.size.height, rect.size.width, height);
    
    [m_view setFrame:newrect];
    [m_view sizeThatFits:newrect.size];
    
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
    CGRect barViewRect = [self.barView bounds];
    float height = rect.size.height - barViewRect.size.height;
    if(SYS_VERSION >= 7.0) height = height - 20;
    
    m_view = [[PDFView alloc] initWithFrame:CGRectMake(0, barViewRect.size.height, rect.size.width, height)];
    [m_view vOpen :m_doc :(id<PDFViewDelegate>)self];
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
    CGRect barViewRect = [self.barView bounds];
    float height = rect.size.height - barViewRect.size.height;
    if(SYS_VERSION >= 7.0) height = height - 20;
    
    m_view = [[PDFView alloc] initWithFrame:CGRectMake(0, barViewRect.size.height, rect.size.width, height)];
    [m_view vOpen:m_doc: (id<PDFViewDelegate>)self];
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
    CGRect barViewRect = [self.barView bounds];
    float height = rect.size.height - barViewRect.size.height;
    if(SYS_VERSION >= 7.0) height = height - 20;
    
    m_view = [[PDFView alloc] initWithFrame:CGRectMake(0, barViewRect.size.height, rect.size.width, height)];
    [m_view vOpen :m_doc :(id<PDFViewDelegate>)self];
    [self.container addSubview:m_view];
    return 1;
}

-(IBAction) close:(id)sender{
    [self dismissViewControllerAnimated:YES completion:nil];
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

-(void)OnPageChanged:(int)pageno {}

-(void)OnSingleTapped:(float)x :(float)y {}

-(void)OnDoubleTapped:(float)x :(float)y {}

-(void)OnLongPressed:(float)x :(float)y {}

-(void)OnSingleTapped:(float)x :(float)y :(NSString *)text {}

-(void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
