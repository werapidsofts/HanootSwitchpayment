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
  func convertDictionaryToJSON(_ dictionary: [String: Any]) -> String? {

     guard let jsonData = try? JSONSerialization.data(withJSONObject: dictionary, options: .prettyPrinted) else {
        print("Something is wrong while converting dictionary to JSON data.")
        return nil
     }

     guard let jsonString = String(data: jsonData, encoding: .utf8) else {
        print("Something is wrong while converting JSON data to JSON string.")
        return nil
     }

     return jsonString
  }
  
  @objc public func initiatePaymentWith(_ param: String , callback: @escaping (String) -> Void) {
    
    print(param)
    
    if let jsonData = param.data(using: .utf8) {
        do {
            // Parse the Data into a JSON object
            if let jsonObject = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: Any] {
                // Now you have a Swift Dictionary representing the JSON object
                print(jsonObject)
              if let amount = jsonObject["amount"] as? String {
                if let doubleValue = Double(amount) {
                  Config.amount = doubleValue
                }
                
              }
              if let merchant_ID = jsonObject["merchant_ID"] as? String {
              
                  Config.merchanhtId = merchant_ID
           
                
              }
              
              
              if let currency = jsonObject["currency"] as? String {
                 
                Config.currency = currency
              }
              
              if let token_user_id = jsonObject["token_user_id"] as? String {
                Config.token_user_id = token_user_id
               
              }
              
              if let secret = jsonObject["secret_key"] as? String {
                Config.secret = secret
              }
            } else {
                print("Failed to convert JSON string to Dictionary")
            }
        } catch {
            print("Error parsing JSON: \(error)")
        }
    } else {
        print("Failed to convert string to Data")
    }
    
    
    
    PaymentHandler.shared.initiatePayment { dictionary in
      
      callback("output")
//      if let output = self.convertDictionaryToJSON(dictionary) {
//          callback("output")
//         print("Input dictionary: \(dictionary)")
//         print("Output JSON: \(output)")
//      }
//
     
    
      
    
    }
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




