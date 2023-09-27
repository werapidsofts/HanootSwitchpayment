//
//  Config.swift
//  TestProject
//
//  Created by MacPro on 27/09/23.
//

import Foundation
import UIKit
import OPPWAMobile_MSA


@objcMembers
class Config: NSObject {
    
    // MARK: - The default amount and currency that are used for all payments
    static let amount: Double = 49.99
    static let currency: String = "EUR"
    static let paymentType: String = "PA"
    
    // MARK: - The payment brands for Ready-to-use UI
    static let checkoutPaymentBrands = ["VISA", "MASTER", "PAYPAL"]
    
    // MARK: - The default payment brand for Payment Button
    static let paymentButtonBrand = "VISA"
    
    // MARK: - The card parameters for SDK & Your Own UI form
    static let cardBrand = "VISA"
    static let cardHolder = "JOHN DOE"
    static let cardNumber = "4200000000000042"
    static let cardExpiryMonth = "07"
    static let cardExpiryYear = "2023"
    static let cardCVV = "123"
    
    // MARK: - Other constants
    static let asyncPaymentCompletedNotificationKey = "AsyncPaymentCompletedNotificationKey"
    static let urlScheme = "msdk.demo.async"
    static let mainColor: UIColor = UIColor.init(red: 10.0/255.0, green: 134.0/255.0, blue: 201.0/255.0, alpha: 1.0)
}


//TODO: This class uses our test integration server (OPPWAMobile_MSA.xcframework); please adapt it to use your own backend API.
class Request: NSObject {
    
    static func requestCheckoutID(amount: Double, currency: String, completion: ( (String?) -> Void)?) {
        let extraParamaters: [String:String] = [
            "testMode": "INTERNAL",
            "sendRegistration": "true"
        ]
        
        OPPMerchantServer.requestCheckoutId(amount: amount,
                                            currency: currency,
                                            paymentType: Config.paymentType,
                                            serverMode: .test,
                                            extraParameters: extraParamaters) { checkoutId, error in
            if let checkoutId = checkoutId {
                completion?(checkoutId)
              
            } else {
                completion?(nil)
            }
        }
    }
    
    static func requestPaymentStatus(resourcePath: String, completion: @escaping (Bool) -> Void) {
        OPPMerchantServer.requestPaymentStatus(resourcePath: resourcePath) { status, error in
            if status {
                completion(true)
            } else {
                completion(false)
            }
        }
    }
}


