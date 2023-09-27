//
//  CustomMethods.m
//  TestProject
//
//  Created by MacPro on 23/03/23.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"


#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(CustomMethods, NSObject)
  RCT_EXTERN_METHOD(simpleMethod)
  RCT_EXTERN_METHOD(simpleMethodReturns:
    (RCTResponseSenderBlock) callback
  )
  RCT_EXTERN_METHOD(initiatePaymentWith:
    (NSString *) param
    callback: (RCTResponseSenderBlock)callback
  )
  RCT_EXTERN_METHOD(
    resolvePromise: (RCTPromiseResolveBlock) resolve
    rejecter: (RCTPromiseRejectBlock) reject
  )
  RCT_EXTERN_METHOD(rejectPromise:
    (RCTPromiseResolveBlock) resolve
    rejecter: (RCTPromiseRejectBlock) reject
  )
@end
