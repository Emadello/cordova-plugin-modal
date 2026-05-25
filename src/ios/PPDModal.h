//
//  PPDModal.h
//  HelloCordova
//
//  Created by purpleworks on 4/2/14.
//
//

#import <Cordova/CDVPlugin.h>
#import <Cordova/CDVInvokedUrlCommand.h>
#import <SafariServices/SafariServices.h>

@interface PPDModal : CDVPlugin <UIAdaptivePresentationControllerDelegate, SFSafariViewControllerDelegate>

- (void)open:(CDVInvokedUrlCommand*)command;
- (void)openHalf:(CDVInvokedUrlCommand *)command;
- (void)openSF:(CDVInvokedUrlCommand *)command;
- (void)close:(CDVInvokedUrlCommand*)command;

@end
