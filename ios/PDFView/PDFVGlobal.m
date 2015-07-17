//
//
//  PDFVGlobal.m
//  PDFViewer
//
//  Created by Radaee on 13-5-26.
//
//

//  PDFViewer
//
//  Created by Radaee on 13-5-26.
//
//

#import "PDFVGlobal.h"

@implementation PDFVLocker
-(id)init
{
    if( self = [super init] )
    {
        pthread_mutex_init( &mutex, NULL );
    }
    return self;
}
-(void)dealloc
{
    pthread_mutex_destroy( &mutex );
}
-(void)lock
{
    pthread_mutex_lock( &mutex );
}
-(void)unlock
{
    pthread_mutex_unlock( &mutex );
}
@end

@implementation PDFVEvent
-(id)init
{
    if( self = [super init] )
    {
        pthread_cond_init( &m_event, NULL );
        pthread_mutex_init( &mutex, NULL );
        flags = 0;
    }
    return self;
}
-(void)dealloc
{
    pthread_cond_destroy( &m_event );
    pthread_mutex_destroy( &mutex );
}
-(void)reset
{
    pthread_mutex_lock( &mutex );
    flags = 0;
    pthread_mutex_unlock( &mutex );
}
-(void)notify
{
    pthread_mutex_lock( &mutex );
    if( flags & 2 )
        pthread_cond_signal( &m_event );
    else
        flags |= 1;
    pthread_mutex_unlock( &mutex );
}
-(void)wait
{
    pthread_mutex_lock( &mutex );
    if( !(flags & 1) )
    {
        flags |= 2;
        pthread_cond_wait( &m_event, &mutex );
        flags &= (~2);
    }
    else
        flags &= (~1);
    pthread_mutex_unlock( &mutex );
}
@end

int g_def_view = 0;
float g_zoom_level = 5;
bool g_paging_enabled = false;
PDF_RENDER_MODE renderQuality = mode_normal;

float g_Ink_Width = 2;
float g_rect_Width = 2;
uint g_rect_color = 0xFFFF0000;
uint g_ink_color = 0xFFFF0000;
uint g_sel_color = 0x400000C0;
uint g_oval_color = 0xFF0000FF;
uint annotHighlightColor = 0xFFFFFF00;
uint annotUnderlineColor = 0xFF0000FF;
uint annotStrikeoutColor = 0xFFFF0000;
uint annotSquigglyColor = 0xFF00FF00;
void APP_Init()
{
    
    NSArray *paths=NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *path=[paths objectAtIndex:0];
    char dst[512];
    const char *dd1 = [path UTF8String];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    //Global_activeStandard("com.radaee.pdf.PDFViewer", "Radaee", "radaee_com@yahoo.cn", "3BQIA5-IW8GQM-H3CRUZ-WAJQ9H-FADG6Z-XEBCAO");
    //Global_activeProfession("com.radaee.pdf.PDFViewer", "Radaee", "radaee_com@yahoo.cn", "MP8SG1-7SPIWP-H3CRUZ-WAJQ9H-FADG6Z-XEBCAO");
    Global_activePremium("com.radaee.pdf.PDFViewer", "Radaee", "radaee_com@yahoo.cn", "89WG9I-HCL62K-H3CRUZ-WAJQ9H-FADG6Z-XEBCAO");
    
    NSString *cmaps_path = [[NSBundle mainBundle] pathForResource:@"cmaps" ofType:@"dat"];
    NSString *umaps_path = [[NSBundle mainBundle] pathForResource:@"umaps" ofType:@"dat"];
    Global_setCMapsPath([cmaps_path UTF8String], [umaps_path UTF8String]);
    NSString *fpath;
    fpath = [[NSBundle mainBundle] pathForResource:@"00" ofType:nil];
    Global_loadStdFont( 0, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"01" ofType:nil];
    Global_loadStdFont( 1, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"02" ofType:nil];
    Global_loadStdFont( 2, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"03" ofType:nil];
    Global_loadStdFont( 3, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"04" ofType:nil];
    Global_loadStdFont( 4, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"05" ofType:nil];
    Global_loadStdFont( 5, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"06" ofType:nil];
    Global_loadStdFont( 6, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"07" ofType:nil];
    Global_loadStdFont( 7, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"08" ofType:nil];
    Global_loadStdFont( 8, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"09" ofType:nil];
    Global_loadStdFont( 9, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"10" ofType:nil];
    Global_loadStdFont( 10, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"11" ofType:nil];
    Global_loadStdFont( 11, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"12" ofType:nil];
    Global_loadStdFont( 12, [fpath UTF8String] );
    fpath = [[NSBundle mainBundle] pathForResource:@"13" ofType:nil];
    Global_loadStdFont( 13, [fpath UTF8String] );
    
    NSString *helpfile;
    helpfile = [[NSBundle mainBundle]pathForResource:@"help" ofType:@"pdf" inDirectory:@"fdat"];
    Global_fontfileListStart();
    strcpy( dst, dd1 );
    strcat( dst, "/Arial.ttf" );
    if( ![fileManager fileExistsAtPath:[[NSString alloc] initWithUTF8String:dst]] )
    {
        if( Global_SaveFont("Arial", dst) )
            Global_fontfileListAdd(dst);
    }
    else Global_fontfileListAdd(dst);
    strcpy( dst, dd1 );
    strcat( dst, "/Verdana.ttf" );
    if( ![fileManager fileExistsAtPath:[[NSString alloc] initWithUTF8String:dst]] )
    {
        if( Global_SaveFont("Verdana", dst) )
            Global_fontfileListAdd(dst);
    }
    else Global_fontfileListAdd(dst);
    //todo: Global_fontfileListAdd("???/argbsn00lp.ttf")
    fpath = [[NSBundle mainBundle] pathForResource:@"argbsn00lp.ttf" ofType:nil inDirectory:@"fdat"];
    if( fpath )
        Global_fontfileListAdd( [fpath UTF8String] );
    Global_fontfileListEnd();
    bool ret;
    ret = Global_setDefaultFont(NULL, "BousungEG-Light-GB", false);
    ret = Global_setDefaultFont(NULL, "BousungEG-Light-GB", true);
    ret = Global_setDefaultFont("GB1", "BousungEG-Light-GB", false);
    ret = Global_setDefaultFont("GB1", "BousungEG-Light-GB", true);
    ret = Global_setDefaultFont("CNS1", "BousungEG-Light-GB", false);
    ret = Global_setDefaultFont("CNS1", "BousungEG-Light-GB", true);
    //radaee don't know which font should used in Japan or Korea, so use "BousungEG"
    //developers may need modify codes below:
    Global_setDefaultFont("Japan1", "BousungEG-Light-GB", false);
    Global_setDefaultFont("Japan1", "BousungEG-Light-GB", true);
    Global_setDefaultFont("Korea1", "BousungEG-Light-GB", false);
    Global_setDefaultFont("Korea1", "BousungEG-Light-GB", true);
    Global_setAnnotFont( "BousungEG-Light-GB" );
    
	Global_setAnnotTransparency(0x200040FF);
	g_sel_color = 0x400000C0;
	g_def_view = 0;
	renderQuality = mode_normal;
	g_zoom_level = 5;
    g_paging_enabled = false;
}
