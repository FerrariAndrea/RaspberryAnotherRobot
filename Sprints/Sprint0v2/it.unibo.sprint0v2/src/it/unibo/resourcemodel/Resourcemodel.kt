/* Generated by AN DISI Unibo */ 
package it.unibo.resourcemodel

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Resourcemodel ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Start resourcemodel")
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("wait") { //this:State
					action { //it:State
					}
					 transition(edgeName="t02",targetState="handleUpdate",cond=whenDispatch("modelUpdate"))
					transition(edgeName="t03",targetState="handleChange",cond=whenDispatch("modelChange"))
				}	 
				state("handleUpdate") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("modelUpdate(TARGET,VALUE)"), Term.createTerm("modelUpdate(TARGET,VALUE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												var Target=payloadArg(0)
												var Value=payloadArg(1)
								val ForTest = "ResourcesModelUpdate"
								forward("test", "test($ForTest)" ,"tester" ) 
								println("resourcemodel->richiesta salvataggio modifica a kb(persistenza): $Target , $Value")
								forward("kbModelUpdate", "kbModelUpdate($Target,$Value)" ,"kb" ) 
								println("resourcemodel->Propagazione modifica (esempio tramite coap): $Target , $Value")
						}
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
				state("handleChange") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("modelChange(TARGET,VALUE)"), Term.createTerm("modelChange(TARGET,VALUE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												var Target=payloadArg(0)
												var Value=payloadArg(1)
								println("resourcemodel->applicazione modifica: $Target , $Value (esempio spostamento robot)")
								val ForTest = "ResourcesModelChange"
								forward("test", "test($ForTest)" ,"tester" ) 
								forward("modelChanged", "modelChanged($Target,$Value)" ,"mindrobot" ) 
						}
					}
					 transition( edgeName="goto",targetState="wait", cond=doswitch() )
				}	 
			}
		}
}