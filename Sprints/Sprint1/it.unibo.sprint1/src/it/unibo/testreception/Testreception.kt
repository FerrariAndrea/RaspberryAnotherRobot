/* Generated by AN DISI Unibo */ 
package it.unibo.testreception

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Testreception ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Start Test")
					}
					 transition( edgeName="goto",targetState="waitModelChange", cond=doswitch() )
				}	 
				state("waitModelChange") { //this:State
					action { //it:State
					}
					 transition(edgeName="t00",targetState="printReception",cond=whenDispatch("modelChange"))
				}	 
				state("printReception") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("modelUpdate(TARGET,VALUE)"), Term.createTerm("modelUpdate(robot,V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								println("MSG Received from external QAK")
						}
					}
				}	 
			}
		}
}
