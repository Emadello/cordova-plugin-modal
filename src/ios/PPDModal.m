//
//  PPDModal.m
//  HelloCordova
//
//  Created by purpleworks on 4/2/14.
//
//

#import "PPDModal.h"
#import "PPDModalViewController.h"
#import <objc/runtime.h>
#import <SafariServices/SafariServices.h>

@implementation PPDModal

- (void)open:(CDVInvokedUrlCommand *)command
{
    NSString* url = [command.arguments objectAtIndex:0];
    NSNumber* mode = (command.arguments.count > 1) ? command.arguments[1] : @(2); // default = 2
    NSNumber* btnPos = (command.arguments.count > 2) ? command.arguments[2] : @(0); // default right
    NSString* callbackId = command.callbackId;

    PPDModalViewController *vc = [[PPDModalViewController alloc] init];
    vc.dismissMode = [mode integerValue];
    vc.closeButtonPosition = [btnPos integerValue];

    if (callbackId) {
        [vc setParantCommandDelegate:self.commandDelegate];
        [vc setCallbackId:callbackId];
    }

    vc.modalPresentationStyle = UIModalPresentationAutomatic;
    vc.presentationController.delegate = self;

    if (![[NSNull null] isEqual:url]) {
        vc.startPage = url;
    } else {
        vc.startPage = @"https://github.com/purpleworks-developer/cordova-plugin-modal";
    }

    [self.viewController presentViewController:vc animated:YES completion:^{
        NSLog(@"Modal Presented");
    }];
}

- (void)openHalf:(CDVInvokedUrlCommand *)command
{
    NSString* url = [command.arguments objectAtIndex:0];
    NSNumber* mode = (command.arguments.count > 1) ? command.arguments[1] : @(2); // default = 2
    NSNumber* btnPos = (command.arguments.count > 2) ? command.arguments[2] : @(0); // default right
    NSString* callbackId = command.callbackId;

    PPDModalViewController *vc = [[PPDModalViewController alloc] init];
    vc.dismissMode = [mode integerValue];
    vc.closeButtonPosition = [btnPos integerValue];

    if (callbackId) {
        [vc setParantCommandDelegate:self.commandDelegate];
        [vc setCallbackId:callbackId];
    }

    if (![[NSNull null] isEqual:url]) {
        vc.startPage = url;
    } else {
        vc.startPage = @"https://github.com/purpleworks-developer/cordova-plugin-modal";
    }

    if (@available(iOS 15.0, *)) {
        vc.modalPresentationStyle = UIModalPresentationPageSheet;
        UISheetPresentationController *sheet = vc.sheetPresentationController;
        if (sheet) {
            sheet.detents = @[
            [UISheetPresentationControllerDetent mediumDetent],
            [UISheetPresentationControllerDetent largeDetent]
            ];
            sheet.prefersGrabberVisible = YES;
            sheet.preferredCornerRadius = 20.0;
        }
    } else {
        vc.modalPresentationStyle = UIModalPresentationAutomatic;
    }

    vc.presentationController.delegate = self;

    [self.viewController presentViewController:vc animated:YES completion:^{
        NSLog(@"Half Modal Presented");
    }];
}

- (BOOL)presentationControllerShouldDismiss:(UIPresentationController *)presentationController {
    PPDModalViewController *vc = (PPDModalViewController *)presentationController.presentedViewController;
    if (vc.dismissMode == 0 || vc.dismissMode == 1) {
        return NO; // undismissable OR X-only
    }
    return YES; // X + swipe
}

- (void)presentationControllerDidDismiss:(UIPresentationController *)presentationController {
    // Assuming the view controller being dismissed is a PPDModalViewController
    PPDModalViewController *vc = (PPDModalViewController *)presentationController.presentedViewController;

    // Ensure the view controller is of the correct type
    if ([vc isKindOfClass:[PPDModalViewController class]]) {
        // Trigger the same logic when dismissed by gesture
        [self handleModalDismissalForViewController:vc withCommand:nil]; // Assuming no command during gesture dismissal
    }
}

// Helper method for dismissal logic
- (void)handleModalDismissalForViewController:(PPDModalViewController *)vc withCommand:(CDVInvokedUrlCommand *)command {
    NSString *closeResultData = [command.arguments objectAtIndex:0];

    if (vc.callbackId && vc.parantCommandDelegate) {
        CDVPluginResult *closeResult = nil;
        if (![[NSNull null] isEqual:closeResultData]) {
            closeResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:closeResultData];
        } else {
            closeResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        }

        [vc.parantCommandDelegate sendPluginResult:closeResult callbackId:vc.callbackId];
    }

    NSLog(@"Modal Dismissed PPDModal");
}


- (void)close:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult = nil;
    
    if ([self.viewController isKindOfClass:[PPDModalViewController class]]) {
        PPDModalViewController *vc = (PPDModalViewController*)self.viewController;
        NSString* closeResultData = [command.arguments objectAtIndex:0];
        
        if (vc.callbackId && vc.parantCommandDelegate) {
            CDVPluginResult* closeResult = nil;
            if (![[NSNull null] isEqual:closeResultData]) {
                closeResult =[CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:closeResultData];
            }
            else {
                closeResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            }
            
            [vc.parantCommandDelegate sendPluginResult:closeResult callbackId:vc.callbackId];
        }
        
        [self.viewController dismissViewControllerAnimated:YES completion:^{
            NSLog(@"Modal Dismissed PPDModal");
        }];
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"view is not modal"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)openSF:(CDVInvokedUrlCommand *)command
{
    NSString *urlString = nil;

    if (command.arguments.count > 0 && ![[NSNull null] isEqual:command.arguments[0]]) {
        urlString = command.arguments[0];
    }

    if (!urlString || urlString.length == 0) {
        urlString = @"https://github.com/purpleworks-developer/cordova-plugin-modal";
    }

    NSURL *url = [NSURL URLWithString:urlString];

    if (!url || !url.scheme || !url.host) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                          messageAsString:@"Invalid URL"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        return;
    }

    SFSafariViewController *safariVC = [[SFSafariViewController alloc] initWithURL:url];
    safariVC.delegate = self;

    // Disable swipe-down dismissal
    safariVC.modalInPresentation = YES;

    // Recommended for payment / Apple Pay flows
    safariVC.modalPresentationStyle = UIModalPresentationFullScreen;

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    objc_setAssociatedObject(
        safariVC,
        @"callbackId",
        command.callbackId,
        OBJC_ASSOCIATION_RETAIN_NONATOMIC
    );

    [self.viewController presentViewController:safariVC animated:YES completion:^{
        NSLog(@"SFSafariViewController Presented");
    }];
}

- (void)safariViewControllerDidFinish:(SFSafariViewController *)controller
{
    NSString *callbackId = objc_getAssociatedObject(controller, @"callbackId");

    if (callbackId) {
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                          messageAsString:@"dismissed"];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
    }

    NSLog(@"SFSafariViewController Dismissed");
}
@end
