/* Generated by AN DISI Unibo */ 
package it.unibo.onecellforward

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Onecellforward ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
				var FoundObstacle = false
				var StepTime = 0L
				var Duration : Long =0
				var DistanzaMinima :Long =10
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Start onecellforward")
					}
					 transition( edgeName="goto",targetState="ready", cond=doswitch() )
				}	 
				state("ready") { //this:State
					action { //it:State
					}
					 transition(edgeName="t026",targetState="checkFirst",cond=whenDispatch("onestep"))
				}	 
				state("checkFirst") { //this:State
					action { //it:State
						println("---------->checkFirst")
						
									storeCurrentMessageForReply()
									FoundObstacle = false 
						if( checkMsgContent( Term.createTerm("onestep(DURATION)"), Term.createTerm("onestep(TIME)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								StepTime = payloadArg(0).toLong()
						}
						forward("internalReq", "internalReq(lastSonarRobot)" ,"sonarhandler" ) 
						println("----------------WAITING")
					}
					 transition(edgeName="t027",targetState="waitingForcheckFirstSonar",cond=whenEvent("lastSonarRobot"))
				}	 
				state("waitingForcheckFirstSonar") { //this:State
					action { //it:State
						println("---------->waitingForcheckFirstSonar")
						if( checkMsgContent( Term.createTerm("lastSonarRobot(DISATNCE)"), Term.createTerm("lastSonarRobot(DISTANCE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								var distance = Integer.parseInt( payloadArg(0) ) 
								              FoundObstacle = (distance<DistanzaMinima/2) 
								if(FoundObstacle){ replyToCaller("stepFail", "stepFail(obstacle,$distance) ")
								println("Actor: OneStepForward; State:cantDoOneStep")
								 }
								else
								 { println("Actor: OneStepForward; State: OK-> $distance")
								  }
						}
					}
					 transition( edgeName="goto",targetState="ready", cond=doswitchGuarded({FoundObstacle}) )
					transition( edgeName="goto",targetState="doMoveForward", cond=doswitchGuarded({! FoundObstacle}) )
				}	 
				state("doMoveForward") { //this:State
					action { //it:State
						println("-->doMoveForward")
						forward("modelChange", "modelChange(robot,w)" ,"resourcemodel" ) 
						forward("setTimer", "setTimer($StepTime)" ,"timer" ) 
						itunibo.planner.plannerUtil.startTimer(  )
					}
					 transition(edgeName="t028",targetState="endDoMoveForward",cond=whenEvent("tickTimer"))
					transition(edgeName="t029",targetState="handleSonarRobot",cond=whenEvent("sonarRobot"))
				}	 
				state("endDoMoveForward") { //this:State
					action { //it:State
						println("---------------------------OK----->endDoMoveForward")
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						forward("modelUpdate", "modelUpdate(robot,w)" ,"kb" ) 
					}
					 transition( edgeName="goto",targetState="endCorrezioneRotta", cond=doswitch() )
				}	 
				state("endCorrezioneRotta") { //this:State
					action { //it:State
						replyToCaller("stepOk", "stepOk(ok)")
					}
					 transition( edgeName="goto",targetState="ready", cond=doswitch() )
				}	 
				state("handleSonarRobot") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("sonar(DISTANCE)"), Term.createTerm("sonar(DISTANCE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								val distance = Integer.parseInt( payloadArg(0) ) 
								              FoundObstacle = (distance<DistanzaMinima) 
								if(FoundObstacle){ itunibo.planner.moveUtils.setDuration(myself)
								 }
								println("handleSonarRobot-ONESTEP--------------------------------------------->$distance")
						}
					}
					 transition( edgeName="goto",targetState="stepFail", cond=doswitchGuarded({FoundObstacle}) )
					transition( edgeName="goto",targetState="mustGoOn", cond=doswitchGuarded({! FoundObstacle}) )
				}	 
				state("stepFail") { //this:State
					action { //it:State
						forward("resetTimer", "resetTimer(reset)" ,"timer" ) 
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						solve("wduration(TIME)","") //set resVar	
						Duration=getCurSol("TIME").toString().toLong()
					}
					 transition( edgeName="goto",targetState="goBackFromFail", cond=doswitch() )
				}	 
				state("goBackFromFail") { //this:State
					action { //it:State
						forward("modelChange", "modelChange(robot,s)" ,"resourcemodel" ) 
						delay(Duration)
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						replyToCaller("stepFail", "stepFail(obstacle,$Duration) ")
					}
					 transition( edgeName="goto",targetState="ready", cond=doswitch() )
				}	 
				state("mustGoOn") { //this:State
					action { //it:State
						println("->mustGoOn")
					}
					 transition(edgeName="t030",targetState="endDoMoveForward",cond=whenEvent("tickTimer"))
					transition(edgeName="t031",targetState="handleSonarRobot",cond=whenEvent("sonarRobot"))
				}	 
			}
		}
}