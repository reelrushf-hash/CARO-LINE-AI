package com.caroline.assistant

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var torchOn = false
    private lateinit var cameraManager: CameraManager
    private var flashId: String? = null
    private var said by mutableStateOf("Tap microphone and speak.")
    private var reply by mutableStateOf("Namaste! Main CARO LINE AI hoon.")
    private var listening by mutableStateOf(false)

    private val voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        listening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            if (text.isNotBlank()) { said = text; runCommand(text) }
        } else { reply = "Voice input cancel hua. Dobara try karein." }
    }
    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) setTorch(!torchOn) else say("Flashlight ke liye Camera permission zaroori hai.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        flashId = cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
        setContent { Screen() }
    }
    override fun onInit(status: Int) {
        ttsReady = status == TextToSpeech.SUCCESS
        if (ttsReady) tts?.language = Locale("hi", "IN")
    }
    private fun say(text: String) { if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "caroline") }
    private fun listen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "CARO LINE AI ko command dein")
        }
        try { listening = true; voiceLauncher.launch(intent) }
        catch (_: Exception) { listening = false; reply = "Speech service nahi mili. Google app/language settings check karein."; say(reply) }
    }
    private fun runCommand(raw: String) {
        val c = raw.lowercase(Locale.ROOT)
        reply = when {
            listOf("torch on","flashlight on","light on","torch chalu","light chalu","टॉर्च चालू","लाइट चालू").any { c.contains(it) } -> { setTorch(true); "Flashlight on karne ki koshish kar raha hoon." }
            listOf("torch off","flashlight off","light off","torch band","light band","टॉर्च बंद","लाइट बंद").any { c.contains(it) } -> { setTorch(false); "Flashlight off karne ki koshish kar raha hoon." }
            listOf("time","samay","kitne baje","समय").any { c.contains(it) } -> "Abhi time hai " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            listOf("date","tarikh","तारीख","aaj ki date").any { c.contains(it) } -> "Aaj ki date hai " + SimpleDateFormat("dd MMMM yyyy", Locale("hi","IN")).format(Date())
            listOf("youtube","यूट्यूब").any { c.contains(it) } -> { openWeb("https://www.youtube.com", "com.google.android.youtube"); "YouTube khol raha hoon." }
            listOf("browser","google kholo","open google","ब्राउज़र").any { c.contains(it) } -> { openWeb("https://www.google.com"); "Browser khol raha hoon." }
            listOf("hello","namaste","नमस्ते","hi caro").any { c.contains(it) } -> "Namaste! Main madad ke liye ready hoon."
            else -> "Abhi basic commands try karein: time, date, flashlight, YouTube ya browser."
        }
        say(reply)
    }
    private fun setTorch(on: Boolean) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermission.launch(Manifest.permission.CAMERA); return
        }
        try { if (flashId == null) { reply = "Flashlight hardware nahi mila."; return }; cameraManager.setTorchMode(flashId!!, on); torchOn = on }
        catch (_: Exception) { reply = "Flashlight control nahi ho saka." }
    }
    private fun openWeb(url: String, packageName: String? = null) {
        val app = packageName?.let { packageManager.getLaunchIntentForPackage(it) }
        try { if (app != null) startActivity(app) else startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))) }
        catch (_: Exception) { startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))) }
    }
    @Composable private fun Screen() {
        MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFF3BE5FF), background = Color(0xFF061321), surface = Color(0xFF102B3D))) {
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF061321), Color(0xFF0A2639), Color(0xFF02070D))).padding(20.dp))) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Spacer(Modifier.height(12.dp))
                    Text("CARO LINE", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF48E5FF))
                    Text("PERSONAL AI ASSISTANT", fontSize = 12.sp, letterSpacing = 3.sp, color = Color(0xFFB5EFFF))
                    Box(Modifier.size(170.dp).background(Color(0xFF103A50), RoundedCornerShape(100.dp)), contentAlignment = Alignment.Center) {
                        Text(if (listening) "LISTENING" else "◉", fontSize = if (listening) 18.sp else 72.sp, color = Color(0xFF51E8FF), fontWeight = FontWeight.Bold)
                    }
                    Text(if (listening) "I AM LISTENING..." else "SYSTEM READY", color = Color(0xFF59E9FF), fontWeight = FontWeight.Bold)
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xCC102B3D))) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("YOU SAID", color = Color(0xFF52DFFF), fontWeight = FontWeight.Bold)
                            Text(said, color = Color.White)
                            Divider(color = Color(0xFF28556B))
                            Text("CARO LINE", color = Color(0xFF52DFFF), fontWeight = FontWeight.Bold)
                            Text(reply, color = Color(0xFFE1F8FF))
                        }
                    }
                    Button(onClick = { listen() }, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("🎙 SPEAK TO CARO LINE") }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { setTorch(!torchOn) }, modifier = Modifier.weight(1f)) { Text(if (torchOn) "Flashlight OFF" else "Flashlight ON") }
                        OutlinedButton(onClick = { reply = "Abhi time hai " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()); say(reply) }, modifier = Modifier.weight(1f)) { Text("Time bolo") }
                    }
                    Text("BASIC COMMANDS • VOICE SERVICE DEPENDS ON DEVICE", color = Color(0xFF79AFC2), fontSize = 10.sp)
                }
            }
        }
    }
    override fun onDestroy() { tts?.stop(); tts?.shutdown(); super.onDestroy() }
}
