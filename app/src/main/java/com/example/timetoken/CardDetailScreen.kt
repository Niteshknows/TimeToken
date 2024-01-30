package com.example.timetoken

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timetoken.databinding.ActivityCardDetailScreenBinding
import java.io.UnsupportedEncodingException
import java.util.Calendar


class CardDetailScreen : AppCompatActivity() {
    private lateinit var binding : ActivityCardDetailScreenBinding
    private lateinit var cardNo : String
    private lateinit var signupTimestamp : String
    lateinit var username : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val receivedIntent = intent
        cardNo = receivedIntent.getStringExtra("cardNo") ?: "0"
        signupTimestamp = receivedIntent.getStringExtra("signupTimestamp") ?: "0"
        username = receivedIntent.getStringExtra("username") ?: " "
        initView()
    }
    private fun initView(){
        binding.tvCardNo.text = formatDigits(cardNo,4,1)
        val customerName = extractNameFromEmail(username)
        binding.tvUsername.text = "Welcome ${customerName.toUpperCase()} !"
        if(cardNo!="0" && signupTimestamp!="0"){
            generateOtpEveryTwoMinutes()
        }else{
            Toast.makeText(applicationContext,"Unable to fetch data",Toast.LENGTH_SHORT).show()
        }
        binding.tvLogout.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    fun generateOtpEveryTwoMinutes(){
        // set otp
        val pinGenerated = generatePin()
        binding.tvPin.text = pinGenerated
        startCountdownTimer(120000)
    }

    private fun generatePin() : String{
        val currentTimestamp = roundToNearestTwoMinutes(System.currentTimeMillis())
        val stringToEncrypt = "$signupTimestamp:$currentTimestamp"
        val encryptedString = AESEncryptionMethod(stringToEncrypt)?.takeLast(6)
        return generatePinFromAlphanumeric(encryptedString!!)
    }

    private fun startCountdownTimer(durationMillis: Long) {
        val countDownTimer = object : CountDownTimer(durationMillis, 100) {
            private val totalTicks = durationMillis / 100
            private val ticksPerSecond = totalTicks / (durationMillis / 100)

            override fun onTick(millisUntilFinished: Long) {
                val secondsElapsed = (totalTicks - millisUntilFinished / 100).toInt()

                // Calculate the progress based on the elapsed time
                val progress = 100 - (secondsElapsed * 100 / totalTicks)
                binding.barTimer.progress = progress.toInt()
            }

            override fun onFinish() {
              binding.barTimer.progress = 0
                generateOtpEveryTwoMinutes()
            }
        }

        // Start the countdown timer
        countDownTimer.start()
    }


    fun roundToNearestTwoMinutes(inputTimestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = inputTimestamp

        // Extracting minutes from the current timestamp
        val minutes = calendar.get(Calendar.MINUTE)

        // Calculate the remaining minutes to the next multiple of 2
        val remainingMinutes = minutes % 2

        // Round down the minutes to the nearest floor value of 2
        val roundedMinutes = minutes - remainingMinutes

        // Set the rounded minutes back to the calendar
        calendar.set(Calendar.MINUTE, roundedMinutes)
        // Set seconds and milliseconds to 0 for consistency
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }
    private fun generatePinFromAlphanumeric(input: String): String {
        val charToDigitMap = mutableMapOf<Char, Char>()
        // Use a hash-based approach to map characters to digits
        input.forEach {
            if (!charToDigitMap.containsKey(it)) {
                val randomDigit = ('0' + (it.hashCode() and Int.MAX_VALUE) % 10).toChar()
                charToDigitMap[it] = randomDigit
            }
        }
        // Transform each character into a digit
        val pinDigits = input.map { charToDigitMap.getValue(it) }
        // Take the first 6 digits (or pad with zeros if needed)
        val pin = pinDigits.take(6).joinToString("").padEnd(6, '0')
        return pin
    }


    @Throws(UnsupportedEncodingException::class)
    private fun AESEncryptionMethod(string: String): String? {
        val aes = EncodeDecodeAES()
        var encrypted: String? = null
        try {
            Log.d("nitesh", "before AESEncryptionMethod: "+string)
            encrypted = EncodeDecodeAES.bytesToHex(aes.encrypt(string))
            Log.d("nitesh", "after encryption AESEncryptionMethod: "+encrypted)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return encrypted
    }

    private fun extractNameFromEmail(email: String): String {
        val atIndex = email.indexOf('@')
        return if (atIndex != -1) {
            email.substring(0, atIndex)
        } else {
            return email
        }
    }
    private fun formatDigits(digits: String, groupSize: Int, spaceSize: Int): String {
        val regex = "(\\d{$groupSize})".toRegex()
        return digits.chunked(groupSize).joinToString(" ") { it }
    }
}