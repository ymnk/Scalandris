package scalatohoku.android.example.tests

import junit.framework.Assert._
import _root_.android.test.AndroidTestCase

class UnitTests extends AndroidTestCase {
  def testPackageIsCorrect {
    assertEquals("scalatohoku.android.example", getContext.getPackageName)
  }
}
