/* Generated by AN DISI Unibo */ 
package it.unibo.attore1

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Attore1 ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
				val ActualTimer : Long = 50
				var Count : Long = 0
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("attore1 START")
						stateTimer = TimerActor("timer_s0", 
							scope, context!!, "local_tout_attore1_s0", ActualTimer )
					}
					 transition(edgeName="t00",targetState="doNotifyAll",cond=whenTimeout("local_tout_attore1_s0"))   
				}	 
				state("doNotifyAll") { //this:State
					action { //it:State
						emit("notifyAll", "notifyAll($Count)" ) 
						Count+=1
						stateTimer = TimerActor("timer_doNotifyAll", 
							scope, context!!, "local_tout_attore1_doNotifyAll", ActualTimer )
					}
					 transition(edgeName="t01",targetState="doNotifyAll",cond=whenTimeout("local_tout_attore1_doNotifyAll"))   
				}	 
			}
		}
}
