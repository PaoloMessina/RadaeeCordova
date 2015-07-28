//
//  RadaePdfViewController.m
//  RadaeePdfDemo
//
//  Created by Paolo Messina on 17/07/15.
//
//

#import "RadaePdfViewController.h"
#import "RDPDFViewController.h"

@interface RadaePdfViewController ()

@end

@implementation RadaePdfViewController

NSUserDefaults *userDefaults;
bool g_CaseSensitive = false;
bool g_MatchWholeWord = false;
bool g_ScreenAwake = false;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)loadSettingsWithDefaults
{
    userDefaults = [NSUserDefaults standardUserDefaults];
    g_CaseSensitive = [userDefaults boolForKey:@"CaseSensitive"];
    g_MatchWholeWord = [userDefaults boolForKey:@"MatchWholeWord"];
    g_ScreenAwake = [userDefaults boolForKey:@"KeepScreenAwake"];
    [[UIApplication sharedApplication] setIdleTimerDisabled:g_ScreenAwake];
    g_MatchWholeWord = [userDefaults floatForKey:@"MatchWholeWord"];
    
    g_CaseSensitive = [userDefaults floatForKey:@"CaseSensitive"];

    g_def_view = [userDefaults integerForKey:@"ViewMode"];
    g_ink_color = [userDefaults integerForKey:@"InkColor"];
    if(g_ink_color ==0)
    {
        g_ink_color =0xFF0000FF;
    }
    g_Ink_Width = 2.0f;
    g_rect_color = [userDefaults integerForKey:@"RectColor"];
    if(g_rect_color==0)
    {
        g_rect_color =0xFF0000FF;
    }
    annotUnderlineColor = [userDefaults integerForKey:@"UnderlineColor"];
    if (annotUnderlineColor == 0) {
        annotUnderlineColor = 0xFF0000FF;
    }
    annotStrikeoutColor = [userDefaults integerForKey:@"StrikeoutColor"];
    if (annotStrikeoutColor == 0) {
        annotStrikeoutColor = 0xFFFF0000;
    }
    annotHighlightColor = [userDefaults integerForKey:@"HighlightColor"];
    if(annotHighlightColor ==0)
    {
        annotHighlightColor =0xFFFFFF00;
    }
    g_oval_color =[userDefaults integerForKey:@"OvalColor"]; if(g_oval_color ==0)
    {
        g_oval_color =0xFFFFFF00;
    }
}

/*- (void)openPDF:(id)sender
{
    [self loadSettingsWithDefaults];
    RDPDFViewController *m_pdf;
    if( m_pdf == nil )
    {
        m_pdf = [[RDPDFViewController alloc] initWithNibName:@"RDPDFViewController"bundle:nil];
    }
    //pdfName = @"PianoTerapeutico2.pdf";
    int result = [m_pdf PDFOpen:[[NSBundle mainBundle] pathForResource:@"PianoTerapeutico2"ofType:@"pdf"] withPassword:@""];
    if(result == 1)
    {
        m_pdf.hidesBottomBarWhenPushed = YES;
        [self.navigationController pushViewController:m_pdf animated:YES];
    }
}*/

- (void)openPDF:(id)sender
{
    if( m_pdf == nil )
    {
        m_pdf = [[RDPDFViewController alloc] initWithNibName:@"RDPDFViewController" bundle:nil];
    }
    
    PDFDoc *doc_dst = [[PDFDoc alloc] init];
    PDFDoc *doc_src = [[PDFDoc alloc] init];
    
    PDFImportCtx *ctx = [doc_dst newImportCtx:doc_src];
    int dstno = [doc_dst pageCount];
    int srccount= [doc_src pageCount];
    int srcno = 0;
    while (srcno <srccount) {
        [ctx import :srcno :dstno];
        dstno++;
        srcno++;
    }
    [ctx importEnd];
    [doc_dst save];
    
    
    NSFileManager *fm = [NSFileManager defaultManager];
    //Open PDF from Mem demo
    char *path1 = [[[NSBundle mainBundle]pathForResource:@"PianoTerapeutico2" ofType:@"pdf" inDirectory:@"fdat"] UTF8String];
    FILE *file1 = fopen(path1, "rb");
    fseek(file1, 0, SEEK_END);
    int filesize1 = ftell(file1);
    fseek(file1, 0, SEEK_SET);
    
    
    buffer = malloc((filesize1)*sizeof(char));
    fread(buffer, filesize1, 1, file1);
    fclose(file1);
    
    [m_pdf PDFopenMem: buffer :filesize1 :nil];
    
    
    
    //Open PDF from FileStream demo
    //stream = [[PDFFileStream alloc] init];
    
    
    //Open PDF HTTPStream demo
    //@testUrlPath: the http pdf path you requested,this url needs support [NSURLRequest forHTTPHeaderField:@"Range"] method
    //@testfile : You need set a temp path in sandbox to save the request file
    // httpStream = [[PDFHttpStream alloc] init];
    // [httpStream open:testUrlPath :testfile];
    // [m_pdf PDFOpenStream:httpStream :@""];
    
    
    UINavigationController *nav = self.navigationController;
    m_pdf.hidesBottomBarWhenPushed = YES;
    [nav pushViewController:m_pdf animated:YES];
    
    //use PDFopenMem ,here need release memory
    //free(buffer);
    
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
