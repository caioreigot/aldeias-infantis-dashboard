package br.org.aldeiasinfantis.dashboard

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.org.aldeiasinfantis.dashboard.model.Information
import br.org.aldeiasinfantis.dashboard.model.information
import java.lang.Math.abs

class InformationsActivity : AppCompatActivity() {

    private var idReceived: Int = -1

    lateinit var recyclerViewMain: RecyclerView
    lateinit var infoGroupName: TextView
    lateinit var infoItemCount: TextView

    lateinit var velocity: VelocityTracker
    var alreadyCalled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informations)

        val fetchId = intent
        idReceived = fetchId.getIntExtra(MainActivity.intentIDTag, -1)

        // Se o ID for menor que 0, ele não foi encontrado no intent
        if (idReceived < 0) {
            // TODO: Tela de erro para ID não encontrado

            return
        }

        recyclerViewMain = findViewById(R.id.recycler_view_main)

        // Botão 1
        if (idReceived == MainActivity.button1.id) {
            infoGroupName = findViewById(R.id.info_group_name)
            infoItemCount = findViewById(R.id.info_item_count)

            infoGroupName.text = "INFORMAÇÕES: CASA"

            val fakeData = mutableListOf(
                information {
                    header = "Casa 01"
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                },
                information {
                    header = "Casa 02"
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                },
                information {
                    header = "Casa 03"
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                },
                information {
                    header = "Casa 04"
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                },
                information {
                    header = "Casa 05"
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                },
                information {
                    header = "Casa 06"
                    content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                }
            )

            recyclerViewMain.adapter = InformationAdapter(fakeData)
            recyclerViewMain.layoutManager = LinearLayoutManager(this)

            infoItemCount.text = "${fakeData.size.toString()} ITENS"
        }
        // Botão 2
        else if (idReceived == MainActivity.button2.id) {

        }
        // Botão 3
        else if (idReceived == MainActivity.button3.id) {

        }
        // Botão 4
        else if (idReceived == MainActivity.button4.id) {

        }

        velocity = VelocityTracker.obtain()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
    }

    fun createMyConnectionErrorDialog(dps: Int): Dialog {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.connection_error_dialog)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        val scale: Float = resources.displayMetrics.density
        val widthDp: Int = (dps * scale + 0.5F).toInt()
        dialog.window?.setLayout(widthDp, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.setCancelable(true)

        val btnPositive: Button = dialog.findViewById(R.id.btn_positive)
        val btnNegative: Button = dialog.findViewById(R.id.btn_negative)

        btnPositive.setOnClickListener {
            // TODO: Tentar reconectar com o banco de dados
            dialog.dismiss()
        }

        btnNegative.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    var y1: Float = 0F
    var y2: Float = 0F

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val height = Resources.getSystem().displayMetrics.heightPixels

        // MIN_DISTANCE = width * x% of Screen Width
        val MIN_DISTANCE = (height * 0.20);

        when (event.action) {
            MotionEvent.ACTION_DOWN -> y1 = event.y

            MotionEvent.ACTION_UP -> {
                y2 = event.y

                val deltaY = y2 - y1
                if (abs(deltaY) > MIN_DISTANCE) {
                    super.onBackPressed()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                velocity.addMovement(event)
                velocity.computeCurrentVelocity(1000)

                //var xVelocity: Float = velocity.xVelocity
                var yVelocity: Float = velocity.yVelocity

                if (yVelocity > 4000) {
                    if (!alreadyCalled)
                        super.onBackPressed()
                }
            }
        }

        return super.onTouchEvent(event)
    }

}