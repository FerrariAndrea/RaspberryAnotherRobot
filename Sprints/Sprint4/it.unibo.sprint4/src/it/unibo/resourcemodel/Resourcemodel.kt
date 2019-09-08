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
		var dobackstep = false
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Start resourcemodel")
						solve("consult('sysRules.pl')","") //set resVar	
						solve("consult('resourceModel.pl')","") //set resVar	
						solve("showResourceModel","") //set resVar	
						itunibo.coap.modelResourceCoap.create(myself ,"resourcemodel" )
					}
					 transition( edgeName="goto",targetState="waitModelChange", cond=doswitch() )
				}	 
				state("waitModelChange") { //this:State
					action { //it:State
					}
					 transition(edgeName="t00",targetState="changeModel",cond=whenDispatch("modelChange"))
					transition(edgeName="t01",targetState="updateModel",cond=whenDispatch("modelUpdate"))
				}	 
				state("updateModel") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("modelUpdate(TARGET,VALUE)"), Term.createTerm("modelUpdate(robot,V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								itunibo.robot.resourceModelSupport.updateRobotModel(myself ,payloadArg(1) )
						}
						if( checkMsgContent( Term.createTerm("modelUpdate(TARGET,VALUE)"), Term.createTerm("modelUpdate(sonarRobot,V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								itunibo.robot.resourceModelSupport.updateSonarRobotModel(myself ,payloadArg(1) )
						}
						if( checkMsgContent( Term.createTerm("modelUpdate(TARGET,VALUE)"), Term.createTerm("modelUpdate(metre,V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								itunibo.robot.resourceModelSupport.updateSonarRobotModel(myself ,payloadArg(1) )
						}
						if( checkMsgContent( Term.createTerm("modelUpdate(TARGET,VALUE)"), Term.createTerm("modelUpdate(pos,V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								itunibo.robot.resourceModelSupport.updatePosRobotModel(myself ,payloadArg(1) )
						}
					}
					 transition( edgeName="goto",targetState="waitModelChange", cond=doswitch() )
				}	 
				state("changeModel") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("modelChange(TARGET,VALUE)"), Term.createTerm("modelChange(robot,V)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("local_modelChanged", "modelChanged(robot,${payloadArg(1)})" ,"mindrobot" ) 
						}
					}
					 transition( edgeName="goto",targetState="waitModelChange", cond=doswitch() )
				}	 
			}
		}
}
