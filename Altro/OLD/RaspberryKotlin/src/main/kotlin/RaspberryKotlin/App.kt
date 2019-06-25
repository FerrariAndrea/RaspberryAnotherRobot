/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package RaspberryKotlin

import myDevice.MyMatrixLed

class App {
    val greeting: String
        get() {
            return "Kotlin on rasp"
        }
}

fun main(args: Array<String>) {
    println(App().greeting)
    val ml = MyMatrixLed(1.toShort())
    ml.open()
    ml.brightness(15.toByte())
    ml.orientation(0)

    try {
        for (x in 0..19) {

            ml.draw(0, MyMatrixLed.ImgFactory(MyMatrixLed.IMG.SMILE))
            Thread.sleep(50)
            ml.draw(0, MyMatrixLed.ImgFactory(MyMatrixLed.IMG.WINK))
            Thread.sleep(110)
            ml.draw(0, MyMatrixLed.ImgFactory(MyMatrixLed.IMG.SMILE))
            Thread.sleep(500)
        }
        ml.draw(0, MyMatrixLed.ImgFactory(MyMatrixLed.IMG.SMILE))
    } catch (e: Exception) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }

}
