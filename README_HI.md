# CARO LINE AI — Android starter project

Yeh ek **starter prototype** hai, complete JARVIS nahi. Ismein Android UI, SpeechRecognizer-based voice input, TextToSpeech replies, flashlight toggle, YouTube launch aur browser launch ka basic code hai.

## Build kaise karein
1. Android Studio install karein: https://developer.android.com/studio
2. ZIP extract karein.
3. Android Studio mein `CaroLineAI` folder open karein.
4. Gradle sync complete hone dein.
5. Android phone ko USB debugging ke saath connect karein, ya emulator use karein.
6. Run dabayein. APK ke liye: **Build > Build APK(s)**.

## Abhi included
- Android Kotlin app skeleton
- Hindi/English/Bangla command ke kuch examples (speech engine ki language availability device par depend karti hai)
- Voice input permission
- Text-to-speech
- Torch on/off
- YouTube aur browser launch
- Basic local command routing

## Abhi included NAHI hain
- Real online AI API integration (API key ko app mein hardcode na karein; backend proxy use karein)
- Fully offline speech recognition/model
- WhatsApp message read/send
- SMS send/receive
- Call answering / screening service
- Notification listener
- Voice-note transcription pipeline
- Background hotword listener

In features ko alag-alag add karke device par test karna hoga. Android ke call/SMS roles aur Google Play policy restrictions apply hoti hain. WhatsApp automation ke liye official supported APIs/approved mechanisms use karein; accessibility se private messages ko chupke read/send karne ki koshish na karein.

## Important
- SpeechRecognizer ka offline hona guaranteed nahi hai; installed speech provider aur device settings par depend karta hai.
- Test karte waqt microphone/torch permissions samjhein.
- Message auto-send ko pehle manual confirmation ke saath implement karein.
