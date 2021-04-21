/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.coppernic.lib.interactors.ocr.doubango

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import fr.coppernic.lib.interactors.ocr.doubango.InteractorsDefines.LOG
import fr.coppernic.lib.ocr.doubango.DoubangoReader
import fr.coppernic.lib.ocr.doubango.DoubangoReaderFactory
import fr.coppernic.lib.ocr.doubango.model.DoubangoConfig
import fr.coppernic.lib.ocr.doubango.model.RawMrz
import fr.coppernic.lib.ocr.doubango.parser.MrzParser


/**
 * Main entry point into our app. This app follows the single-activity pattern, and all
 * functionality is implemented in the form of fragments.
 */
open class OcrCameraActivity : AppCompatActivity() {

    companion object {
        private const val KEY_DOUBANGO_CONFIG = "key_doubango_config"
        private const val KEY_INTERACTOR_CONFIG = "key_interactor_config"
        private const val ACTIVATION_URL = "https://activation.doubango.org:3600"

        const val REQUEST_CODE = 10851
        const val KEY_MRZ = "key_mrz"

        fun start(activity: Activity,
                  doubangoConfig: DoubangoConfig = DoubangoConfig(activationServerUrl = ACTIVATION_URL),
                  interactorConfig: OcrInteractorConfig = OcrInteractorConfig(),
                  requestCode: Int = REQUEST_CODE) {
            activity.startActivityForResult(Intent(activity, OcrCameraActivity::class.java).apply {
                putExtra(KEY_DOUBANGO_CONFIG, doubangoConfig)
                putExtra(KEY_INTERACTOR_CONFIG, interactorConfig)
            }, requestCode)
        }
    }

    internal lateinit var reader: DoubangoReader
    private lateinit var config: OcrInteractorConfig
    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        container = findViewById(R.id.fragment_container)

        val doubangoConfig: DoubangoConfig = intent.getParcelableExtra(KEY_DOUBANGO_CONFIG) as DoubangoConfig?
                ?: DoubangoConfig(activationServerUrl = ACTIVATION_URL)
        config = intent.getSerializableExtra(KEY_INTERACTOR_CONFIG) as OcrInteractorConfig? ?: OcrInteractorConfig(false)

        reader = DoubangoReaderFactory.builder()
                .withContext(this).build()

        reader.initWithLicense(doubangoConfig)
    }

    override fun onDestroy() {
        super.onDestroy()

        reader.close()
    }

    internal fun onMrz(rawMrz: RawMrz) {
        LOG.trace("$rawMrz")
        val mrz = MrzParser.parse(rawMrz)
        if (mrz.valid) {
            this.setResult(
                    Activity.RESULT_OK,
                    Intent().putExtra(KEY_MRZ, mrz))
        } else {
            this.setResult(
                    Activity.RESULT_CANCELED)
        }
        if (config.finishOnOcr && mrz.valid) {
            this.finish()
        }
    }
}
