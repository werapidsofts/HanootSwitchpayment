//
//  PaymentHandler.swift
//  TestProject
//
//  Created by MacPro on 27/09/23.
//

import Foundation
import UIKit
import OPPWAMobile_MSA


@objcMembers
class PaymentHandler : NSObject {
  
  static let shared = PaymentHandler()
  
  var checkoutProvider: OPPCheckoutProvider?
  var transaction: OPPTransaction?

  
   func initiatePayment(completionHandler : (([String: Any])->Void)?){
   
    Request.requestCheckoutID(amount: Config.amount, currency: Config.currency, completion: {(checkoutID) in
        DispatchQueue.main.async {
           
            print("here is the result")
            guard let checkoutID = checkoutID else {
//                Utils.showResult(presenter: self, success: false, message: "Checkout ID is empty")
                return
            }
            
            self.checkoutProvider = self.configureCheckoutProvider(checkoutID: checkoutID)
//            self.checkoutProvider?.delegate = self
            self.checkoutProvider?.presentCheckout(forSubmittingTransactionCompletionHandler: { (transaction, error) in
                DispatchQueue.main.async {
                  self.handleTransactionSubmission(transaction: transaction, error: error) { message in
                    completionHandler?(["message":message])
                  }
                }
            }, cancelHandler: nil)
        }
    })
    
  }
  
  
  
}

extension PaymentHandler {
  
  
  func configureCheckoutSettings() -> OPPCheckoutSettings {
      let checkoutSettings = OPPCheckoutSettings.init()
      checkoutSettings.paymentBrands = Config.checkoutPaymentBrands
      checkoutSettings.shopperResultURL = Config.urlScheme + "://payment"
      
      checkoutSettings.theme.navigationBarBackgroundColor = Config.mainColor
      checkoutSettings.theme.confirmationButtonColor = Config.mainColor
      checkoutSettings.theme.accentColor = Config.mainColor
      checkoutSettings.theme.cellHighlightedBackgroundColor = Config.mainColor
      checkoutSettings.theme.sectionBackgroundColor = Config.mainColor.withAlphaComponent(0.05)
      
      return checkoutSettings
  }
  // MARK: - OPPCheckoutProviderDelegate methods
  
  // This method is called right before submitting a transaction to the Server.
  func checkoutProvider(_ checkoutProvider: OPPCheckoutProvider, continueSubmitting transaction: OPPTransaction, completion: @escaping (String?, Bool) -> Void) {
      // To continue submitting you should call completion block which expects 2 parameters:
      // checkoutID - you can create new checkoutID here or pass current one
      // abort - you can abort transaction here by passing 'true'
      completion(transaction.paymentParams.checkoutID, false)
  }
  
  // MARK: - Payment helpers
  
  func handleTransactionSubmission(transaction: OPPTransaction?, error: Error?, completion : ((_ message : String) -> Void)?) {
      guard let transaction = transaction else {
//          Utils.showResult(presenter: self, success: false, message: error?.localizedDescription)
          return
      }
      
      self.transaction = transaction
      if transaction.type == .synchronous {
          // If a transaction is synchronous, just request the payment status
        self.requestPaymentStatus { message in
          print("ghetting message in handleTransactionSubmission",message)
          completion?("getting message in handleTransactionSubmission \(message)")
        }
      } else if transaction.type == .asynchronous {
          // If a transaction is asynchronous, SDK opens transaction.redirectUrl in a browser
          // Subscribe to notifications to request the payment status when a shopper comes back to the app
          NotificationCenter.default.addObserver(self, selector: #selector(self.didReceiveAsynchronousPaymentCallback), name: Notification.Name(rawValue: Config.asyncPaymentCompletedNotificationKey), object: nil)
      } else {
//          Utils.showResult(presenter: self, success: false, message: "Invalid transaction")
      }
  }
  
  func configureCheckoutProvider(checkoutID: String) -> OPPCheckoutProvider? {
      let provider = OPPPaymentProvider.init(mode: .test)
    let checkoutSettings = self.configureCheckoutSettings()
      checkoutSettings.storePaymentDetails = .prompt
      return OPPCheckoutProvider.init(paymentProvider: provider, checkoutID: checkoutID, settings: checkoutSettings)
  }
  
  func requestPaymentStatus(completion: ((_ message : String)->Void)?) {
      guard let resourcePath = self.transaction?.resourcePath else {
//          Utils.showResult(presenter: self, success: false, message: "Resource path is invalid")
          return
      }
      
      self.transaction = nil
//      self.processingView.startAnimating()
      Request.requestPaymentStatus(resourcePath: resourcePath) { (success) in
          DispatchQueue.main.async {
//              self.processingView.stopAnimating()
              let message = success ? "Your payment was successful" : "Your payment was not successful"
            PaymentHandler.showResult(success: success, message: message)
            
            print("success message",message)
            completion?(message)
          }
      }
  }
  
  // MARK: - Async payment callback
  
  @objc func didReceiveAsynchronousPaymentCallback() {
      NotificationCenter.default.removeObserver(self, name: Notification.Name(rawValue: Config.asyncPaymentCompletedNotificationKey), object: nil)
      self.checkoutProvider?.dismissCheckout(animated: true) {
          DispatchQueue.main.async {
            self.requestPaymentStatus { message in
              print("getting didReceiveAsynchronousPaymentCallback",message)
            }
          }
      }
  }
  
  static func showResult( success: Bool, message: String?) {
    if let appDelegate = UIApplication.shared.delegate as? AppDelegate,
       var topViewController = appDelegate.window?.rootViewController {
        while let presentedViewController = topViewController.presentedViewController {
            topViewController = presentedViewController
        }
        
      let title = success ? "Success" : "Failure"
      let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
      alert.addAction(UIAlertAction(title: "OK", style: UIAlertAction.Style.default, handler: nil))
      topViewController.present(alert, animated: true, completion: nil)

    }
  }
}
