/* Generated by AN DISI Unibo */ 
package it.unibo.ctxExplorer
import it.unibo.kactor.QakContext
import it.unibo.kactor.sysUtil
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
	QakContext.createContexts(
	        "localhost", this, "system1d.pl", "sysRules.pl"
	)
}

