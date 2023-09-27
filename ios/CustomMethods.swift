//
//  CustomMethods.swift
//  TestProject
//
//  Created by MacPro on 23/03/23.
//
import Foundation
import UIKit





@available(iOS 13.0, *)
@objc(CustomMethods) class CustomMethods: NSObject {
  
  @objc static func requiresMainQueueSetup() -> Bool { return true }
  
  @objc public func simpleMethod() { /* do something */ }
  
  @objc public func simpleMethodReturns(
    _ callback: RCTResponseSenderBlock
  ) {
//    callback(["Hi react, this is my first attempt of bridging"])
    callback([test()])

  }

  @objc public func initiatePaymentWith(_ param: String, callback: RCTResponseSenderBlock) {
    
    Task {
      let res = await self.getUpdatedResponse()
      print("response is ",res)
    }
   
    
    print("now using callback")

    callback(["CustomMethods sdsd.initiatePaymentWith('\(param)')"])
  }
  
  @available(iOS 13.0.0, *)
  func getUpdatedResponse() async -> [String: Any]{
    var str = [String : Any]()
    let dispatch = DispatchGroup()
    dispatch.enter()
//    dispatch.wait()
    PaymentHandler.shared.initiatePayment { dictionary in
   
      
//      callback(["CustomMethods sdsd.initiatePaymentWith('\(dictionary)')"])
      str = dictionary
      
      dispatch.leave()
    }
    dispatch.notify(queue: .main) {
     
//      return str
      print("getting response",str)
      
    }
    
    
    return str
  }
  
  @objc public func throwError() throws {
   // throw createError(message: "CustomMethods.throwError()")
  }
  
  @objc public func resolvePromise(
    _ resolve: RCTPromiseResolveBlock,
    rejecter reject: RCTPromiseRejectBlock
  ) -> Void {
    resolve("CustomMethods.resolvePromise()")
  }
  
  
  @objc public func rejectPromise(
    _ resolve: RCTPromiseResolveBlock,
    rejecter reject: RCTPromiseRejectBlock
  ) -> Void {
    reject("0", "CustomMethods.rejectPromise()", nil)
  }
  
  
  
  func test(){
    let alert = UIAlertController(title: "Test", message: "hello react", preferredStyle: .alert)
    alert.addAction(UIAlertAction(title: "Done", style: .cancel))
    DispatchQueue.main.async {
      UIApplication.topViewController()?.addChild(alert)
    }
  }
}


extension UIApplication {
    class func topViewController(controller: UIViewController? = UIApplication.shared.keyWindow?.rootViewController) -> UIViewController? {
        if let navigationController = controller as? UINavigationController {
            return topViewController(controller: navigationController.visibleViewController)
        }
        if let tabController = controller as? UITabBarController {
            if let selected = tabController.selectedViewController {
                return topViewController(controller: selected)
            }
        }
        if let presented = controller?.presentedViewController {
            return topViewController(controller: presented)
        }
        return controller
    }
}




