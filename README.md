# TimeToken

Time Based One Time Passoword generation and validation solution.
Consists of two android apps - one for OTP generation and one for OTP validation.


## Tech Stack

* Kotlin, Java
* Android Studio
* AES Encryption
* Firebase (Firestore DB)


## Features

A Time Based OTP (TOTP) generation and validation system. It consists of two apps using AES Encryption and Firebase DB.  
  
*TimeToken app works as a TOTP generation app.  

*User will first login the app using email and password. email password is validated from details in Firestore DB hosted on Firebase.

*Once the user is logged in the card details along with Time Based OTP is available. 

*Every user has a unique ID which is generated during the setup of account and which helps in OTP hashing.

*AES Encryption is used in the process of generating OTP to make it secure.

*This OTP is critical in payment and does not require any type of connection to be generated. The OTP refreshes itself every two minutes.

*The user will open Merchant app to make a payment - [Merchant App](https://github.com/Niteshknows/Merchant-App-TimeToken). 

*The user will select TimeToken as payment method and continue to enter card details.

*User can now just copy the OTP generated, paste it and hit on PAY button.  

*The OTP will be validated using same AES Encryption and unique ID which was used while OTP generation.

Voila ! Validation Sucessful.

## Usage


User can do the payments on the merchant app by selecting TimeToken as a payment method.

![App Screen](https://github.com/Niteshknows/sortingVisualiser.github.io/assets/62033523/9f2db461-e63c-4085-b71d-949b1b925c23)

On the payment page user have to enter Card Detail and OTP. 

![App Screen](https://github.com/Niteshknows/sortingVisualiser.github.io/assets/62033523/9f8b41de-9fb2-47e4-9012-235d60902c7a)

To Generate the OTP user will open TimeToken app which is for OTP generation. User will login the TimeToken app using email and password.

![Screenshot_20240130_023959_TimeToken](https://github.com/Niteshknows/sortingVisualiser.github.io/assets/62033523/59d94691-ad0e-454a-9948-3cbda44ff18d)

Once the user is logged in with correct username and password, payment card details and TOTP is visible.

![Screenshot_20240130_024247_TimeToken](https://github.com/Niteshknows/sortingVisualiser.github.io/assets/62033523/3ef31ff0-afd2-4603-bd7a-04d2665941f6)

The TOTP or Time Based One Time Password will keep refreshing every two minutes. To generate the TOTP you do not need any type of connection.

The user will copy the OTP and again redirect to the payment page in merchant app.

![Screenshot_20240130_024456_Merchant-TimeToken](https://github.com/Niteshknows/sortingVisualiser.github.io/assets/62033523/b8f155b7-f777-4b82-b45f-92b10b400b4a)

Once the user enters correct OTP the validation is successful !
## My algorithm for TOTP

TOTP is usually generated using current timestamp and a unique ID which is pre decided while user first time setup app so that user do not have to send any request to the server in future to generate an OTP.  
I decided to use signup timestamp to be that unique ID.  
I used two keys - signup timestamp and current timestamp to generate the OTP - 
```bash
  stringToEncrypt = "$signupTimestamp:$currentTimestamp"
```
Then I encrypted the result using AES Encryption which is considered as one of the safest encryption techniques. But since the encryted result was too large I used last 6 characters to be mapped as OTP. 

```bash
  encryptedString = AESEncryptionMethod(stringToEncrypt)?.takeLast(6)
```

