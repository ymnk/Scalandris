package scalatohoku.android.example.tetris

import android.os._
import android.widget._
import android.view._
import android.content._
import android.app._
import service._
import android.view.KeyEvent._
import scalatohoku.android.example.R
import scalatohoku.android.example.TR
import scalatohoku.android.example.TypedActivity

import android.util.AttributeSet
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color

import android.util.Log


class Tetris2Activity extends Activity with TypedActivity {

  private var mTetris2View: Tetris2View = _

  override def onCreate(savedInstanceState: Bundle):Unit = {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.tetris2)
    mTetris2View = findView(TR.tetris2)
    mTetris2View.setTextView(findView(TR.tetris2_text));
    mTetris2View.setMode(Tetris2View.READY);
  }
}

object Tetris2View {
  val PAUSE = 0
  val READY = 1
  val RUNNING = 2
  val LOSE = 3
}

class Tetris2View(context: Context, attrs: AttributeSet, defStyle: Int)
  extends View(context, attrs, defStyle) {  
  import Tetris2View._

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  private var mStatusText: TextView = _

  private def initTetris2View: Unit = {
    setFocusable(true)
  }

  private val mRedrawHandler = new RefreshHandler()

  class RefreshHandler extends Handler {
    override def handleMessage(msg: Message): Unit= {
      Tetris2View.this.update()
      Tetris2View.this.invalidate()
    }

    def sleep(delayMillis: Long): Unit = {
      this.removeMessages(0);
      sendMessageDelayed(obtainMessage(0), delayMillis);
    }
  }

  val mMoveDelay:Long = 500
  var mLastMove: Long = 0 

  def update(): Unit = { 
    if (mMode == RUNNING) {
      val now = System.currentTimeMillis()
      if (now - mLastMove > mMoveDelay) {
        Tetris.ticktack
        mLastMove = now
      }
      mRedrawHandler.sleep(mMoveDelay);
    }

    if(Tetris.gameover){
      setMode(READY)
    }

  }

  override def onKeyDown(code: Int, event: KeyEvent): Boolean = {
    if (code == KeyEvent.KEYCODE_DPAD_UP) {
      if (mMode == READY | mMode == LOSE) {
        setMode(RUNNING)
      }
    }

    if (code == KEYCODE_DPAD_DOWN ){
      if (mMode == READY | mMode == LOSE) {
        import android.content.Intent
        import scalatohoku.android.example.MainActivity
        val activity = this.getContext.asInstanceOf[Activity]
        val intent = new Intent(activity, classOf[MainActivity])
        activity.startActivity(intent)
      }
    } 

   if(mMode == RUNNING){
     code match{
       case KEYCODE_DPAD_RIGHT => Tetris.action( Tetris.GO_RIGHT )
       case KEYCODE_DPAD_LEFT => Tetris.action( Tetris.GO_LEFT )
       case KEYCODE_DPAD_UP  => Tetris.action( Tetris.ROTATE )
       case _ =>
     }
     invalidate()
   }  

   true
  }  

  var mMode = READY

  def setMode(newMode:Int): Unit = {
    val oldMode = mMode
    mMode = newMode;

    if(mMode == RUNNING && oldMode!=RUNNING){
      Tetris.init
      mStatusText.setVisibility(View.INVISIBLE);
      update();
      return
    }

    val res = getContext.getResources
    var str: CharSequence = ""
    if (newMode == PAUSE) {
      str = res.getText(R.string.mode_pause)
    }
    if (newMode == READY) {
      str = res.getText(R.string.mode_ready)
    }

    mStatusText.setText(str);
    mStatusText.setVisibility(View.VISIBLE);
  }

  override def onDraw(canvas: Canvas): Unit = {
    super.onDraw(canvas)

    canvas.drawColor(Color.BLACK);

    val mPaint = new Paint()

    mPaint.setStyle(Paint.Style.FILL)

    val lines = Tetris.renderViewBuffer.split("\n")

    if(lines.length < 10)
      return

    val offsetx=10
    val offsety=10
    val w = 20
    val h = 20
    var x = offsetx
    var y = offsety

    for(line <- lines){
      for(c <- line){

        c match {
          case ' ' =>
            mPaint.setARGB(255, 0, 0, 255)
            canvas.drawRect(x, y, x+w, y+h, mPaint);
          case 'X' =>
            mPaint.setARGB(255, 0, 0, 0)
            canvas.drawRect(x, y, x+w, y+h, mPaint);
          case '#' =>
            mPaint.setARGB(255, 0, 255, 0)
            canvas.drawRect(x, y, x+w, y+h, mPaint);
            mPaint.setARGB(255, 0, 0, 0)
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(x, y, x+w-1, y+h-1, mPaint);
            mPaint.setStyle(Paint.Style.FILL)
          case _ =>
        }
        x+=w
      }
      x = offsetx
      y += h
    }
  }

  def setTextView(newView: TextView): Unit = {
    mStatusText = newView
  }

  initTetris2View

} // end of class
