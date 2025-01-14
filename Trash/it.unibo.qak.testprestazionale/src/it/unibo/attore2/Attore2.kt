/* Generated by AN DISI Unibo */ 
package it.unibo.attore2

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Attore2 ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("attore2 START")
					}
					 transition(edgeName="t02",targetState="handleNotifyAll",cond=whenEvent("notifyAll"))
				}	 
				state("handleNotifyAll") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("notifyAll(NUMBER)"), Term.createTerm("notifyAll(COUNT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("------------------->${payloadArg(0)}")
						}
					}
					 transition(edgeName="t03",targetState="handleNotifyAll",cond=whenEvent("notifyAll"))
				}	 
			}
		}
}
