package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object SoundPlayer {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    var isMuted: Boolean = false

    // Piano frequencies (C4 major scale)
    private val NOTE_FREQUENCIES = doubleArrayOf(
        261.63, // Do (C4)
        293.66, // Re (D4)
        329.63, // Mi (E4)
        349.23, // Fa (F4)
        392.00, // Sol (G4)
        440.00, // La (A4)
        493.88, // Si (B4)
        523.25  // İnce Do (C5)
    )

    fun playPianoNote(noteIndex: Int) {
        if (isMuted) return
        val freq = NOTE_FREQUENCIES.getOrElse(noteIndex) { 440.0 }
        scope.launch {
            generateAndPlayTone(
                startFreq = freq,
                endFreq = freq,
                durationMs = 280,
                waveform = Waveform.SINE_HARMONIC
            )
        }
    }

    fun playPop() {
        if (isMuted) return
        scope.launch {
            // Rapid high to low sweep sounds like popping balloon / bubble
            generateAndPlayTone(
                startFreq = 850.0,
                endFreq = 220.0,
                durationMs = 90,
                waveform = Waveform.SINE
            )
        }
    }

    fun playSparkle() {
        if (isMuted) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            for (f in notes) {
                generateAndPlayTone(startFreq = f, endFreq = f, durationMs = 70, waveform = Waveform.SINE)
            }
        }
    }

    fun playCheer() {
        if (isMuted) return
        scope.launch {
            val melody = doubleArrayOf(392.00, 523.25, 659.25, 783.99)
            for (i in melody.indices) {
                val dur = if (i == melody.lastIndex) 350 else 100
                generateAndPlayTone(startFreq = melody[i], endFreq = melody[i], durationMs = dur, waveform = Waveform.SINE_HARMONIC)
            }
        }
    }

    fun playMatchCard() {
        if (isMuted) return
        scope.launch {
            generateAndPlayTone(startFreq = 440.0, endFreq = 659.25, durationMs = 140, waveform = Waveform.SINE)
        }
    }

    fun playSnap() {
        if (isMuted) return
        scope.launch {
            generateAndPlayTone(startFreq = 587.33, endFreq = 880.0, durationMs = 110, waveform = Waveform.SINE_HARMONIC)
        }
    }

    fun playWhoosh() {
        if (isMuted) return
        scope.launch {
            generateAndPlayTone(startFreq = 300.0, endFreq = 700.0, durationMs = 120, waveform = Waveform.SINE)
        }
    }

    fun playTap() {
        if (isMuted) return
        scope.launch {
            generateAndPlayTone(startFreq = 600.0, endFreq = 400.0, durationMs = 45, waveform = Waveform.SINE)
        }
    }

    fun playPetGiggle() {
        if (isMuted) return
        scope.launch {
            val giggles = doubleArrayOf(400.0, 550.0, 480.0, 620.0, 700.0)
            for (freq in giggles) {
                generateAndPlayTone(startFreq = freq, endFreq = freq, durationMs = 65, waveform = Waveform.SINE)
            }
        }
    }

    fun playAnimalSound(animal: String) {
        if (isMuted) return
        scope.launch {
            when (animal.uppercase()) {
                "DOG" -> {
                    // Double woof
                    generateAndPlayTone(220.0, 160.0, 100, Waveform.SQUARE)
                    kotlinx.coroutines.delay(60)
                    generateAndPlayTone(200.0, 140.0, 120, Waveform.SQUARE)
                }
                "CAT" -> {
                    // Meow sweep
                    generateAndPlayTone(380.0, 680.0, 180, Waveform.SINE)
                    generateAndPlayTone(680.0, 420.0, 160, Waveform.SINE)
                }
                "DUCK" -> {
                    // Quack quack
                    generateAndPlayTone(300.0, 240.0, 90, Waveform.SAWTOOTH)
                    kotlinx.coroutines.delay(50)
                    generateAndPlayTone(280.0, 220.0, 110, Waveform.SAWTOOTH)
                }
                "COW" -> {
                    // Low Moo
                    generateAndPlayTone(150.0, 125.0, 400, Waveform.SINE_HARMONIC)
                }
                "FROG" -> {
                    // Ribbit
                    generateAndPlayTone(180.0, 260.0, 70, Waveform.SAWTOOTH)
                    kotlinx.coroutines.delay(30)
                    generateAndPlayTone(200.0, 160.0, 100, Waveform.SAWTOOTH)
                }
                "LION" -> {
                    // Roar
                    generateAndPlayTone(120.0, 90.0, 350, Waveform.SAWTOOTH)
                }
                "SHEEP" -> {
                    // Baa
                    generateAndPlayTone(260.0, 230.0, 280, Waveform.SQUARE)
                }
                else -> {
                    generateAndPlayTone(440.0, 520.0, 120, Waveform.SINE)
                }
            }
        }
    }

    private enum class Waveform {
        SINE,
        SINE_HARMONIC,
        SAWTOOTH,
        SQUARE
    }

    private fun generateAndPlayTone(
        startFreq: Double,
        endFreq: Double,
        durationMs: Int,
        waveform: Waveform
    ) {
        try {
            val sampleRate = 22050
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress

                // Envelope: quick attack and smooth decay
                val envelope = when {
                    progress < 0.08 -> progress / 0.08
                    progress > 0.7 -> (1.0 - progress) / 0.3
                    else -> 1.0
                }

                val angle = 2.0 * PI * currentFreq * t
                val rawSample = when (waveform) {
                    Waveform.SINE -> sin(angle)
                    Waveform.SINE_HARMONIC -> 0.7 * sin(angle) + 0.25 * sin(2 * angle) + 0.05 * sin(3 * angle)
                    Waveform.SAWTOOTH -> (2.0 * (t * currentFreq - Math.floor(t * currentFreq + 0.5)))
                    Waveform.SQUARE -> if (sin(angle) >= 0) 0.6 else -0.6
                }

                val sample = (rawSample * envelope * Short.MAX_VALUE * 0.75).toInt().coerceIn(
                    Short.MIN_VALUE.toInt(),
                    Short.MAX_VALUE.toInt()
                )
                buffer[i] = sample.toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            // Release after playing
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.Default) {
                kotlinx.coroutines.delay((durationMs + 100).toLong())
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Audio device fallback
        }
    }
}
