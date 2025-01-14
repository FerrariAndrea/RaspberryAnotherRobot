/* Generated by AN DISI Unibo */ 
package it.unibo.butler

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Butler ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
		
		//var StepTime   = 1000L	//long		//for real
		//var RotateTime = 560L	//long		//for real 
		//var PauseTime  = 1000L 
		var RotateStep = 255L	//long		//for real 
		var BackTime   = 500L
		
		var StepTime   = 350L	//for virtual
		var RotateTime = 300L	//for virtual
		var PauseTime  = 100L 
		var RobotDirection = ""
		
		var goingHome  = false 
		 
		var Tback      = 0L
		
		var stepCounter = 0 
		var Curmove = ""
		var curmoveIsForward = false
		
		var Direction = "" 
		
		//var needInit =1
		
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("&&&  butler STARTED")
						solve("consult('sysRules.pl')","") //set resVar	
						solve("consult('floorMap.pl')","") //set resVar	
						solve("showMap","") //set resVar	
					}
					 transition( edgeName="goto",targetState="exploreTheRoom", cond=doswitch() )
				}	 
				state("exploreTheRoom") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="moveAhead", cond=doswitch() )
				}	 
				state("moveAhead") { //this:State
					action { //it:State
						forward("onestep", "onestep($StepTime)" ,"butlerstep" ) 
					}
					 transition(edgeName="t00",targetState="hadleStepOk",cond=whenDispatch("stepOk"))
					transition(edgeName="t01",targetState="hadleStepKo",cond=whenDispatch("stepFail"))
				}	 
				state("hadleStepOk") { //this:State
					action { //it:State
						solve("updateMapAfterStep","") //set resVar	
						solve("showMap","") //set resVar	
						delay(PauseTime)
					}
					 transition( edgeName="goto",targetState="moveAhead", cond=doswitch() )
				}	 
				state("hadleStepKo") { //this:State
					action { //it:State
						var Tback = 0L
						println("$name in ${currentState.stateName} | $currentMsg")
						println("&&& moveAhead failed")
						if( checkMsgContent( Term.createTerm("stepFail(R,T)"), Term.createTerm("stepFail(R,D)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								Tback=payloadArg(1).toString().toLong() 
								println(" ..................................  BACK TIME= $Tback")
						}
						if( Tback > StepTime * 2 /3 ) Tback = 0 else Tback = Tback / 3 
						if(Tback>0){ forward("modelChange", "modelChange(robot,s)" ,"resourcemodel" ) 
						delay(Tback)
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						 }
						else
						 { solve("updateMapAfterStep","") //set resVar	
						  }
						solve("showMap","") //set resVar	
						delay(PauseTime)
						forward("modelChange", "modelChange(robot,a)" ,"resourcemodel" ) 
						solve("changeDirection","") //set resVar	
						solve("robotdirection(D)","") //set resVar	
						RobotDirection = getCurSol("D").toString()
						println("RobotDirection= ${RobotDirection}")
					}
					 transition( edgeName="goto",targetState="endOfExploration", cond=doswitchGuarded({(getCurSol("D").toString() == "sud")}) )
					transition( edgeName="goto",targetState="tuning", cond=doswitchGuarded({! (getCurSol("D").toString() == "sud")}) )
				}	 
				state("tuning") { //this:State
					action { //it:State
						delay(RotateTime)
					}
					 transition( edgeName="goto",targetState="moveAhead", cond=doswitch() )
				}	 
				state("endOfExploration") { //this:State
					action { //it:State
						println("EXPLORATION ENDS")
						itunibo.planner.plannerUtil.getDuration(  )
						delay(PauseTime)
						itunibo.planner.plannerUtil.initAI(  )
					}
					 transition( edgeName="goto",targetState="seeSudCopy", cond=doswitch() )
				}	 
				state("gotToFridge") { //this:State
					action { //it:State
						var x="0"
						var y="0"
						solve("get(fridge,X,Y)","") //set resVar	
						x= getCurSol("X").toString()
								  y= getCurSol("Y").toString()
						println("--->x=$x, y=$y")
						println("&&&  explorer STARTED")
						solve("consult('moves.pl')","") //set resVar	
						itunibo.planner.plannerUtil.setGoal( x, y  )
						itunibo.planner.moveUtils.doPlan(myself)
					}
					 transition( edgeName="goto",targetState="executePlannedActions", cond=doswitchGuarded({itunibo.planner.moveUtils.existPlan()}) )
					transition( edgeName="goto",targetState="endOfJob", cond=doswitchGuarded({! itunibo.planner.moveUtils.existPlan()}) )
				}	 
				state("goalOk") { //this:State
					action { //it:State
						println("ON THE TARGET CELL !!!")
					}
					 transition( edgeName="goto",targetState="atHome", cond=doswitchGuarded({goingHome}) )
					transition( edgeName="goto",targetState="backToHome", cond=doswitchGuarded({! goingHome}) )
				}	 
				state("checkAndDoAction") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="doForwardMove", cond=doswitchGuarded({curmoveIsForward}) )
					transition( edgeName="goto",targetState="doTheMove", cond=doswitchGuarded({! curmoveIsForward}) )
				}	 
				state("executePlannedActions") { //this:State
					action { //it:State
						solve("retract(move(M))","") //set resVar	
						if(currentSolution.isSuccess()) { Curmove = getCurSol("M").toString() 
						              curmoveIsForward=(Curmove == "w")
						 }
						else
						{ Curmove = ""; curmoveIsForward=false
						 }
					}
					 transition( edgeName="goto",targetState="checkAndDoAction", cond=doswitchGuarded({(Curmove.length>0) }) )
					transition( edgeName="goto",targetState="goalOk", cond=doswitchGuarded({! (Curmove.length>0) }) )
				}	 
				state("doTheMove") { //this:State
					action { //it:State
						forward("modelChange", "modelChange(robot,$Curmove)" ,"resourcemodel" ) 
						delay(RotateTime)
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						itunibo.planner.moveUtils.doPlannedMove(myself ,Curmove )
						delay(PauseTime)
					}
					 transition( edgeName="goto",targetState="executePlannedActions", cond=doswitch() )
				}	 
				state("executePlannedActions") { //this:State
					action { //it:State
						solve("retract(move(M))","") //set resVar	
						if(currentSolution.isSuccess()) { Curmove = getCurSol("M").toString() 
						              curmoveIsForward=(Curmove == "w")
						 }
						else
						{ Curmove = ""; curmoveIsForward=false
						 }
					}
					 transition( edgeName="goto",targetState="checkAndDoAction", cond=doswitchGuarded({(Curmove.length>0) }) )
					transition( edgeName="goto",targetState="goalOk", cond=doswitchGuarded({! (Curmove.length>0) }) )
				}	 
				state("goalOk") { //this:State
					action { //it:State
						println("ON THE TARGET CELL !!!")
					}
					 transition( edgeName="goto",targetState="atHome", cond=doswitchGuarded({goingHome}) )
					transition( edgeName="goto",targetState="backToHome", cond=doswitchGuarded({! goingHome}) )
				}	 
				state("checkAndDoAction") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="doForwardMove", cond=doswitchGuarded({curmoveIsForward}) )
					transition( edgeName="goto",targetState="doTheMove", cond=doswitchGuarded({! curmoveIsForward}) )
				}	 
				state("doTheMove") { //this:State
					action { //it:State
						forward("modelChange", "modelChange(robot,$Curmove)" ,"resourcemodel" ) 
						delay(RotateTime)
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						itunibo.planner.moveUtils.doPlannedMove(myself ,Curmove )
						delay(PauseTime)
					}
					 transition( edgeName="goto",targetState="executePlannedActions", cond=doswitch() )
				}	 
				state("doForwardMove") { //this:State
					action { //it:State
						itunibo.planner.plannerUtil.startTimer(  )
						forward("onestep", "onestep($StepTime)" ,"onecellforward" ) 
					}
					 transition(edgeName="t02",targetState="handleStepOk",cond=whenDispatch("stepOk"))
					transition(edgeName="t03",targetState="hadleStepFail",cond=whenDispatch("stepFail"))
				}	 
				state("handleStepOk") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.doPlannedMove(myself ,"w" )
						delay(PauseTime)
					}
					 transition( edgeName="goto",targetState="executePlannedActions", cond=doswitch() )
				}	 
				state("hadleStepFail") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("stepFail(R,T)"), Term.createTerm("stepFail(R,D)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								Tback=payloadArg(1).toString().toLong()  
								 if( Tback > StepTime * 2 / 3 ) Tback = 0 else Tback=Tback/2
								println(" ..................................  BACK TIME= $Tback over $StepTime")
						}
						println("$name in ${currentState.stateName} | $currentMsg")
						if(Tback > 0 ){ forward("modelChange", "modelChange(robot,s)" ,"resourcemodel" ) 
						delay(Tback)
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						solve("direction(D)","") //set resVar	
						println("direction at fail: ${getCurSol("D").toString()}")
						itunibo.planner.plannerUtil.doMove( getCurSol("D").toString()  )
						println("MAP when hadleStepFail")
						itunibo.planner.plannerUtil.showMap(  )
						 }
						delay(PauseTime)
					}
					 transition( edgeName="goto",targetState="replan", cond=doswitchGuarded({(Tback > 0)}) )
					transition( edgeName="goto",targetState="executePlannedActions", cond=doswitchGuarded({! (Tback > 0)}) )
				}	 
				state("replan") { //this:State
					action { //it:State
						if(goingHome){ solve("retractall(move(_))","") //set resVar	
						itunibo.planner.plannerUtil.setGoal( 0, 0  )
						itunibo.planner.moveUtils.doPlan(myself)
						 }
						solve("dialog(F)","") //set resVar	
					}
					 transition( edgeName="goto",targetState="executePlannedActions", cond=doswitch() )
				}	 
				state("backToHome") { //this:State
					action { //it:State
						println("&&&  backToHome")
						solve("direction(D)","") //set resVar	
						println("direction at backToHome: ${getCurSol("D").toString()}")
						println("MAP BEFORE backToHome")
						itunibo.planner.plannerUtil.showMap(  )
						solve("retractall(move(_))","") //set resVar	
						itunibo.planner.plannerUtil.setGoal( 0, 0  )
						goingHome=true
						itunibo.planner.moveUtils.doPlan(myself)
					}
					 transition( edgeName="goto",targetState="executePlannedActions", cond=doswitch() )
				}	 
				state("doGoHomeActions") { //this:State
					action { //it:State
						solve("retract(move(M))","") //set resVar	
						if(currentSolution.isSuccess()) { Curmove = getCurSol("M").toString() 
						itunibo.planner.moveUtils.doPlannedMove(myself ,getCurSol("M").toString() )
						forward("modelChange", "modelChange(robot,$Curmove)" ,"resourcemodel" ) 
						if(Curmove == "w" ){ delay(StepTime)
						 }
						else
						 { delay(RotateTime)
						  }
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						 }
						else
						{ Curmove = "" 
						 }
						delay(PauseTime)
					}
					 transition( edgeName="goto",targetState="doGoHomeActions", cond=doswitchGuarded({(Curmove.length>0)}) )
					transition( edgeName="goto",targetState="atHome", cond=doswitchGuarded({! (Curmove.length>0)}) )
				}	 
				state("atHome") { //this:State
					action { //it:State
						println(" ---- AT HOME --- ")
						solve("direction(D)","") //set resVar	
						Direction = getCurSol("D").toString() 
						println(getCurSol("D").toString())
						
						val map = itunibo.planner.plannerUtil.getMap() 
						println(map)
						println("direction at home: ${getCurSol("D").toString()}")
						if(Direction == "leftDir" || Direction == "upDir" ){ forward("modelChange", "modelChange(robot,w)" ,"resourcemodel" ) 
						 }
					}
					 transition( edgeName="goto",targetState="seeSud", cond=doswitch() )
				}	 
				state("seeSudCopy") { //this:State
					action { //it:State
						Curmove="a"
						delay(StepTime)
						forward("modelChange", "modelChange(robot,$Curmove)" ,"resourcemodel" ) 
						delay(RotateTime)
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						solve("direction(D)","") //set resVar	
						Direction = getCurSol("D").toString() 
						println("--->$Direction")
					}
					 transition( edgeName="goto",targetState="seeSudCopy", cond=doswitchGuarded({(Direction!="downDir")}) )
					transition( edgeName="goto",targetState="gotToFridge", cond=doswitchGuarded({! (Direction!="downDir")}) )
				}	 
				state("seeSud") { //this:State
					action { //it:State
						Curmove="a"
						delay(StepTime)
						forward("modelChange", "modelChange(robot,$Curmove)" ,"resourcemodel" ) 
						itunibo.planner.moveUtils.doPlannedMove(myself ,Curmove )
						delay(RotateTime)
						forward("modelChange", "modelChange(robot,h)" ,"resourcemodel" ) 
						solve("direction(D)","") //set resVar	
						Direction = getCurSol("D").toString() 
						println("--->$Direction")
					}
					 transition( edgeName="goto",targetState="seeSud", cond=doswitchGuarded({(Direction!="downDir")}) )
					transition( edgeName="goto",targetState="endOfJob", cond=doswitchGuarded({! (Direction!="downDir")}) )
				}	 
				state("endOfJob") { //this:State
					action { //it:State
						println("END")
					}
				}	 
			}
		}
}
