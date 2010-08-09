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

import android.view.Menu

import android.util.Log


class Tetris3Activity extends Activity with TypedActivity {

  private var mTetris3View: Tetris3View = _

  override def onCreate(savedInstanceState: Bundle):Unit = {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.tetris3)
    mTetris3View = findView(TR.tetris3)
    mTetris3View.setTextView(findView(TR.tetris3_text));
    mTetris3View.setMode(Tetris3View.READY);
  }

  val MENU_ID1 = Menu.FIRST

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    val m = menu.add(Menu.NONE, MENU_ID1, Menu.NONE, "Revert")
    m.setIcon(android.R.drawable.ic_menu_revert)
    super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    val dialog = new AlertDialog.Builder(this)

    item.getItemId match {
      case MENU_ID1 =>
        import android.content.Intent
        import scalatohoku.android.example.MainActivity
        val intent = new Intent(this, classOf[MainActivity])
        this.startActivity(intent)
        return true
      case _ =>
        return super.onOptionsItemSelected(item)
    }
  }
}

object Tetris3View {
  val PAUSE = 0
  val READY = 1
  val RUNNING = 2
  val LOSE = 3
}

class Tetris3View(context: Context, attrs: AttributeSet, defStyle: Int)
  extends SurfaceView(context, attrs, defStyle) with SurfaceHolder.Callback with Runnable{  
  import Tetris3View._

  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)

  private var mStatusText: TextView = _

  private var mainLoop: Thread = _

  private var mPaint: Paint = _

  private def initTetris3View: Unit = {
    setFocusable(true)

    getHolder().addCallback(this)

    mPaint = new Paint()

    Tetris.init
  }

  override def surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int){
  }

  override def surfaceCreated(holder: SurfaceHolder): Unit = {
    val canvas = holder.lockCanvas
    onDraw(canvas)
    holder.unlockCanvasAndPost(canvas);
  }

  override def surfaceDestroyed(holder: SurfaceHolder): Unit = {
  }


  override def run(): Unit = {
    while (mainLoop!=null){
       update()
       Option(getHolder.lockCanvas).foreach {canvas =>
         onDraw(canvas)
         getHolder.unlockCanvasAndPost(canvas);
       }
    }        
  }
    
  val mMoveDelay:Long = 300
  var mLastMove: Long = 0 

  def update(): Unit = { 
    if (mMode == RUNNING) {
      val now = System.currentTimeMillis()
      if (now - mLastMove > mMoveDelay) {
        Tetris.ticktack
        mLastMove = now
      }
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
/*
    if (code == KEYCODE_DPAD_DOWN ){
      if (mMode == READY | mMode == LOSE) {
        import android.content.Intent
        import scalatohoku.android.example.MainActivity
        val activity = this.getContext.asInstanceOf[Activity]
        val intent = new Intent(activity, classOf[MainActivity])
        activity.startActivity(intent)
      }
    } 
*/
   if(mMode == RUNNING){
     code match{
       case KEYCODE_DPAD_RIGHT => Tetris.action( Tetris.GO_RIGHT )
       case KEYCODE_DPAD_LEFT => Tetris.action( Tetris.GO_LEFT )
       case KEYCODE_DPAD_UP  => Tetris.action( Tetris.ROTATE )
       case _ =>
     }
   }  

   true
  }  

  var mMode = READY

  def setMode(newMode:Int): Unit = {
    val oldMode = mMode
    mMode = newMode;

    if(newMode == oldMode)
      return

    if(mMode == RUNNING && oldMode!=RUNNING){
      Tetris.init
      mStatusText.setVisibility(View.INVISIBLE);

      mainLoop = new Thread(this)
      mainLoop.start();

      return
    }

    val res = getContext.getResources
    var str: CharSequence = ""
    if (newMode == PAUSE) {
      str = res.getText(R.string.mode_pause)
    }
    if (newMode == READY && oldMode == RUNNING) {
      mainLoop = null
      str = res.getText(R.string.mode_ready)
    }
/*
    val handler = new Handler() {  
      override def handleMessage(msg: Message): Unit = {  
        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE)
      }
    }
    handler.sendEmptyMessage(0);  
*/
  }

  override def onDraw(canvas: Canvas): Unit = {

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

  initTetris3View

} // end of class
