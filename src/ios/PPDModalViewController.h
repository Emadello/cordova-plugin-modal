//
//  PPDModalViewController.h
//  HelloCordova
//
//  Created by purpleworks on 4/2/14.
//
//

#import <Cordova/CDVViewController.h>

@interface PPDModalViewController : CDVViewController
@property (nonatomic, strong) NSString *callbackId;
@property (nonatomic, weak) id <CDVCommandDelegate> parantCommandDelegate;
@property (nonatomic, assign) NSInteger dismissMode; // 0=undismissable, 1=X only, 2=X+swipe
@property (nonatomic, assign) NSInteger closeButtonPosition; // 0=right (default), 1=left
@property (nonatomic, strong) UIButton *closeButton;
@end
