package scalatohoku.android.example

import tetris._
import android.app.Activity
import android.os.Bundle
import android.widget._
import android.view._
import android.content.Intent

class MainActivity extends Activity with TypedActivity {

  override def onCreate(savedInstanceState: Bundle):Unit = {
    super.onCreate(savedInstanceState)
    
    // res/layout/main.xmlで定義
    setContentView(R.layout.main)
    
    //ボタンイベント
    findView(TR.start_button)
    .setOnClickListener( new View.OnClickListener{
    	def onClick(view:View) = startTetrisActivity
    })

    findView(TR.start_button2)
    .setOnClickListener( new View.OnClickListener{
    	def onClick(view:View) = startTetris2Activity
    })

  }

  private def startTetrisActivity = {
	val intent = new Intent(this,classOf[TetrisActivity])
	intent.putExtra( "NOW" , new java.util.Date )//データ引継ぎ
	startActivity(intent)
  }

  private def startTetris2Activity = {
	val intent = new Intent(this,classOf[Tetris2Activity])
	intent.putExtra( "NOW" , new java.util.Date )//データ引継ぎ
	startActivity(intent)
  }
  
  /**
   * 他のライフサイクル例。
   * @see http://developer.android.com/reference/android/app/Activity.html
   */
  override def onStart()={super.onStart}
  override def onRestart()={super.onRestart}
  override def onResume()={super.onResume}
  override def onPause()={super.onPause}
  override def onStop()={super.onStop}
  override def onDestroy()={super.onDestroy}
  //override def onConfigurationChanged ..etc
}
