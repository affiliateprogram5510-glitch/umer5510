package com.example.ultrasoundbooster

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var loudnessEnhancer: LoudnessEnhancer? = null
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val switchBooster = findViewById<Switch>(R.id.switchBooster)
        val seekBarBoost = findViewById<SeekBar>(R.id.seekBarBoost)
        val seekBarBass = findViewById<SeekBar>(R.id.seekBarBass)
        val seekBarVirtualizer = findViewById<SeekBar>(R.id.seekBarVirtualizer)
        val btnUltraPreset = findViewById<Button>(R.id.btnUltraPreset)

        val tvBoostLevel = findViewById<TextView>(R.id.tvBoostLevel)
        val tvBassLevel = findViewById<TextView>(R.id.tvBassLevel)
        val tvVirtualizerLevel = findViewById<TextView>(R.id.tvVirtualizerLevel)

        initAudioEffects()

        switchBooster.setOnCheckedChangeListener { _, isChecked ->
            loudnessEnhancer?.enabled = isChecked
            equalizer?.enabled = isChecked
            bassBoost?.enabled = isChecked
            virtualizer?.enabled = isChecked
            val msg = if (isChecked) "Ultra Sound Engine ON" else "Ultra Sound Engine OFF"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        seekBarBoost.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvBoostLevel.text = "Volume Boost: $progress%"
                loudnessEnhancer?.setTargetGain(progress * 15)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarBass.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvBassLevel.text = "Deep Bass: ${progress / 10}%"
                if (bassBoost?.strengthSupported == true) {
                    bassBoost?.setStrength(progress.toShort())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarVirtualizer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvVirtualizerLevel.text = "3D Surround Clarity: ${progress / 10}%"
                if (virtualizer?.strengthSupported == true) {
                    virtualizer?.setStrength(progress.toShort())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnUltraPreset.setOnClickListener {
            switchBooster.isChecked = true
            applyUltraHdEqualizer()
            seekBarBoost.progress = 40
            seekBarBass.progress = 650
            seekBarVirtualizer.progress = 500
            Toast.makeText(this, "Ultra HD Studio Profile Applied!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initAudioEffects() {
        try {
            val sessionId = 0
            loudnessEnhancer = LoudnessEnhancer(sessionId)
            equalizer = Equalizer(0, sessionId)
            bassBoost = BassBoost(0, sessionId)
            virtualizer = Virtualizer(0, sessionId)
        } catch (e: Exception) {
            Toast.makeText(this, "Device audio session error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun applyUltraHdEqualizer() {
        equalizer?.let { eq ->
            val bands = eq.numberOfBands
            val minLevel = eq.bandLevelRange[0]
            val maxLevel = eq.bandLevelRange[1]

            for (i in 0 until bands) {
                val ratio = i.toFloat() / (bands - 1).coerceAtLeast(1)
                val targetLevel = when {
                    ratio <= 0.2f -> (maxLevel * 0.65).toInt()
                    ratio <= 0.5f -> (minLevel * 0.15).toInt()
                    ratio <= 0.8f -> (maxLevel * 0.45).toInt()
                    else -> (maxLevel * 0.75).toInt()
                }
                eq.setBandLevel(i.toShort(), targetLevel.toShort())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loudnessEnhancer?.release()
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
    }
}
