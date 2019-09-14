/* Generated by AN DISI Unibo */ 
package it.unibo.roomexplorer

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Roomexplorer ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String {
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
			var mapEmpty    = false
			val mapname     = "roomBoundary"  //"roomMbot3"		// 
			var Tback       = 0
			var NumStep     = 0
		 
			//REAL ROBOT
			//var StepTime   = 1000	 
			//var PauseTime  = 500 
		
			//VIRTUAL ROBOT
			var StepTime   = 330	 
			var PauseTime  = 250
			var RotateTime = 300
		
			//OTHER
			var Move = ""
			//var PauseTimeL  = PauseTime.toLong()
			var secondLap : Boolean = false
			var mustStop : Boolean = false
			
			var needExploreBound : Boolean =false
			var tableFound : Boolean =false
			var directionSud : Boolean =false
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						itunibo.coap.observer.resourceObserverCoapClient.create( "coap://localhost:5683/resourcemodel"  )
						itunibo.planner.plannerUtil.initAI(  )
						itunibo.planner.moveUtils.showCurrentRobotState(  )
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("waitCmd") { //this:State
					action { //it:State
						println("Waiting for exploration cmd...")
					}
					 transition(edgeName="t00",targetState="handeCmd",cond=whenDispatch("doExplor"))
				}	 
				state("handeCmd") { //this:State
					action { //it:State
						storeCurrentMessageForReply()
						needExploreBound=false
						if( checkMsgContent( Term.createTerm("doExplor(TARGET)"), Term.createTerm("doExplor(TARGET)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												needExploreBound = (payloadArg(0)=="bound")
												secondLap=false
						}
					}
					 transition( edgeName="goto",targetState="exploreBounds", cond=doswitchGuarded({needExploreBound}) )
					transition( edgeName="goto",targetState="exploreTale", cond=doswitchGuarded({! needExploreBound}) )
				}	 
				state("exploreBounds") { //this:State
					action { //it:State
						if(!secondLap){ println("Start explore bounds.")
						NumStep=0
						 }
					}
					 transition( edgeName="goto",targetState="rotateEast", cond=doswitchGuarded({secondLap}) )
					transition( edgeName="goto",targetState="detectPerimeter", cond=doswitchGuarded({! secondLap}) )
				}	 
				state("rotateEast") { //this:State
					action { //it:State
						NumStep=0
						Move="a"
						forward("onerotationstep", "onerotationstep(a)" ,"onerotateforward" ) 
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
					}
					 transition(edgeName="t01",targetState="detectPerimeter",cond=whenDispatch("rotationOk"))
				}	 
				state("block") { //this:State
					action { //it:State
						println("BLOCk")
					}
				}	 
				state("detectPerimeter") { //this:State
					action { //it:State
						NumStep++
						itunibo.planner.plannerUtil.showMap(  )
					}
					 transition( edgeName="goto",targetState="goOneStepAhead", cond=doswitchGuarded({(NumStep<5)}) )
					transition( edgeName="goto",targetState="perimeterWalked", cond=doswitchGuarded({! (NumStep<5)}) )
				}	 
				state("goOneStepAhead") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.attemptTomoveAhead(myself ,StepTime, "onecellforward" )
					}
					 transition(edgeName="t02",targetState="handleStepOk",cond=whenDispatch("stepOk"))
					transition(edgeName="t03",targetState="checkingObject",cond=whenEvent("collision"))
				}	 
				state("handleStepOk") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.updateMapAfterAheadOk(myself)
						delay(500) 
					}
					 transition( edgeName="goto",targetState="goOneStepAhead", cond=doswitch() )
				}	 
				state("checkingObject") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("collision(OBJECT)"), Term.createTerm("collision(OBJECT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								val ObjName = payloadArg(0)
								println("OGGETTO IN COLLISIONE: $ObjName")
								if((ObjName.equals("pantry") || ObjName.equals("dishwasher") || ObjName.equals("fridge")|| ObjName.equals("table"))){ 
												val XTemp = itunibo.planner.plannerUtil.getPosX()
												val YTemp = itunibo.planner.plannerUtil.getPosY()	
												tableFound	=ObjName.equals("table")		
								forward("modelUpdateMap", "modelUpdateMap($ObjName,$XTemp,$YTemp)" ,"kb" ) 
								 }
						}
					}
					 transition( edgeName="goto",targetState="handleStepFail", cond=doswitchGuarded({needExploreBound}) )
					transition( edgeName="goto",targetState="handleStepFailTable", cond=doswitchGuarded({! needExploreBound}) )
				}	 
				state("handleStepFail") { //this:State
					action { //it:State
						delay(500) 
						val MapStr =  itunibo.planner.plannerUtil.getMapOneLine()
						forward("modelUpdate", "modelUpdate(roomMap,$MapStr)" ,"resourcemodel" ) 
						itunibo.planner.plannerUtil.wallFound(  )
						if(secondLap){ Move="d"
						forward("onerotationstep", "onerotationstep(d)" ,"onerotateforward" ) 
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
						 }
						else
						 { Move="a"
						 forward("onerotationstep", "onerotationstep(a)" ,"onerotateforward" ) 
						 itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
						  }
					}
					 transition(edgeName="t04",targetState="detectPerimeter",cond=whenDispatch("rotationOk"))
				}	 
				state("perimeterWalked") { //this:State
					action { //it:State
						if(!secondLap){ println("FINAL MAP")
						itunibo.planner.moveUtils.showCurrentRobotState(  )
						itunibo.planner.plannerUtil.saveMap( mapname  )
						secondLap = true
						 }
						else
						 { mustStop = true
						  }
					}
					 transition( edgeName="goto",targetState="endOfJobBounds", cond=doswitchGuarded({mustStop}) )
					transition( edgeName="goto",targetState="exploreBounds", cond=doswitchGuarded({! mustStop}) )
				}	 
				state("endOfJobBounds") { //this:State
					action { //it:State
						delay(500) 
						forward("onerotationstep", "onerotationstep(d)" ,"onerotateforward" ) 
						Move="d"
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
						println("Perimeter completely walked. Exit.")
					}
					 transition(edgeName="t05",targetState="reply",cond=whenDispatch("rotationOk"))
				}	 
				state("reply") { //this:State
					action { //it:State
						replyToCaller("endExplor", "endExplor(ok)")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("exploreTale") { //this:State
					action { //it:State
						println("Start explore table.")
						tableFound=false
					}
					 transition( edgeName="goto",targetState="goOneStepAhead", cond=doswitch() )
				}	 
				state("handleStepFailTable") { //this:State
					action { //it:State
						
								val MapStr =  itunibo.planner.plannerUtil.getMapOneLine()
						forward("modelUpdate", "modelUpdate(roomMap,$MapStr)" ,"resourcemodel" ) 
						itunibo.planner.plannerUtil.wallFound(  )
					}
					 transition( edgeName="goto",targetState="endOfJobTable", cond=doswitchGuarded({tableFound}) )
					transition( edgeName="goto",targetState="askOrientation", cond=doswitchGuarded({! tableFound}) )
				}	 
				state("askOrientation") { //this:State
					action { //it:State
						forward("modelRequest", "modelRequest(robot,location)" ,"kb" ) 
					}
					 transition(edgeName="t06",targetState="rotateBefore",cond=whenDispatch("modelRobotResponse"))
				}	 
				state("rotateBefore") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("modelRobotResponse(X,Y,O)"), Term.createTerm("modelRobotResponse(X,Y,O)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								val actualOrientation = payloadArg(2)
								if(actualOrientation=="sud"){ Move="a"
								forward("onerotationstep", "onerotationstep(a)" ,"onerotateforward" ) 
								itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
								 }
								if(actualOrientation=="nord"){ Move="d"
								forward("onerotationstep", "onerotationstep(d)" ,"onerotateforward" ) 
								itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
								 }
						}
					}
					 transition(edgeName="t07",targetState="moveOne",cond=whenDispatch("rotationOk"))
				}	 
				state("moveOne") { //this:State
					action { //it:State
						delay(500) 
						itunibo.planner.moveUtils.attemptTomoveAhead(myself ,StepTime, "onecellforward" )
					}
					 transition(edgeName="t08",targetState="rotateAfter",cond=whenDispatch("stepOk"))
					transition(edgeName="t09",targetState="checkingObject",cond=whenEvent("collision"))
				}	 
				state("rotateAfter") { //this:State
					action { //it:State
						if(Move=="a"){ forward("onerotationstep", "onerotationstep(a)" ,"onerotateforward" ) 
						 }
						else
						 { forward("onerotationstep", "onerotationstep(d)" ,"onerotateforward" ) 
						  }
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
					}
					 transition(edgeName="t010",targetState="goOneStepAhead",cond=whenDispatch("rotationOk"))
				}	 
				state("endOfJobTable") { //this:State
					action { //it:State
						println("Table exploration end.")
						replyToCaller("endExplor", "endExplor(ok)")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
			}
		}
}
