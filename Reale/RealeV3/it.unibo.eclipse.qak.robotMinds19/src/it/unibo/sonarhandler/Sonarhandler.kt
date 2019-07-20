/* Generated by AN DISI Unibo */ 
package it.unibo.sonarhandler

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Sonarhandler ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Start Sonarhandler")
					}
					 transition(edgeName="t05",targetState="handleSonar",cond=whenEvent("sonar"))
					transition(edgeName="t06",targetState="handleSonar",cond=whenEvent("sonarRobot"))
					transition(edgeName="t07",targetState="handleSonar",cond=whenEvent("sonarLeft"))
					transition(edgeName="t08",targetState="handleSonar",cond=whenEvent("sonarRigth"))
				}	 
				state("handleSonar") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("sonar(DISTANCE)"), Term.createTerm("sonar(DISTANCE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("modelChange", "modelChange(sonarRobot,${payloadArg(0)})" ,"resourcemodel" ) 
						}
						if( checkMsgContent( Term.createTerm("sonar(DISTANCE)"), Term.createTerm("sonar(DISTANCE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("modelChange", "modelChange(sonarLeft,${payloadArg(0)})" ,"resourcemodel" ) 
						}
						if( checkMsgContent( Term.createTerm("sonar(DISTANCE)"), Term.createTerm("sonar(DISTANCE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("modelChange", "modelChange(sonarRigth,${payloadArg(0)})" ,"resourcemodel" ) 
						}
					}
					 transition(edgeName="t09",targetState="handleSonar",cond=whenEvent("sonar"))
					transition(edgeName="t010",targetState="handleSonar",cond=whenEvent("sonarRobot"))
					transition(edgeName="t011",targetState="handleSonar",cond=whenEvent("sonarLeft"))
					transition(edgeName="t012",targetState="handleSonar",cond=whenEvent("sonarRigth"))
				}	 
			}
		}
}