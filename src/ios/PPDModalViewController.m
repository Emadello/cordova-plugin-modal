//
//  PPDModalViewController.m
//  HelloCordova
//
//  Created by purpleworks on 4/2/14.
//
//

#import "PPDModalViewController.h"

@interface PPDModalViewController ()

@end

@implementation PPDModalViewController

// Do not create the app's launch storyboard inside the modal.
- (void)createLaunchView {
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];

    // Only add X button if dismissMode allows it AND position is not 0
    if ((self.dismissMode == 1 || self.dismissMode == 2) && self.closeButtonPosition != 0) {
        self.closeButton = [UIButton buttonWithType:UIButtonTypeSystem];
        [self.closeButton setTitle:@"✕" forState:UIControlStateNormal];
        self.closeButton.titleLabel.font = [UIFont boldSystemFontOfSize:22];
        self.closeButton.tintColor = [UIColor blackColor];

        CGFloat buttonSize = 40.0;
        CGFloat margin = 20.0;

        if (self.closeButtonPosition == 1) {
            // Top-left
            self.closeButton.frame = CGRectMake(margin, 40, buttonSize, buttonSize);
            self.closeButton.autoresizingMask = UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleBottomMargin;
        } else {
            // Top-right
            self.closeButton.frame = CGRectMake(self.view.bounds.size.width - (margin + buttonSize), 40, buttonSize, buttonSize);
            self.closeButton.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleBottomMargin;
        }

        [self.closeButton addTarget:self action:@selector(closeButtonTapped) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:self.closeButton];
    }
}

- (void)closeButtonTapped {
    if (self.parantCommandDelegate && self.callbackId) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.parantCommandDelegate sendPluginResult:result callbackId:self.callbackId];
    }
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
